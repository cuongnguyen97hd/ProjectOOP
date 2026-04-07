
import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class AttendanceManager {
    private static final String ATT_FILE = "attendance.txt";
    private static final int REQUIRED_DAYS = 26;
    private static final long BONUS = 1_000_000L;

    // cham cong 1 ngay cho 1 nhan vien 
    public static void markAttendance(Scanner sc) {
        System.out.print("Nhap IDNV: ");
        String id = sc.nextLine().trim();

        Employee emp = EmployeeManager.findById(id);
        if (emp == null) {
            System.out.println("Khong tim thay nhan vien!");
            return;
        }

        System.out.print("Nhap ngay cham cong (yyyy-mm-dd): ");
        String s = sc.nextLine().trim();
        LocalDate d;
        if (s.isEmpty())
            d = LocalDate.now();
        else {
            try { 
                d = LocalDate.parse(s);
            }
            catch (Exception ex)
            { 
                System.out.println("Sai dinh dang ngay!");
            return;
            }
        }
        // tao ban ghi 
        String record = id + "," + d.toString() + ",X";
        // kiem tra neu da co thi thong bao
        boolean exist = false;
        try (BufferedReader br = new BufferedReader(new FileReader(ATT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(record)) {
                    exist = true;
                    break;
                }
            }
        } catch (IOException ex) {
            System.out.println("Khong the doc file attendance!");
        }
        if (exist) {
            System.out.println("Da cham cong cho ngay nay roi.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ATT_FILE, true))) {
            bw.write(record);
            bw.newLine();
            System.out.println("Cham cong thanh cong!");
        } catch (IOException e) {
            System.out.println("Loi ghi file attendance!");
        }
    }
    // hien thi bang cham cong cua 1 thang cho 1 nhan vien
    public static void showAttendanceForMonth(Scanner sc) {
        System.out.print("Nhap IDNV: ");
        String id = sc.nextLine().trim();

        Employee emp = EmployeeManager.findById(id);
        if (emp == null) {
            System.out.println("Khong tim thay nhan vien!");
            return;
        }

        System.out.print("Nhap thang (yyyy-mm): ");
        String ym = sc.nextLine().trim();
        YearMonth ymObj;
        try {
            String[] parts = ym.split("-");
            ymObj = YearMonth.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } catch (Exception ex) {
            System.out.println("Sai dinh dang thang!");
            return;
        }

        // tao danh sach ngay trong thang
        int daysInMonth = ymObj.lengthOfMonth();
        System.out.println("Bang cham cong: " + emp.getName() + " - " + ymObj);
        System.out.println("Ngay | X/O");

        // du lieu cham cong 
        Set<String> presentDates = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ATT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 3 && p[0].equals(id)) {
                    LocalDate d = LocalDate.parse(p[1]);
                    if (d.getYear() == ymObj.getYear() && d.getMonthValue() == ymObj.getMonthValue()) {
                        presentDates.add(p[1]);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Khong the doc file attendance!");
        }
        // hien thi ket qua
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate d = ymObj.atDay(day);
            String status = presentDates.contains(d.toString()) ? "X" : "O";
            System.out.println(d + " | " + status);
        }
    }

    // tinh luong cho 1 nhan vien 1 thang
    public static void computeSalary(Scanner sc) {
        System.out.print("Nhap IDNV: ");
        String id = sc.nextLine().trim();

        Employee emp = EmployeeManager.findById(id);
        if (emp == null) {
            System.out.println("Khong tim thay nhan vien!");
            return;
        }

        System.out.print("Nhap thang (yyyy-mm): ");
        String ym = sc.nextLine().trim();
        YearMonth ymObj;
        try {
            String[] parts = ym.split("-");
            ymObj = YearMonth.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } catch (Exception ex) {
            System.out.println("Sai dinh dang thang!");
            return;
        }

        // dem so ngay X trong thang
        int presentCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(ATT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 3 && p[0].equals(id) && p[2].equalsIgnoreCase("X")) {
                    LocalDate d = LocalDate.parse(p[1]);
                    if (d.getYear() == ymObj.getYear() && d.getMonthValue() == ymObj.getMonthValue()) {
                        presentCount++;
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Khong the doc file attendance!");
         }

        long basic = emp.getBasicSalary();
        long salary;
        if (presentCount >= REQUIRED_DAYS) {
            salary = basic + BONUS;
        } else {
            // tinh = presentCount * (basic / 26)
            salary = Math.round((double) presentCount * ((double) basic / REQUIRED_DAYS));
        }

        System.out.println("Nhan vien: " + emp.getName());
        System.out.println("Thang: " + ymObj);
        System.out.println("So ngay lam: " + presentCount);
        System.out.println("Luong duoc tra (VND): " + salary);
    }
}