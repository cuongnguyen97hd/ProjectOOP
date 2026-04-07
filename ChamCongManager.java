import java.util.Scanner;

public class ChamCongManager {

    public static void menu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== CHAM CONG NHAN VIEN =====");
            System.out.println("1 Cham cong");
            System.out.println("2 Xem bang cham cong thang");
            System.out.println("3 Tinh luong");
            System.out.println("0 Quay lai");
            System.out.print("Chon: ");

        int choice;
        try {            
            choice = Integer.parseInt(sc.nextLine());
        } 
        catch (NumberFormatException e) {
            System.out.println("Vui long nhap mot so hop le!");
            continue;
        }
            switch (choice) {
                case 1:
                    AttendanceManager.markAttendance(sc);
                    break;
                case 2:
                    AttendanceManager.showAttendanceForMonth(sc);
                    break;
                case 3:
                    AttendanceManager.computeSalary(sc);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Sai lua chon!");
            }
        }
    }
}