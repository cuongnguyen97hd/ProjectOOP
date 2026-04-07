
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class StatisticsReportManager {

    // Thong ke chi tiet theo ngay
    public static void statisticsByDay(Scanner sc) {
        System.out.print("Nhap ngay (yyyy-mm-dd): ");
        String dateStr = sc.nextLine().trim();
        LocalDate searchDate;
        try {
            searchDate = LocalDate.parse(dateStr);
        } catch (Exception e) {
            System.out.println("Sai dinh dang ngay!");
            return;
        }

        ReportStatistics report = new ReportStatistics(searchDate);
        // Dem so luot kham va chi phi dich vu kham
        List<MedicalRecord> medicalRecords = MedicalRecordManager.loadMedicalRecords();
        for (MedicalRecord record : medicalRecords) {
            if (record.getExaminationDate().equals(searchDate)) {
                report.addExamination();
                report.addServiceCost(record.getServiceCost());
            }
        }

        // Dem chi phi thuoc
        List<Invoice> invoices = InvoiceManager.loadInvoices();
        for (Invoice invoice : invoices) {
            if (invoice.getInvoiceDate().equals(searchDate)) {
                report.addMedicineCost(invoice.getTotalAmount());
            }
        }
        report.calculateTotalRevenue();

        // Hien thi ket qua
        System.out.println("\n===== THONG KE THEO NGAY =====");
        System.out.println("Ngay: " + searchDate);
        System.out.println("------- -------");
        System.out.println("So luot kham: " + report.getExaminationCount());
        System.out.println("Chi phi dich vu kham: " + report.getServiceCost() + " VND");
        System.out.println("Chi phi thuoc: " + report.getMedicineCost() + " VND");
        System.out.println("------- -------");
        System.out.println("Tong doanh thu: " + report.getTotalRevenue() + " VND");
        System.out.println("=============================");
    }
    // ==================== THONG KE THEO THANG ====================

    // Thong ke chi tiet theo thang
    public static void statisticsByMonth(Scanner sc) {
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

            YearMonth yearMonth = YearMonth.of(year, month);

            ReportStatistics report = new ReportStatistics(yearMonth.atDay(1));

            // Dem so luot kham va chi phi dich vu kham
            List<MedicalRecord> medicalRecords = MedicalRecordManager.loadMedicalRecords();
            for (MedicalRecord record : medicalRecords) {
                if (record.getExaminationDate().getYear() == year &&
                        record.getExaminationDate().getMonthValue() == month) {
                    report.addExamination();
                    report.addServiceCost(record.getServiceCost());
                }
            }

            // Dem chi phi thuoc
            List<Invoice> invoices = InvoiceManager.loadInvoices();
            for (Invoice invoice : invoices) {
                if (invoice.getInvoiceDate().getYear() == year &&
                        invoice.getInvoiceDate().getMonthValue() == month) {
                    report.addMedicineCost(invoice.getTotalAmount());
                }
            }

            report.calculateTotalRevenue();

            // Hien thi ket qua
            System.out.println("\n===== THONG KE THEO THANG =====");
            System.out.println("Thang: " + monthStr);
            System.out.println("------- -------");
            System.out.println("So luot kham: " + report.getExaminationCount());
            System.out.println("Chi phi dich vu kham: " + report.getServiceCost() + " VND");
            System.out.println("Chi phi thuoc: " + report.getMedicineCost() + " VND");
            System.out.println("------- -------");
            System.out.println("Tong doanh thu: " + report.getTotalRevenue() + " VND");
            
            if (report.getExaminationCount() > 0) {
                System.out.println("Doanh thu trung binh/kham: " + 
                        (report.getTotalRevenue() / report.getExaminationCount()) + " VND");
            }
            System.out.println("=============================");

        } catch (Exception e) {
            System.out.println("Sai dinh dang!");
        }
    }
    // ==================== THONG KE TONG QUAN ====================

    // Thong ke tong quan he thong
    public static void overallStatistics() {
        // Thong ke ho so kham
        List<MedicalRecord> medicalRecords = MedicalRecordManager.loadMedicalRecords();
        long totalServiceCost = 0;
        for (MedicalRecord record : medicalRecords) {
            totalServiceCost += record.getServiceCost();
        }

        // Thong ke don thuoc
        List<Invoice> invoices = InvoiceManager.loadInvoices();
        long totalMedicineCost = 0;
        for (Invoice invoice : invoices) {
            totalMedicineCost += invoice.getTotalAmount();
        }

        // Thong ke benh nhan
        List<Patient> patients = PatientManager.loadPatients();

        // Thong ke lich kham
        List<Appointment> appointments = AppointmentManager.loadAppointments();

        long totalRevenue = totalServiceCost + totalMedicineCost;

        System.out.println("\n===== THONG KE TONG QUAN HE THONG =====");
        System.out.println("So benh nhan: " + patients.size());
        System.out.println("So luot kham: " + medicalRecords.size());
        System.out.println("So lich kham: " + appointments.size());
        System.out.println("So hoa don: " + invoices.size());
        System.out.println("------- -------");
        System.out.println("Tong chi phi dich vu kham: " + totalServiceCost + " VND");
        System.out.println("Tong chi phi thuoc: " + totalMedicineCost + " VND");
        System.out.println("------- -------");
        System.out.println("Tong doanh thu: " + totalRevenue + " VND");
        
        if (medicalRecords.size() > 0) {
            System.out.println("Doanh thu trung binh/kham: " + 
                    (totalRevenue / medicalRecords.size()) + " VND");
        }
        System.out.println("=============================");
    }

    // ==================== MENU ====================

    public static void menu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== THONG KE BAO CAO =====");
            System.out.println("1 Thong ke theo ngay");
            System.out.println("2 Thong ke theo thang");
            System.out.println("3 Thong ke tong quan");
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
                    statisticsByDay(sc);
                    break;
                case 2:
                    statisticsByMonth(sc);
                    break;
                case 3:
                    overallStatistics();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lua chon khong hop le!");
            }
        }
    }
}