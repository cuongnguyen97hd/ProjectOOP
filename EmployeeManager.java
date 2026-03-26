// EmployeeManager.java
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class EmployeeManager {
    private static final String EMP_FILE = "employees.txt";
    private static final String SESSION_FILE = "session.txt";

    // load tat ca nhan vien tu file
    public static List<Employee> loadEmployees() {
        List<Employee> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(EMP_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 6) {
                    String id = p[0];
                    String name = p[1];
                    LocalDate sd = LocalDate.parse(p[2]);
                    String position = p[3];
                    String phone = p[4];
                    long basic = Long.parseLong(p[5]);
                    list.add(new Employee(id, name, sd, position, phone, basic));
                }
            }
        } catch (IOException e) {
            // file co the chua ton tai -> tra ve rong
        }
        return list;
    }

    // luu list vao file (ghi de)
    public static void saveEmployees(List<Employee> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EMP_FILE))) {
            for (Employee emp : list) {
                bw.write(emp.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi employees.txt");
        }
    }

    // lay ngay session hien tai (ngay dang nhap), neu khong co tra ve LocalDate.now()
    public static LocalDate readSessionDate() {
        try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line = br.readLine();
            if (line != null && !line.isBlank()) {
                return LocalDate.parse(line.trim());
            }
        } catch (Exception e) {
            // ignore
        }
        return LocalDate.now();
    }

    // them nhan vien -> ngay bat dau lay tu session
    public static void addEmployee(Scanner sc) {
        List<Employee> list = loadEmployees();

        System.out.print("Nhap IDNV: ");
        String id = sc.nextLine().trim();

        // kiem tra trung ID
        for (Employee e : list) {
            if (e.getId().equals(id)) {
                System.out.println("ID da ton tai!");
                return;
            }
        }

        System.out.print("Nhap ten: ");
        String name = sc.nextLine().trim();

        LocalDate startDate = readSessionDate(); // lay tu session
        System.out.println("Ngay bat dau lam viec duoc gan: " + startDate);

        System.out.print("Nhap vi tri (NhanVien/BacSi): ");
        String pos = sc.nextLine().trim();

        System.out.print("Nhap so dien thoai: ");
        String phone = sc.nextLine().trim();

        System.out.print("Nhap luong co ban (VND - neu lam du 26 ngay): ");
        long basic;
        try {
            basic = Long.parseLong(sc.nextLine().trim());
        } catch (NumberFormatException ex) {
            System.out.println("Gia tri luong khong hop le.");
            return;
        }

        Employee emp = new Employee(id, name, startDate, pos, phone, basic);
        list.add(emp);
        saveEmployees(list);
        System.out.println("Them nhan vien thanh cong!");
    }

    // cap nhat (ten, vi tri, sdt, luong) - khong cap nhat ngay bat dau
    public static void updateEmployee(Scanner sc) {
        List<Employee> list = loadEmployees();
        System.out.print("Nhap IDNV can cap nhat: ");
        String id = sc.nextLine().trim();

        boolean found = false;
        for (Employee e : list) {
            if (e.getId().equals(id)) {
                found = true;
                System.out.println("Thong tin hien tai: " + e.toString());
                System.out.print("Nhap ten moi (enter de giu): ");
                String name = sc.nextLine().trim();
                if (!name.isEmpty()) e.setName(name);

                System.out.print("Nhap vi tri moi (NhanVien/BacSi): ");
                String pos = sc.nextLine().trim();
                if (!pos.isEmpty()) e.setPosition(pos);

                System.out.print("Nhap sdt moi (enter de giu): ");
                String phone = sc.nextLine().trim();
                if (!phone.isEmpty()) e.setPhone(phone);

                System.out.print("Nhap luong co ban moi: ");
                String bs = sc.nextLine().trim();
                if (!bs.isEmpty()) {
                    try { e.setBasicSalary(Long.parseLong(bs)); }
                    catch (NumberFormatException ex) { System.out.println("Luong khong hop le -> giu gia tri cu."); }
                }

                saveEmployees(list);
                System.out.println("Cap nhat thanh cong!");
                break;
            }
        }
        if (!found) System.out.println("Khong tim thay nhan vien!");
    }

    // xoa
    public static void deleteEmployee(Scanner sc) {
        List<Employee> list = loadEmployees();
        System.out.print("Nhap IDNV can xoa: ");
        String id = sc.nextLine().trim();

        boolean removed = list.removeIf(e -> e.getId().equals(id));
        if (removed) {
            saveEmployees(list);
            // xoa cung cac ban ghi cham cong neu can (khong bat buoc)
            removeAttendanceOfEmployee(id);
            System.out.println("Xoa thanh cong!");
        } else {
            System.out.println("Khong tim thay!");
        }
    }

    public static void listEmployees() {
        List<Employee> list = loadEmployees();
        System.out.println("===== DANH SACH NHAN VIEN =====");
        for (Employee e : list) {
            System.out.println(e.getId() + " | " + e.getName() + " | Bat dau: " + e.getStartDate()
                    + " | Vi tri: " + e.getPosition() + " | SDT: " + e.getPhone()
                    + " | LuongCoBan: " + e.getBasicSalary());
        }
    }

    private static void removeAttendanceOfEmployee(String id) {
        String ATT = "attendance.txt";
        List<String> keep = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ATT))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(id + ",")) keep.add(line);
            }
        } catch (IOException ex) { /* ignore */ }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ATT))) {
            for (String s : keep) { bw.write(s); bw.newLine(); }
        } catch (IOException ex) { /* ignore */ }
    }

    // helper: tim employee theo ID
    public static Employee findById(String id) {
        for (Employee e : loadEmployees()) {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    public static void menu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== QUAN LY NHAN VIEN =====");
            System.out.println("1 Them nhan vien");
            System.out.println("2 Sua nhan vien");
            System.out.println("3 Xoa nhan vien");
            System.out.println("4 Danh sach nhan vien");
            System.out.println("0 Quay lai");
            System.out.print("Chon: ");

        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Vui long nhap mot so hop le!");
            continue;
        }
            switch (choice) {
                case 1:
                    addEmployee(sc);
                    break;
                case 2:
                    updateEmployee(sc);
                    break;
                case 3:
                    deleteEmployee(sc);
                    break;
                case 4:
                    listEmployees();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Sai lua chon!");
            }
        }
    }
}