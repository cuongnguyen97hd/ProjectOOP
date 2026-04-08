import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ===== LOGIN =====
        String role = LoginSystem.login();

        if (role == null) {
            System.out.println("Dang nhap that bai!");
            return;
        }

        // ===== MENU CHINH =====
        while (true) {
            System.out.println("\n===== HE THONG QUAN LY PHONG KHAM =====");

            // Menu theo role
            if (role.equalsIgnoreCase("Admin")) {
                System.out.println("1 Quan ly tai khoan");
                System.out.println("2 Quan ly nhan vien");
                System.out.println("3 Cham cong nhan vien");
                System.out.println("4 Quan ly benh nhan");
                System.out.println("5 Quan ly lich kham");
                System.out.println("6 Quan ly ho so kham benh");
                System.out.println("7 Quan ly thuoc");
                System.out.println("8 Thanh toan don thuoc");
                System.out.println("9 Quan ly nha phan phoi thuoc");
                System.out.println("10 Xem lich su kham benh");
                System.out.println("11 Thong ke bao cao");
            }

            if (role.equalsIgnoreCase("LeTan")) {
                System.out.println("2 Quan ly nhan vien");
                System.out.println("3 Cham cong nhan vien");
                System.out.println("4 Quan ly benh nhan");
                System.out.println("5 Quan ly lich kham");
                System.out.println("6 Quan ly ho so kham benh");
                System.out.println("7 Quan ly thuoc");
                System.out.println("8 Thanh toan don thuoc");
                System.out.println("9 Quan ly nha phan phoi thuoc");
                System.out.println("10 Xem lich su kham benh");
                System.out.println("11 Thong ke bao cao");
            }

            if (role.equalsIgnoreCase("BacSi")) {
                System.out.println("3 Cham cong nhan vien");
                System.out.println("4 Quan ly benh nhan");
                System.out.println("5 Quan ly lich kham");
                System.out.println("6 Quan ly ho so kham benh");
                System.out.println("7 Quan ly thuoc");
                System.out.println("8 Thanh toan don thuoc");
                System.out.println("9 Quan ly nha phan phoi thuoc");
                System.out.println("10 Xem lich su kham benh");
                System.out.println("11 Thong ke bao cao");
            }

            System.out.println("0 Dang xuat");
            System.out.print("Chon chuc nang: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Nhap sai!");
                continue;
            }

            switch (choice) {
                // ===== ADMIN =====
                case 1:
                    if (role.equalsIgnoreCase("Admin")) {
                        AdminAccountManager.menu();
                    } else {
                        System.out.println("Ban khong co quyen!");
                    }
                    break;

                case 2:
                    if (role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("LeTan")) {
                        EmployeeManager.menu();
                    } else {
                        System.out.println("Ban khong co quyen!");
                    }
                    break;

                case 3:
                    ChamCongManager.menu();
                    break;

                case 4:
                    PatientManager.menu();
                    break;

                case 5:
                    AppointmentManager.menu();
                    break;

                case 6:
                    MedicalRecordManager.menu();
                    break;

                case 7:
                    MedicineManager.menu();
                    break;

                case 8:
                    InvoiceManager.menu();
                    break;

                case 9:
                    DistributorManager.menu();
                    break;

                case 10:
                    ExaminationHistoryManager.menu();
                    break;

                case 11:
                    StatisticsReportManager.menu();
                    break;

                case 0:
                    LoginSystem.logout();
                    System.out.println("Da dang xuat!");
                    return;

                default:
                    System.out.println("Lua chon khong hop le!");
            }
        }
    }
}