// PatientManager.java
import java.io.*;
import java.util.*;

public class PatientManager {
    private static final String PATIENT_FILE = "patients.txt";

    // ==================== LOAD & SAVE ====================
    
    // Load tat ca benh nhan tu file
    public static List<Patient> loadPatients() {
        List<Patient> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PATIENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 5) {
                    String id = p[0];
                    String name = p[1];
                    int age = Integer.parseInt(p[2]);
                    String phone = p[3];
                    String address = p[4];
                    list.add(new Patient(id, name, age, phone, address));
                }
            }
        } catch (IOException e) {
            // File chua ton tai - tra ve list rong
        } catch (NumberFormatException e) {
            System.out.println("Loi dinh dang tuoi trong file!");
        }
        return list;
    }

    // Luu danh sach benh nhan vao file
    public static void savePatients(List<Patient> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATIENT_FILE))) {
            for (Patient patient : list) {
                bw.write(patient.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi file patients.txt");
        }
    }

    // ==================== THEM BENH NHAN ====================
    
    public static void addPatient(Scanner sc) {
        List<Patient> list = loadPatients();

        // Nhap ma benh nhan
        System.out.print("Nhap IDBN (ma benh nhan): ");
        String id = sc.nextLine().trim();

        // Kiem tra ID da ton tai
        for (Patient p : list) {
            if (p.getId().equals(id)) {
                System.out.println("ID da ton tai!");
                return;
            }
        }

        // Kiem tra ID khong duoc trong
        if (id.isEmpty()) {
            System.out.println("ID khong duoc de trong!");
            return;
        }

        // Nhap ten benh nhan
        System.out.print("Nhap ten benh nhan: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Ten khong duoc de trong!");
            return;
        }

        // Nhap tuoi
        System.out.print("Nhap tuoi: ");
        int age;
        try {
            age = Integer.parseInt(sc.nextLine().trim());
            if (age < 0 || age > 150) {
                System.out.println("Tuoi phai tu 0 den 150!");
                return;
            }
        } catch (NumberFormatException ex) {
            System.out.println("Tuoi phai la so!");
            return;
        }

        // Nhap so dien thoai
        System.out.print("Nhap so dien thoai: ");
        String phone = sc.nextLine().trim();
        if (!isValidPhone(phone)) {
            System.out.println("So dien thoai khong hop le!");
            return;
        }

        // Nhap dia chi
        System.out.print("Nhap dia chi: ");
        String address = sc.nextLine().trim();
        if (address.isEmpty()) {
            System.out.println("Dia chi khong duoc de trong!");
            return;
        }

        // Tao doi tuong va them vao list
        Patient patient = new Patient(id, name, age, phone, address);
        list.add(patient);
        savePatients(list);
        System.out.println("Them benh nhan thanh cong!");
    }

    // ==================== CAP NHAT BENH NHAN ====================
    
    public static void updatePatient(Scanner sc) {
        List<Patient> list = loadPatients();
        
        System.out.print("Nhap IDBN can cap nhat: ");
        String id = sc.nextLine().trim();

        boolean found = false;
        for (Patient p : list) {
            if (p.getId().equals(id)) {
                found = true;
                System.out.println("Thong tin hien tai: " + p.getName() + 
                                   " | Tuoi: " + p.getAge() + 
                                   " | SDT: " + p.getPhone() + 
                                   " | Dia chi: " + p.getAddress());

                // Cap nhat ten
                System.out.print("Nhap ten moi (enter de giu): ");
                String name = sc.nextLine().trim();
                if (!name.isEmpty()) {
                    p.setName(name);
                }

                // Cap nhat tuoi
                System.out.print("Nhap tuoi moi (enter de giu): ");
                String ageStr = sc.nextLine().trim();
                if (!ageStr.isEmpty()) {
                    try {
                        int age = Integer.parseInt(ageStr);
                        if (age < 0 || age > 150) {
                            System.out.println("Tuoi khong hop le - giu gia tri cu!");
                        } else {
                            p.setAge(age);
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("Tuoi phai la so - giu gia tri cu!");
                    }
                }

                // Cap nhat so dien thoai
                System.out.print("Nhap SDT moi (enter de giu): ");
                String phone = sc.nextLine().trim();
                if (!phone.isEmpty()) {
                    if (isValidPhone(phone)) {
                        p.setPhone(phone);
                    } else {
                        System.out.println("So dien thoai khong hop le - giu gia tri cu!");
                    }
                }

                // Cap nhat dia chi
                System.out.print("Nhap dia chi moi (enter de giu): ");
                String address = sc.nextLine().trim();
                if (!address.isEmpty()) {
                    p.setAddress(address);
                }

                savePatients(list);
                System.out.println("Cap nhat thanh cong!");
                break;
            }
        }
        
        if (!found) {
            System.out.println("Khong tim thay benh nhan!");
        }
    }

    // ==================== XOA BENH NHAN ====================
    
    public static void deletePatient(Scanner sc) {
        List<Patient> list = loadPatients();
        
        System.out.print("Nhap IDBN can xoa: ");
        String id = sc.nextLine().trim();

        boolean removed = list.removeIf(p -> p.getId().equals(id));
        
        if (removed) {
            savePatients(list);
            System.out.println("Xoa benh nhan thanh cong!");
        } else {
            System.out.println("Khong tim thay benh nhan!");
        }
    }

    // ==================== LIET KE BENH NHAN ====================
    
    public static void listPatients() {
        List<Patient> list = loadPatients();
        
        if (list.isEmpty()) {
            System.out.println("Chua co benh nhan nao!");
            return;
        }

        System.out.println("\n===== DANH SACH BENH NHAN =====");
        System.out.println("IDBN | Ten | Tuoi | SDT | Dia chi");
        System.out.println("-----+-----+------+-----+--------");
        
        for (Patient p : list) {
            System.out.println(p.getId() + " | " + p.getName() + " | " + p.getAge() + 
                             " | " + p.getPhone() + " | " + p.getAddress());
        }
        System.out.println("===== HET DANH SACH =====");
    }

    // ==================== TIM KIEM BENH NHAN ====================
    
    // Tim benh nhan theo ID
    public static Patient findById(String id) {
        for (Patient p : loadPatients()) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null;
    }

    // Tim benh nhan theo ten
    public static void searchPatientByName(Scanner sc) {
        System.out.print("Nhap ten benh nhan can tim: ");
        String searchName = sc.nextLine().trim().toLowerCase();

        List<Patient> results = new ArrayList<>();
        for (Patient p : loadPatients()) {
            if (p.getName().toLowerCase().contains(searchName)) {
                results.add(p);
            }
        }

        if (results.isEmpty()) {
            System.out.println("Khong tim thay benh nhan!");
        } else {
            System.out.println("Tim thay " + results.size() + " benh nhan:");
            System.out.println("IDBN | Ten | Tuoi | SDT | Dia chi");
            System.out.println("-----+-----+------+-----+--------");
            for (Patient p : results) {
                System.out.println(p.getId() + " | " + p.getName() + " | " + p.getAge() + 
                                 " | " + p.getPhone() + " | " + p.getAddress());
            }
        }
    }

    // Tim benh nhan theo id hoac ten (tu yeu cau menu)
    public static void searchPatient(Scanner sc) {
        while (true) {
            System.out.println("\n===== TIM KIEM BENH NHAN =====");
            System.out.println("1 Tim theo ID");
            System.out.println("2 Tim theo Ten");
            System.out.println("0 Quay lai");
            System.out.print("Chon: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("Nhap IDBN can tim: ");
                    String id = sc.nextLine().trim();
                    Patient p = findById(id);
                    if (p == null) {
                        System.out.println("Khong tim thay benh nhan!");
                    } else {
                        System.out.println("Tim thay 1 benh nhan:");
                        System.out.println("IDBN | Ten | Tuoi | SDT | Dia chi");
                        System.out.println("-----+-----+------+-----+--------");
                        System.out.println(p.getId() + " | " + p.getName() + " | " + p.getAge() + 
                                         " | " + p.getPhone() + " | " + p.getAddress());
                    }
                    break;
                case "2":
                    searchPatientByName(sc);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Lua chon khong hop le!");
            }
        }
    }

    // ==================== HELPER METHODS ====================
    
    // Kiem tra so dien thoai hop le (10-11 so)
    private static boolean isValidPhone(String phone) {
        if (phone.isEmpty()) {
            return false;
        }
        return phone.matches("\\d{10,11}");
    }

    // ==================== MENU ====================
    
    public static void menu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== QUAN LY BENH NHAN =====");
            System.out.println("1 Them benh nhan");
            System.out.println("2 Sua benh nhan");
            System.out.println("3 Xoa benh nhan");
            System.out.println("4 Danh sach benh nhan");
            System.out.println("5 Tim kiem benh nhan");
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
                    addPatient(sc);
                    break;
                case 2:
                    updatePatient(sc);
                    break;
                case 3:
                    deletePatient(sc);
                    break;
                case 4:
                    listPatients();
                    break;
                case 5:
                    searchPatient(sc);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lua chon khong hop le!");
            }
        }
    }
}