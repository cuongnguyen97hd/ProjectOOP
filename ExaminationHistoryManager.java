
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ExaminationHistoryManager {
    private static final String HISTORY_FILE = "examination_history.txt";

    // Load tat ca lich su kham tu file
    public static List<ExaminationHistory> loadHistory() {
        List<ExaminationHistory> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(HISTORY_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 6) {
                    LocalDate examinationDate = LocalDate.parse(p[0]);
                    String patientId = p[1];
                    String patientName = p[2];
                    String doctorId = p[3];
                    String doctorName = p[4];
                    String diagnosis = p[5];
                    String medicinesStr = p.length > 6 ? p[6] : "";

                    ExaminationHistory history = new ExaminationHistory(examinationDate, patientId,
                            patientName, doctorId, doctorName, diagnosis);

                    // Parse danh sach thuoc
                    if (!medicinesStr.isEmpty()) {
                        String[] medicines = medicinesStr.split(";");
                        for (String medicine : medicines) {
                            history.addMedicine(medicine.trim());
                        }
                    }
                    list.add(history);
                }
            }
        } catch (IOException e) {
            System.out.println("Loi doc file lich su kham!");
        }
        return list;
    }

    // Luu danh sach lich su kham vao file
    public static void saveHistory(List<ExaminationHistory> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            for (ExaminationHistory history : list) {
                bw.write(history.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi file examination_history.txt");
        }
    }

    // Tao hoac cap nhat lich su tu ho so kham benh
    public static void createHistoryFromMedicalRecord(MedicalRecord record) {
        List<ExaminationHistory> list = loadHistory();

        // Kiem tra da co lich su cho ngay va benh nhan nay khong
        boolean exists = false;
        for (ExaminationHistory h : list) {
            if (h.getExaminationDate().equals(record.getExaminationDate()) &&
                    h.getPatientId().equals(record.getPatientId())) {
                exists = true;// Da co lich su, chi can cap nhat chan doan
                break;
            }
        }
        if (!exists) {
            // Tao lich su moi
            ExaminationHistory history = new ExaminationHistory(
                    record.getExaminationDate(),
                    record.getPatientId(),
                    record.getPatientName(),
                    record.getDoctorId(),
                    record.getDoctorName(),
                    record.getDiagnosis()
            );
            list.add(history);
            saveHistory(list);
        }
    }

    // Cap nhat danh sach thuoc cho lich su kham
    public static void addMedicinesToHistory(String patientId, LocalDate examinationDate, 
                                            String medicineName) {
        List<ExaminationHistory> list = loadHistory();
        for (ExaminationHistory h : list) {
            if (h.getPatientId().equals(patientId) && 
                h.getExaminationDate().equals(examinationDate)) {
                h.addMedicine(medicineName);
                saveHistory(list);
                break;
            }
        }
    }

    // XEM LICH SU THEO IDBN

    public static void viewHistoryByPatient(Scanner sc) {
        System.out.print("Nhap IDBN benh nhan: ");
        String patientId = sc.nextLine().trim();

        List<ExaminationHistory> results = new ArrayList<>();
        for (ExaminationHistory history : loadHistory()) {
            if (history.getPatientId().equals(patientId)) {
                results.add(history);
            }
        }
        if (results.isEmpty()) {
            System.out.println("Benh nhan nay khong co lich su kham!");
            return;
        }
        // Sap xep theo ngay (tang dan)
        results.sort((h1, h2) -> h1.getExaminationDate().compareTo(h2.getExaminationDate()));

        System.out.println("\n===== LICH SU KHAM BENH =====");
        System.out.println("Benh nhan: " + results.get(0).getPatientName() + 
                " (" + patientId + ")");
        System.out.println("Tong so lan kham: " + results.size());
        System.out.println("--------");

        for (int i = 0; i < results.size(); i++) {
            ExaminationHistory history = results.get(i);
            System.out.println((i + 1) + ". Lan kham lan thu " + (i + 1));
            System.out.println("   Ngay kham: " + history.getExaminationDate());
            System.out.println("   Bac si: " + history.getDoctorName() + 
                    " (" + history.getDoctorId() + ")");
            System.out.println("   Chan doan: " + history.getDiagnosis());
            
            if (!history.getMedicines().isEmpty()) {
                System.out.println("   Cac thuoc da dung:");
                for (String medicine : history.getMedicines()) {
                    System.out.println("      - " + medicine);
                }
            } else {
                System.out.println("   Cac thuoc da dung: Khong co");
            }
            System.out.println();
        }
        System.out.println("=============================");
    }

    // XEM TOAN BO LICH SU
    public static void viewAllHistory() {
        List<ExaminationHistory> list = loadHistory();

        if (list.isEmpty()) {
            System.out.println("Chua co lich su kham nao!");
            return;
        }
        // Sap xep theo benh nhan, sau do theo ngay
        list.sort((h1, h2) -> {
            int patientCompare = h1.getPatientId().compareTo(h2.getPatientId());
            if (patientCompare != 0)
                return patientCompare;
            return h1.getExaminationDate().compareTo(h2.getExaminationDate());
        });

        System.out.println("\n===== TOAN BO LICH SU KHAM BENH =====");
        System.out.println("Ngay | IDBN | Ten BN | IDNV | Ten BS | Chan doan | Thuoc");
        System.out.println("----+------+-------+------+--------+----------+------");

        String currentPatient = "";
        int examinationCount = 0;

        for (ExaminationHistory history : list) {
            if (!currentPatient.equals(history.getPatientId())) {
                if (!currentPatient.isEmpty()) {
                    System.out.println("(Tong " + examinationCount + " lan kham)\n");
                }
                currentPatient = history.getPatientId();
                examinationCount = 0;
            }
            examinationCount++;

            // Danh sach thuoc
            String medicinesStr = "";
            if (!history.getMedicines().isEmpty()) {
                medicinesStr = String.join("; ", history.getMedicines());
            } else {
                medicinesStr = "Khong co";
            }

            System.out.println(history.getExaminationDate() + " | " +
                    history.getPatientId() + " | " + history.getPatientName() + " | " +
                    history.getDoctorId() + " | " + history.getDoctorName() + " | " +
                    history.getDiagnosis() + " | " + medicinesStr);
        }
        if (examinationCount > 0) {
            System.out.println("(Tong " + examinationCount + " lan kham)");
        }

        System.out.println("===== HET DANH SACH =====");
    }

    //XOA LICH SU THEO IDBN 
    public static void deleteHistoryByPatient(Scanner sc) {
        System.out.print("Nhap IDBN benh nhan: ");
        String patientId = sc.nextLine().trim();

        List<ExaminationHistory> list = loadHistory();
        
        // Kiem tra ton tai
        String patientName = "";
        int count = 0;
        for (ExaminationHistory h : list) {
            if (h.getPatientId().equals(patientId)) {
                patientName = h.getPatientName();
                count++;
            }
        }
        if (count == 0) {
            System.out.println("Benh nhan nay khong co lich su kham!");
            return;
        }
        // Xac nhan truoc khi xoa
        System.out.println("Xoa " + count + " ban ghi lich su kham cua benh nhan " + patientName);
        System.out.print("Ban chac chan muon xoa? (Y/N): ");
        String confirm = sc.nextLine().trim().toUpperCase();
        
        if(!confirm.equals("Y")) {
            System.out.println("Da xoa !");
            return;
        }
        int removed = 0;
        List<ExaminationHistory> newList = new ArrayList<>();
        for (ExaminationHistory h : list) {
            if (!h.getPatientId().equals(patientId)) {
                newList.add(h);
            } else {
                removed++;
            }
        }
        saveHistory(newList);
        System.out.println("Xoa thanh cong! Da xoa " + removed + " ban ghi.");
    }

    // Thong ke so lan kham cua benh nhan
    public static void statisticsByPatient(Scanner sc) {
        System.out.print("Nhap IDBN benh nhan: ");
        String patientId = sc.nextLine().trim();

        List<ExaminationHistory> results = new ArrayList<>();
        String patientName = "";

        for (ExaminationHistory history : loadHistory()) {
            if (history.getPatientId().equals(patientId)) {
                results.add(history);
                patientName = history.getPatientName();
            }
        }

        if (results.isEmpty()) {
            System.out.println("Benh nhan nay khong co lich su kham!");
            return;
        }

        // Dem so lan kham va so bac si
        Set<String> doctors = new HashSet<>();
        Set<String> medicines = new HashSet<>();

        for (ExaminationHistory h : results) {
            doctors.add(h.getDoctorId());
            medicines.addAll(h.getMedicines());
        }

        System.out.println("Thong ke lich su kham benh cua benh nhan " + patientName + ":");
        System.out.println("Tong so lan kham: " + results.size());
        System.out.println("So bac si khac nhau: " + doctors.size());
        System.out.println("So loai thuoc da dung: " + medicines.size());
        
        if (!medicines.isEmpty()) {
            System.out.println("Cac loai thuoc da dung:");
            for (String medicine : medicines) {
                System.out.println("  - " + medicine);
            }
        }
    }

    // Dong bo lich su tu ho so kham va don thuoc
    public static void syncHistoryWithMedicalRecords() {
        List<MedicalRecord> medicalRecords = MedicalRecordManager.loadMedicalRecords();
        
        for (MedicalRecord record : medicalRecords) {
            createHistoryFromMedicalRecord(record);
        }
    }

    // Dong bo lich su tu don thuoc
    public static void syncHistoryWithInvoices() {
        List<Invoice> invoices = InvoiceManager.loadInvoices();
        
        for (Invoice invoice : invoices) {
            for (MedicineDetail detail : invoice.getDetails()) {
                addMedicinesToHistory(invoice.getPatientId(), invoice.getInvoiceDate(), 
                        detail.getMedicineName());
            }
        }
    }

    public static void menu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== XEM LICH SU KHAM BENH =====");
            System.out.println("1 Xem lich su theo IDBN");
            System.out.println("2 Xem toan bo lich su kham");
            System.out.println("3 Xoa lich su theo IDBN");
            System.out.println("4 Thong ke lich su benh nhan");
            System.out.println("5 Dong bo du lieu");
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
                    viewHistoryByPatient(sc);
                    break;
                case 2:
                    viewAllHistory();
                    break;
                case 3:
                    deleteHistoryByPatient(sc);
                    break;
                case 4:
                    statisticsByPatient(sc);
                    break;
                case 5:
                    System.out.println("Dang dong bo du lieu...");
                    syncHistoryWithMedicalRecords();
                    syncHistoryWithInvoices();
                    System.out.println("Dong bo du lieu thanh cong!");
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lua chon khong hop le!");
            }
        }
    }
}