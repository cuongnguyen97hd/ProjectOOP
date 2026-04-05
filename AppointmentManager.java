// AppointmentManager.java
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class AppointmentManager {
    private static final String APPOINTMENT_FILE = "appointments.txt";
    private static final String SESSION_FILE = "session.txt";

    // ==================== LOAD & SAVE ====================
    
    // Load tat ca lich kham tu file
    public static List<Appointment> loadAppointments() {
        List<Appointment> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 7) {
                    String stt = p[0];
                    LocalDate appointmentDate = LocalDate.parse(p[1]);
                    String patientId = p[2];
                    String patientName = p[3];
                    String doctorId = p[4];
                    String doctorName = p[5];
                    String status = p[6];
                    
                    Appointment apt = new Appointment(stt, appointmentDate, patientId, 
                                                     patientName, doctorId, doctorName);
                    apt.setStatus(status);
                    list.add(apt);
                }
            }
        } catch (IOException e) {
            // File chua ton tai
        } catch (Exception e) {
            System.out.println("Loi doc file lich kham!");
        }
        return list;
    }

    // Luu danh sach lich kham vao file
    public static void saveAppointments(List<Appointment> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(APPOINTMENT_FILE))) {
            for (Appointment apt : list) {
                bw.write(apt.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi file appointments.txt");
        }
    }

    // ==================== DOC NGAY PHIEN LAM VIEC ====================
    
    // Lay ngay phien lam viec tu file session
    private static LocalDate getSessionDate() {
        try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line = br.readLine();
            if (line != null && !line.isBlank()) {
                return LocalDate.parse(line.trim());
            }
        } catch (Exception e) {
            // Neu khong doc duoc, tra ve ngay hien tai
        }
        return LocalDate.now();
    }

    // ==================== TAO SO THU TU ====================
    
    // Tao so thu tu tu dong theo nam-thang-ngay
    private static String generateStt() {
        List<Appointment> list = loadAppointments();
        
        if (list.isEmpty()) {
            return "L001";
        }
        
        // Lay so cuoi cung va tang len 1
        String lastStt = list.get(list.size() - 1).getStt();
        try {
            int number = Integer.parseInt(lastStt.substring(1));
            number++;
            return "L" + String.format("%03d", number);
        } catch (Exception e) {
            return "L" + (list.size() + 1);
        }
    }

    // ==================== THEM LICH KHAM ====================
    
    public static void addAppointment(Scanner sc) {
        List<Appointment> list = loadAppointments();

        // Lay ngay phien lam viec
        LocalDate sessionDate = getSessionDate();
        System.out.println("Ngay phien lam viec hien tai: " + sessionDate);

        // Nhap ngay kham
        System.out.print("Nhap ngay kham (yyyy-mm-dd): ");
        String dateStr = sc.nextLine().trim();
        LocalDate appointmentDate;
        try {
            appointmentDate = LocalDate.parse(dateStr);
            
            // Kiem tra ngay kham >= ngay phien lam viec
            if (appointmentDate.isBefore(sessionDate)) {
                System.out.println("Ngay kham phai >= ngay phien lam viec (" + sessionDate + ")!");
                return;
            }
        } catch (Exception e) {
            System.out.println("Sai dinh dang ngay!");
            return;
        }

        // Nhap IDBN
        System.out.print("Nhap IDBN: ");
        String patientId = sc.nextLine().trim();
        
        // Kiem tra benh nhan ton tai
        Patient patient = PatientManager.findById(patientId);
        if (patient == null) {
            System.out.println("Benh nhan khong ton tai!");
            return;
        }
        String patientName = patient.getName();

        // Nhap IDNV bac si
        System.out.print("Nhap IDNV bac si: ");
        String doctorId = sc.nextLine().trim();
        
        // Kiem tra bac si ton tai
        Employee doctor = EmployeeManager.findById(doctorId);
        if (doctor == null) {
            System.out.println("Bac si khong ton tai!");
            return;
        }
        
        // Kiem tra xem co phai la bac si khong
        if (!doctor.getPosition().equalsIgnoreCase("BacSi")) {
            System.out.println("Nhan vien nay khong phai la bac si!");
            return;
        }
        String doctorName = doctor.getName();

        // Kiem tra xem da co lich kham cho ngay va bac si nay khong
        for (Appointment apt : list) {
            if (apt.getAppointmentDate().equals(appointmentDate) && 
                apt.getDoctorId().equals(doctorId) &&
                apt.getStatus().equalsIgnoreCase("Cho")) {
                System.out.println("Bac si nay da co lich kham vao ngay nay!");
                return;
            }
        }

        // Tao lich kham moi
        String stt = generateStt();
        Appointment apt = new Appointment(stt, appointmentDate, patientId, 
                                         patientName, doctorId, doctorName);
        list.add(apt);
        saveAppointments(list);
        System.out.println("Them lich kham thanh cong! So thu tu: " + stt);
    }

    // ==================== CAP NHAT LICH KHAM ====================
    
    public static void updateAppointment(Scanner sc) {
        List<Appointment> list = loadAppointments();
        
        System.out.print("Nhap so thu tu lich kham can cap nhat: ");
        String stt = sc.nextLine().trim();

        boolean found = false;
        for (Appointment apt : list) {
            if (apt.getStt().equals(stt)) {
                found = true;
                System.out.println("Thong tin lich kham hien tai:");
                System.out.println("Ngay: " + apt.getAppointmentDate());
                System.out.println("Benh nhan: " + apt.getPatientName() + " (" + apt.getPatientId() + ")");
                System.out.println("Bac si: " + apt.getDoctorName() + " (" + apt.getDoctorId() + ")");
                System.out.println("Trang thai: " + apt.getStatus());

                // Cap nhat trang thai
                System.out.print("Nhap trang thai moi (Cho/Da kham/Huy) (enter de giu): ");
                String status = sc.nextLine().trim();
                if (!status.isEmpty()) {
                    if (status.equalsIgnoreCase("Cho") || 
                        status.equalsIgnoreCase("Da kham") || 
                        status.equalsIgnoreCase("Huy")) {
                        apt.setStatus(status);
                        System.out.println("Cap nhat thanh cong!");
                    } else {
                        System.out.println("Trang thai khong hop le!");
                    }
                }

                saveAppointments(list);
                break;
            }
        }
        
        if (!found) {
            System.out.println("Khong tim thay lich kham!");
        }
    }

    // ==================== XOA LICH KHAM ====================
    
    public static void deleteAppointment(Scanner sc) {
        List<Appointment> list = loadAppointments();
        
        System.out.print("Nhap so thu tu lich kham can xoa: ");
        String stt = sc.nextLine().trim();

        boolean removed = list.removeIf(apt -> apt.getStt().equals(stt));
        
        if (removed) {
            saveAppointments(list);
            System.out.println("Xoa lich kham thanh cong!");
        } else {
            System.out.println("Khong tim thay lich kham!");
        }
    }

    // ==================== LIET KE LICH KHAM ====================
    
    public static void listAppointments() {
        List<Appointment> list = loadAppointments();
        
        if (list.isEmpty()) {
            System.out.println("Chua co lich kham nao!");
            return;
        }

        System.out.println("\n===== DANH SACH LICH KHAM =====");
        System.out.println("STT | Ngay | IDBN | Ten BN | IDNV | Ten BS | Trang thai");
        System.out.println("----+------+------+--------+------+--------+-----------");
        
        for (Appointment apt : list) {
            System.out.println(apt.getStt() + " | " + apt.getAppointmentDate() + " | " + 
                             apt.getPatientId() + " | " + apt.getPatientName() + " | " + 
                             apt.getDoctorId() + " | " + apt.getDoctorName() + " | " + 
                             apt.getStatus());
        }
        System.out.println("===== HET DANH SACH =====");
    }

    // ==================== MENU ====================
    
    public static void menu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== QUAN LY LICH KHAM =====");
            System.out.println("1 Them lich kham");
            System.out.println("2 Cap nhat lich kham");
            System.out.println("3 Xoa lich kham");
            System.out.println("4 Danh sach lich kham");
            System.out.println("0 Quay lai");
            System.out.print("Chon: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Nhap sai!");
                continue;
            }

            switch (choice) {
                case 1:
                    addAppointment(sc);
                    break;
                case 2:
                    updateAppointment(sc);
                    break;
                case 3:
                    deleteAppointment(sc);
                    break;
                case 4:
                    listAppointments();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lua chon khong hop le!");
            }
        }
    }
}