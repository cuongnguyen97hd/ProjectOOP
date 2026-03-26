import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class LoginSystem {

    private static final String ACCOUNT_FILE = "accounts.txt";
    private static final String SESSION_FILE = "session.txt";

    public static String login() {
        Scanner sc = new Scanner(System.in);

        System.out.println("===== DANG NHAP HE THONG =====");

        System.out.print("Nhap username: ");
        String username = sc.nextLine();

        System.out.print("Nhap password: ");
        String password = sc.nextLine();

        if (!checkAdmin(username, password)) {
            System.out.println("Sai tai khoan hoac khong co quyen Admin!");
            return null;
        }

        LocalDate lastDate = readLastSession();

        if (lastDate != null) {
            System.out.println("Phien truoc: " + lastDate);
        }

        while (true) {
            System.out.print("Nhap ngay lam viec (yyyy-mm-dd): ");
            String input = sc.nextLine();

            try {
                LocalDate newDate = LocalDate.parse(input);

                if (lastDate != null && newDate.isBefore(lastDate)) {
                    System.out.println("Ngay khong hop le! Phai >= ngay truoc.");
                    continue;
                }

                saveSession(newDate);
                System.out.println("Dang nhap thanh cong!");
                return "Admin"; // chi co Admin moi dang nhap duoc

            } catch (Exception e) {
                System.out.println("Sai dinh dang ngay!");
            }
        }
    }

    private static boolean checkAdmin(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(ACCOUNT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    if (parts[0].equals(username) &&
                        parts[1].equals(password) &&
                        parts[2].equalsIgnoreCase("Admin")) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Loi doc file tai khoan!");
        }
        return false;
    }

    private static LocalDate readLastSession() {
        try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line = br.readLine();
            if (line != null) {
                return LocalDate.parse(line);
            }
        } catch (Exception e) {
            // file co the chua ton tai
        }
        return null;
    }

    private static void saveSession(LocalDate date) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SESSION_FILE))) {
            bw.write(date.toString());
        } catch (IOException e) {
            System.out.println("Loi ghi session!");
        }
    }

    public static void logout() {
        System.out.println("Da dang xuat. Ket thuc phien lam viec.");
    }
}