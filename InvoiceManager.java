// InvoiceManager.java
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class InvoiceManager {
    private static final String INVOICE_FILE = "invoices.txt";



    // Load tat ca hoa don tu file
    public static List<Invoice> loadInvoices() {
        List<Invoice> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(INVOICE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String mhd = parts[0];
                    LocalDate invoiceDate = LocalDate.parse(parts[1]);
                    String patientId = parts[2];
                    String patientName = parts[3];
                    String detailsStr = parts[4];
                    long totalAmount = Long.parseLong(parts[5]);

                    Invoice invoice = new Invoice(mhd, invoiceDate, patientId, patientName);

                    // Parse chi tiet thuoc
                    if (!detailsStr.isEmpty()) {
                        String[] medicineDetails = detailsStr.split(";");
                        for (String detail : medicineDetails) {
                            String[] detailParts = detail.split(",");
                            if (detailParts.length >= 4) {
                                String medicineName = detailParts[0];
                                int quantity = Integer.parseInt(detailParts[1]);
                                long unitPrice = Long.parseLong(detailParts[2]);
                                invoice.addMedicineDetail(new MedicineDetail(medicineName, quantity, unitPrice));
                            }
                        }
                    }

                    list.add(invoice);
                }
            }
        } catch (IOException e) {
            // File chua ton tai
        } catch (Exception e) {
            System.out.println("Loi doc file hoa don!");
        }
        return list;
    }

    // Luu danh sach hoa don vao file
    public static void saveInvoices(List<Invoice> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(INVOICE_FILE))) {
            for (Invoice invoice : list) {
                bw.write(invoice.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi file invoices.txt");
        }
    }


    // Tao ma hoa don tu dong
    private static String generateInvoiceId() {
        List<Invoice> list = loadInvoices();

        if (list.isEmpty()) {
            return "MHD001";
        }

        // Lay so cuoi cung va tang len 1
        String lastId = list.get(list.size() - 1).getMhd();
        try {
            int number = Integer.parseInt(lastId.substring(3));
            number++;
            return "MHD" + String.format("%03d", number);
        } catch (Exception e) {
            return "MHD" + (list.size() + 1);
        }
    }


    public static void createInvoice(Scanner sc) {
        // Lay ngay phien lam viec
        LocalDate sessionDate = getSessionDate();

        // Nhap IDBN
        System.out.print("Nhap IDBN benh nhan: ");
        String patientId = sc.nextLine().trim();

        // Kiem tra benh nhan ton tai
        Patient patient = PatientManager.findById(patientId);
        if (patient == null) {
            System.out.println("Benh nhan khong ton tai!");
            return;
        }
        String patientName = patient.getName();

        // Tao hoa don moi
        String mhd = generateInvoiceId();
        Invoice invoice = new Invoice(mhd, sessionDate, patientId, patientName);

        System.out.println("Tao hoa don moi cho benh nhan: " + patientName);
        System.out.println("Ma hoa don: " + mhd);
        System.out.println("Ngay tao don: " + sessionDate);

        // Them chi tiet thuoc vao hoa don
        boolean addMore = true;
        while (addMore) {
            System.out.print("\nNhap ten thuoc: ");
            String medicineName = sc.nextLine().trim();

            // Kiem tra thuoc ton tai
            Medicine medicine = findMedicineByName(medicineName);
            if (medicine == null) {
                System.out.println("Thuoc khong ton tai!");
                continue;
            }

            // Nhap so luong
            System.out.print("Nhap so luong mua: ");
            int quantity;
            try {
                quantity = Integer.parseInt(sc.nextLine().trim());
                if (quantity <= 0) {
                    System.out.println("So luong phai lon hon 0!");
                    continue;
                }
                if (quantity > medicine.getQuantityRemaining()) {
                    System.out.println("So luong con lai khong du! (Con " +
                            medicine.getQuantityRemaining() + ")");
                    continue;
                }
            } catch (NumberFormatException ex) {
                System.out.println("So luong phai la so!");
                continue;
            }

            // Them chi tiet vao hoa don
            MedicineDetail detail = new MedicineDetail(medicineName, quantity, medicine.getPrice());
            invoice.addMedicineDetail(detail);
            System.out.println("Them thuoc thanh cong! Thanh tien: " +
                    (quantity * medicine.getPrice()) + " VND");

            // Cap nhat so luong da ban cua thuoc
            medicine.setQuantitySold(medicine.getQuantitySold() + quantity);
            List<Medicine> medicines = MedicineManager.loadMedicines();
            for (Medicine m : medicines) {
                if (m.getId().equals(medicine.getId())) {
                    m.setQuantitySold(medicine.getQuantitySold());
                    break;
                }
            }
            MedicineManager.saveMedicines(medicines);

            // Hoi co muon them thuoc khac khong
            System.out.print("Them thuoc khac? (Y/N): ");
            String choice = sc.nextLine().trim().toUpperCase();
            if (!choice.equals("Y")) {
                addMore = false;
            }
        }

        // Luu hoa don
        List<Invoice> invoices = loadInvoices();
        invoices.add(invoice);
        saveInvoices(invoices);

        System.out.println("\n===== HOA DON =====");
        System.out.println("Ma hoa don: " + invoice.getMhd());
        System.out.println("Ngay tao: " + invoice.getInvoiceDate());
        System.out.println("Benh nhan: " + invoice.getPatientName());
        System.out.println("------- Chi tiet thuoc -------");
        for (MedicineDetail detail : invoice.getDetails()) {
            System.out.println(detail.getMedicineName() + " x " + detail.getQuantity() +
                    " = " + detail.getTotalPrice() + " VND");
        }
        System.out.println("------- -------");
        System.out.println("Tong thanh toan: " + invoice.getTotalAmount() + " VND");
        System.out.println("Tao hoa don thanh cong!");
    }



    public static void viewInvoice(Scanner sc) {
        System.out.print("Nhap ma hoa don: ");
        String mhd = sc.nextLine().trim();

        for (Invoice invoice : loadInvoices()) {
            if (invoice.getMhd().equals(mhd)) {
                System.out.println("\n===== HOA DON =====");
                System.out.println("Ma hoa don: " + invoice.getMhd());
                System.out.println("Ngay tao: " + invoice.getInvoiceDate());
                System.out.println("Benh nhan: " + invoice.getPatientName() +
                        " (" + invoice.getPatientId() + ")");
                System.out.println("------- Chi tiet thuoc -------");
                for (int i = 0; i < invoice.getDetails().size(); i++) {
                    MedicineDetail detail = invoice.getDetails().get(i);
                    System.out.println((i + 1) + ". " + detail.getMedicineName() +
                            " x " + detail.getQuantity() +
                            " @ " + detail.getUnitPrice() +
                            " = " + detail.getTotalPrice() + " VND");
                }
                System.out.println("------- -------");
                System.out.println("Tong thanh toan: " + invoice.getTotalAmount() + " VND");
                System.out.println("================");
                return;
            }
        }
        System.out.println("Khong tim thay hoa don!");
    }

    // ==================== LIET KE HOA DON ====================

    public static void listInvoices() {
        List<Invoice> list = loadInvoices();

        if (list.isEmpty()) {
            System.out.println("Chua co hoa don nao!");
            return;
        }

        System.out.println("\n===== DANH SACH HOA DON =====");
        System.out.println("MHD | Ngay | IDBN | Ten BN | Thanh toan");
        System.out.println("----+------+------+--------+-----------");

        for (Invoice invoice : list) {
            System.out.println(invoice.getMhd() + " | " + invoice.getInvoiceDate() +
                    " | " + invoice.getPatientId() + " | " + invoice.getPatientName() +
                    " | " + invoice.getTotalAmount() + " VND");
        }
        System.out.println("===== HET DANH SACH =====");
    }



    // Loc hoa don theo benh nhan
    public static void filterByPatient(Scanner sc) {
        System.out.print("Nhap IDBN benh nhan: ");
        String patientId = sc.nextLine().trim();

        List<Invoice> results = new ArrayList<>();
        for (Invoice invoice : loadInvoices()) {
            if (invoice.getPatientId().equals(patientId)) {
                results.add(invoice);
            }
        }

        if (results.isEmpty()) {
            System.out.println("Benh nhan nay khong co hoa don nao!");
        } else {
            System.out.println("Hoa don cua benh nhan " +
                    (results.isEmpty() ? "" : results.get(0).getPatientName()) + ":");
            System.out.println("MHD | Ngay | Thanh toan");
            System.out.println("----+------+-----------");
            for (Invoice invoice : results) {
                System.out.println(invoice.getMhd() + " | " + invoice.getInvoiceDate() +
                        " | " + invoice.getTotalAmount() + " VND");
            }
        }
    }

    // Loc hoa don theo ngay
    public static void filterByDate(Scanner sc) {
        System.out.print("Nhap ngay (yyyy-mm-dd): ");
        String dateStr = sc.nextLine().trim();
        LocalDate searchDate;
        try {
            searchDate = LocalDate.parse(dateStr);
        } catch (Exception e) {
            System.out.println("Sai dinh dang ngay!");
            return;
        }

        List<Invoice> results = new ArrayList<>();
        for (Invoice invoice : loadInvoices()) {
            if (invoice.getInvoiceDate().equals(searchDate)) {
                results.add(invoice);
            }
        }

        if (results.isEmpty()) {
            System.out.println("Khong co hoa don nao trong ngay " + searchDate);
        } else {
            System.out.println("Hoa don trong ngay " + searchDate + ":");
            System.out.println("MHD | IDBN | Ten BN | Thanh toan");
            System.out.println("----+------+--------+-----------");
            for (Invoice invoice : results) {
                System.out.println(invoice.getMhd() + " | " + invoice.getPatientId() +
                        " | " + invoice.getPatientName() +
                        " | " + invoice.getTotalAmount() + " VND");
            }
        }
    }



    // Thong ke tong doanh thu
    public static void totalRevenue() {
        List<Invoice> list = loadInvoices();
        long totalRevenue = 0;
        int totalInvoices = 0;

        for (Invoice invoice : list) {
            totalRevenue += invoice.getTotalAmount();
            totalInvoices++;
        }

        System.out.println("Thong ke tong doanh thu ban thuoc:");
        System.out.println("Tong so hoa don: " + totalInvoices);
        System.out.println("Tong doanh thu: " + totalRevenue + " VND");
        if (totalInvoices > 0) {
            System.out.println("Doanh thu trung binh: " + (totalRevenue / totalInvoices) + " VND");
        }
    }

    // Thong ke doanh thu theo thang
    public static void revenueByMonth(Scanner sc) {
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

            long totalRevenue = 0;
            int invoiceCount = 0;

            for (Invoice invoice : loadInvoices()) {
                if (invoice.getInvoiceDate().getYear() == year &&
                        invoice.getInvoiceDate().getMonthValue() == month) {
                    totalRevenue += invoice.getTotalAmount();
                    invoiceCount++;
                }
            }

            if (invoiceCount == 0) {
                System.out.println("Khong co hoa don nao trong thang " + monthStr);
            } else {
                System.out.println("Thong ke doanh thu trong thang " + monthStr + ":");
                System.out.println("So hoa don: " + invoiceCount);
                System.out.println("Tong doanh thu: " + totalRevenue + " VND");
                System.out.println("Doanh thu trung binh: " + (totalRevenue / invoiceCount) + " VND");
            }
        } catch (Exception e) {
            System.out.println("Sai dinh dang!");
        }
    }


    // Tim thuoc theo ten
    private static Medicine findMedicineByName(String name) {
        for (Medicine medicine : MedicineManager.loadMedicines()) {
            if (medicine.getName().equalsIgnoreCase(name)) {
                return medicine;
            }
        }
        return null;
    }

    // Lay ngay phien lam viec
    private static LocalDate getSessionDate() {
        try (BufferedReader br = new BufferedReader(new FileReader("session.txt"))) {
            String line = br.readLine();
            if (line != null && !line.isBlank()) {
                return LocalDate.parse(line.trim());
            }
        } catch (Exception e) {
            // Neu khong doc duoc, tra ve ngay hien tai
        }
        return LocalDate.now();
    }



    public static void menu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== THANH TOAN DON THUOC =====");
            System.out.println("1 Tao hoa don moi");
            System.out.println("2 Xem hoa don");
            System.out.println("3 Danh sach hoa don");
            System.out.println("4 Loc hoa don theo benh nhan");
            System.out.println("5 Loc hoa don theo ngay");
            System.out.println("6 Thong ke tong doanh thu");
            System.out.println("7 Thong ke doanh thu theo thang");
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
                    createInvoice(sc);
                    break;
                case 2:
                    viewInvoice(sc);
                    break;
                case 3:
                    listInvoices();
                    break;
                case 4:
                    filterByPatient(sc);
                    break;
                case 5:
                    filterByDate(sc);
                    break;
                case 6:
                    totalRevenue();
                    break;
                case 7:
                    revenueByMonth(sc);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lua chon khong hop le!");
            }
        }
    }
}
