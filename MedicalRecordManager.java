// MedicalRecordManager.java
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class MedicalRecordManager {
    private static final String MEDICAL_RECORD_FILE = "medical_records.txt";
    private static final String SESSION_FILE = "session.txt";

    // ==================== LOAD & SAVE ====================

    // Load tat ca ho so kham benh tu file
    public static List<MedicalRecord> loadMedicalRecords() {
        List<MedicalRecord> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(MEDICAL_RECORD_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 8) {
                    String stt = p[0];
                    LocalDate examinationDate = LocalDate.parse(p[1]);
                    String patientId = p[2];
                    String patientName = p[3];
                    String doctorId = p[4];
                    String doctorName = p[5];
                    String diagnosis = p[6];
                    long serviceCost = Long.parseLong(p[7]);

                    MedicalRecord record = new MedicalRecord(stt, examinationDate, patientId,
                            patientName, doctorId, doctorName, diagnosis, serviceCost);
                    list.add(record);
                }
            }
        } catch (IOException e) {
            // File chua ton tai
        } catch (Exception e) {
            System.out.println("Loi doc file ho so kham benh!");
        }
        return list;
    }

    // Luu danh sach ho so kham benh vao file
    public static void saveMedicalRecords(List<MedicalRecord> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MEDICAL_RECORD_FILE))) {
            for (MedicalRecord record : list) {
                bw.write(record.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi file medical_records.txt");
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

    // Tao so thu tu tu dong
    private static String generateStt() {
        List<MedicalRecord> list = loadMedicalRecords();

        if (list.isEmpty()) {
            return "HS001";
        }

        // Lay so cuoi cung va tang len 1
        String lastStt = list.get(list.size() - 1).getStt();
        try {
            int number = Integer.parseInt(lastStt.substring(2));
            number++;
            return "HS" + String.format("%03d", number);
        } catch (Exception e) {
            return "HS" + (list.size() + 1);
        }
    }

    // ==================== THEM HO SO KHAM BENH ====================

    public static void addMedicalRecord(Scanner sc) {
        List<MedicalRecord> list = loadMedicalRecords();

        // Lay ngay phien lam viec
        LocalDate sessionDate = getSessionDate();

        // Nhap ngay kham
        System.out.print("Nhap ngay kham (yyyy-mm-dd): ");
        String dateStr = sc.nextLine().trim();
        LocalDate examinationDate;
        try {
            examinationDate = LocalDate.parse(dateStr);

            // Kiem tra ngay kham <= ngay phien lam viec
            if (examinationDate.isAfter(sessionDate)) {
                System.out.println("Ngay kham khong duoc vuot qua ngay phien lam viec (" + sessionDate + ")!");
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

        // Nhap chan doan benh
        System.out.print("Nhap chan doan benh: ");
        String diagnosis = sc.nextLine().trim();
        if (diagnosis.isEmpty()) {
            System.out.println("Chan doan benh khong duoc de trong!");
            return;
        }

        // Nhap chi phi dich vu
        System.out.print("Nhap chi phi dich vu kham (VND): ");
        long serviceCost;
        try {
            serviceCost = Long.parseLong(sc.nextLine().trim());
            if (serviceCost < 0) {
                System.out.println("Chi phi khong duoc am!");
                return;
            }
        } catch (NumberFormatException ex) {
            System.out.println("Chi phi phai la so!");
            return;
        }

        // Tao ho so kham benh moi
        String stt = generateStt();
        MedicalRecord record = new MedicalRecord(stt, examinationDate, patientId,
                patientName, doctorId, doctorName, diagnosis, serviceCost);
        list.add(record);
        saveMedicalRecords(list);
        System.out.println("Them ho so kham benh thanh cong! So thu tu: " + stt);
    }

    // ==================== CAP NHAT HO SO KHAM BENH ====================

    public static void updateMedicalRecord(Scanner sc) {
        List<MedicalRecord> list = loadMedicalRecords();

        System.out.print("Nhap so thu tu ho so kham benh can cap nhat: ");
        String stt = sc.nextLine().trim();

        boolean found = false;
        for (MedicalRecord record : list) {
            if (record.getStt().equals(stt)) {
                found = true;
                System.out.println("Thong tin ho so kham benh hien tai:");
                System.out.println("Ngay kham: " + record.getExaminationDate());
                System.out.println("Benh nhan: " + record.getPatientName() + " (" + record.getPatientId() + ")");
                System.out.println("Bac si: " + record.getDoctorName() + " (" + record.getDoctorId() + ")");
                System.out.println("Chan doan: " + record.getDiagnosis());
                System.out.println("Chi phi: " + record.getServiceCost() + " VND");

                // Cap nhat chan doan benh
                System.out.print("Nhap chan doan benh moi (enter de giu): ");
                String diagnosis = sc.nextLine().trim();
                if (!diagnosis.isEmpty()) {
                    record.setDiagnosis(diagnosis);
                }

                // Cap nhat chi phi dich vu
                System.out.print("Nhap chi phi dich vu moi (enter de giu): ");
                String costStr = sc.nextLine().trim();
                if (!costStr.isEmpty()) {
                    try {
                        long cost = Long.parseLong(costStr);
                        if (cost < 0) {
                            System.out.println("Chi phi khong duoc am - giu gia tri cu!");
                        } else {
                            record.setServiceCost(cost);
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("Chi phi phai la so - giu gia tri cu!");
                    }
                }

                saveMedicalRecords(list);
                System.out.println("Cap nhat thanh cong!");
                break;
            }
        }

        if (!found) {
            System.out.println("Khong tim thay ho so kham benh!");
        }
    }

    // ==================== XOA HO SO KHAM BENH ====================

    public static void deleteMedicalRecord(Scanner sc) {
        List<MedicalRecord> list = loadMedicalRecords();

        System.out.print("Nhap so thu tu ho so kham benh can xoa: ");
        String stt = sc.nextLine().trim();

        boolean removed = list.removeIf(record -> record.getStt().equals(stt));

        if (removed) {
            saveMedicalRecords(list);
            System.out.println("Xoa ho so kham benh thanh cong!");
        } else {
            System.out.println("Khong tim thay ho so kham benh!");
        }
    }

    // ==================== LIET KE HO SO KHAM BENH ====================

    public static void listMedicalRecords() {
        List<MedicalRecord> list = loadMedicalRecords();

        if (list.isEmpty()) {
            System.out.println("Chua co ho so kham benh nao!");
            return;
        }

        System.out.println("\n===== DANH SACH HO SO KHAM BENH =====");
        System.out.println("STT | Ngay | IDBN | Ten BN | IDNV | Ten BS | Chan doan | Chi phi");
        System.out.println("----+------+------+--------+------+--------+----------+--------");

        for (MedicalRecord record : list) {
            System.out.println(record.getStt() + " | " + record.getExaminationDate() + " | " +
                    record.getPatientId() + " | " + record.getPatientName() + " | " +
                    record.getDoctorId() + " | " + record.getDoctorName() + " | " +
                    record.getDiagnosis() + " | " + record.getServiceCost() + " VND");
        }
        System.out.println("===== HET DANH SACH =====");
    }

    // ==================== THONG KE ====================

    // Thong ke tong chi phi theo bac si
    public static void totalCostByDoctor(Scanner sc) {
        System.out.print("Nhap IDNV bac si: ");
        String doctorId = sc.nextLine().trim();

        long totalCost = 0;
        String doctorName = "";
        int recordCount = 0;

        for (MedicalRecord record : loadMedicalRecords()) {
            if (record.getDoctorId().equals(doctorId)) {
                totalCost += record.getServiceCost();
                doctorName = record.getDoctorName();
                recordCount++;
            }
        }

        if (recordCount == 0) {
            System.out.println("Bac si nay khong co ho so kham benh nao!");
        } else {
            System.out.println("Thong ke chi phi cho bac si " + doctorName + ":");
            System.out.println("So lan kham: " + recordCount);
            System.out.println("Tong chi phi: " + totalCost + " VND");
        }
    }

    // Thong ke tong chi phi theo thang
    public static void totalCostByMonth(Scanner sc) {
        System.out.print("Nhap thang (yyyy-mm): ");
        String monthStr = sc.nextLine().trim();

        try {
            String[] parts = monthStr.split("-");
            if (parts.length != 2) {
                System.out.println("Sai dinh dang thang!");
                return;
            }
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            long totalCost = 0;
            int recordCount = 0;

            for (MedicalRecord record : loadMedicalRecords()) {
                if (record.getExaminationDate().getYear() == year &&
                        record.getExaminationDate().getMonthValue() == month) {
                    totalCost += record.getServiceCost();
                    recordCount++;
                }
            }

            if (recordCount == 0) {
                System.out.println("Khong co ho so kham benh nao trong thang " + monthStr);
            } else {
                System.out.println("Thong ke doanh thu trong thang " + monthStr + ":");
                System.out.println("So ho so: " + recordCount);
                System.out.println("Tong thu nhap: " + totalCost + " VND");
            }
        } catch (Exception e) {
            System.out.println("Sai dinh dang!");
        }
    }

    // ==================== MENU ====================

    public static void menu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== QUAN LY HO SO KHAM BENH =====");
            System.out.println("1 Them ho so kham benh");
            System.out.println("2 Cap nhat ho so kham benh");
            System.out.println("3 Xoa ho so kham benh");
            System.out.println("4 Danh sach ho so kham benh");
            System.out.println("5 Thong ke chi phi bac si");
            System.out.println("6 Thong ke doanh thu theo thang");
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
                    addMedicalRecord(sc);
                    break;
                case 2:
                    updateMedicalRecord(sc);
                    break;
                case 3:
                    deleteMedicalRecord(sc);
                    break;
                case 4:
                    listMedicalRecords();
                    break;
                case 5:
                    totalCostByDoctor(sc);
                    break;
                case 6:
                    totalCostByMonth(sc);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lua chon khong hop le!");
            }
        }
    }
}