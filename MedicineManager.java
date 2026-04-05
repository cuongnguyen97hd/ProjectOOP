// MedicineManager.java
import java.io.*;
import java.util.*;

public class MedicineManager {
    private static final String MEDICINE_FILE = "medicines.txt";


    // Load tat ca thuoc tu file
    public static List<Medicine> loadMedicines() {
        List<Medicine> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 6) {
                    String id = p[0];
                    String name = p[1];
                    long price = Long.parseLong(p[2]);
                    int quantity = Integer.parseInt(p[3]);
                    int quantitySold = Integer.parseInt(p[4]);
                    String distributor = p[5];

                    Medicine medicine = new Medicine(id, name, price, quantity, distributor);
                    medicine.setQuantitySold(quantitySold);
                    list.add(medicine);
                }
            }
        } catch (IOException e) {
            // File chua ton tai
        } catch (Exception e) {
            System.out.println("Loi doc file thuoc!");
        }
        return list;
    }

    // Luu danh sach thuoc vao file
    public static void saveMedicines(List<Medicine> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MEDICINE_FILE))) {
            for (Medicine medicine : list) {
                bw.write(medicine.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi ghi file medicines.txt");
        }
    }



    // Tao ma thuoc tu dong
    private static String generateMedicineId() {
        List<Medicine> list = loadMedicines();

        if (list.isEmpty()) {
            return "T001";
        }

        // Lay so cuoi cung va tang len 1
        String lastId = list.get(list.size() - 1).getId();
        try {
            int number = Integer.parseInt(lastId.substring(1));
            number++;
            return "T" + String.format("%03d", number);
        } catch (Exception e) {
            return "T" + (list.size() + 1);
        }
    }



    public static void addMedicine(Scanner sc) {
        List<Medicine> list = loadMedicines();

        // Nhap ten thuoc
        System.out.print("Nhap ten thuoc: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Ten thuoc khong duoc de trong!");
            return;
        }

        // Kiem tra trung ten thuoc
        for (Medicine m : list) {
            if (m.getName().equalsIgnoreCase(name)) {
                System.out.println("Ten thuoc da ton tai!");
                return;
            }
        }

        // Nhap gia thuoc
        System.out.print("Nhap gia thuoc (VND): ");
        long price;
        try {
            price = Long.parseLong(sc.nextLine().trim());
            if (price <= 0) {
                System.out.println("Gia thuoc phai lon hon 0!");
                return;
            }
        } catch (NumberFormatException ex) {
            System.out.println("Gia thuoc phai la so!");
            return;
        }

        // Nhap so luong
        System.out.print("Nhap so luong: ");
        int quantity;
        try {
            quantity = Integer.parseInt(sc.nextLine().trim());
            if (quantity < 0) {
                System.out.println("So luong khong duoc am!");
                return;
            }
        } catch (NumberFormatException ex) {
            System.out.println("So luong phai la so!");
            return;
        }

        // Nhap ten nha phan phoi
        System.out.print("Nhap ten nha phan phoi: ");
        String distributorName = sc.nextLine().trim();
        Distributor distributor = DistributorManager.findByName(distributorName);
        if (distributor == null) {
            System.out.println("Ten nha phan phoi khong ton tai!");
            return;
        }
        if(!distributor.getName().equalsIgnoreCase(distributorName)) {
            System.out.println("Ten nha phan phoi khong ton tai!");
            return;
        }
        // Tao thuoc moi
        String id = generateMedicineId();
        Medicine medicine = new Medicine(id, name, price, quantity, distributorName);
        list.add(medicine);
        saveMedicines(list);
        DistributorManager.updateMedicineImported(distributorName, quantity);
        System.out.println("Them thuoc thanh cong!");
    }



    public static void updateMedicine(Scanner sc) {
        List<Medicine> list = loadMedicines();

        System.out.print("Nhap ma thuoc can cap nhat: ");
        String id = sc.nextLine().trim();

        boolean found = false;
        for (Medicine medicine : list) {
            if (medicine.getId().equals(id)) {
                found = true;
                System.out.println("Thong tin thuoc hien tai:");
                System.out.println("Ten: " + medicine.getName());
                System.out.println("Gia: " + medicine.getPrice() + " VND");
                System.out.println("So luong: " + medicine.getQuantity());
                System.out.println("So luong da ban: " + medicine.getQuantitySold());
                System.out.println("So luong con lai: " + medicine.getQuantityRemaining());
                System.out.println("Nha phan phoi: " + medicine.getDistributor());

                // Cap nhat ten thuoc
                System.out.print("Nhap ten moi (enter de giu): ");
                String name = sc.nextLine().trim();
                if (!name.isEmpty()) {
                    // Kiem tra trung ten
                    boolean duplicate = false;
                    for (Medicine m : list) {
                        if (!m.getId().equals(id) && m.getName().equalsIgnoreCase(name)) {
                            duplicate = true;
                            break;
                        }
                    }
                    if (duplicate) {
                        System.out.println("Ten thuoc da ton tai - giu gia tri cu!");
                    } else {
                        medicine.setName(name);
                    }
                }

                // Cap nhat gia thuoc
                System.out.print("Nhap gia moi (enter de giu): ");
                String priceStr = sc.nextLine().trim();
                if (!priceStr.isEmpty()) {
                    try {
                        long price = Long.parseLong(priceStr);
                        if (price <= 0) {
                            System.out.println("Gia phai lon hon 0 - giu gia tri cu!");
                        } else {
                            medicine.setPrice(price);
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("Gia phai la so - giu gia tri cu!");
                    }
                }

                // Cap nhat so luong
                System.out.print("Nhap so luong moi (enter de giu): ");
                String quantityStr = sc.nextLine().trim();
                if (!quantityStr.isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        if (quantity < medicine.getQuantitySold()) {
                            System.out.println("So luong khong duoc nho hon so luong da ban (" +
                                    medicine.getQuantitySold() + ") - giu gia tri cu!");
                        } else if (quantity < 0) {
                            System.out.println("So luong khong duoc am - giu gia tri cu!");
                        } else {
                            medicine.setQuantity(quantity);
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("So luong phai la so - giu gia tri cu!");
                    }
                }

                // Cap nhat so luong da ban
                System.out.print("Nhap so luong da ban moi (enter de giu): ");
                String soldStr = sc.nextLine().trim();
                if (!soldStr.isEmpty()) {
                    try {
                        int sold = Integer.parseInt(soldStr);
                        if (sold < 0) {
                            System.out.println("So luong da ban khong duoc am - giu gia tri cu!");
                        } else if (sold > medicine.getQuantity()) {
                            System.out.println("So luong da ban khong duoc lon hon so luong co (" +
                                    medicine.getQuantity() + ") - giu gia tri cu!");
                        } else {
                            medicine.setQuantitySold(sold);
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("So luong da ban phai la so - giu gia tri cu!");
                    }
                }

                // Cap nhat nha phan phoi
                System.out.print("Nhap nha phan phoi moi (enter de giu): ");
                String distributor = sc.nextLine().trim();
                if (!distributor.isEmpty()) {
                    medicine.setDistributor(distributor);
                }

                saveMedicines(list);
                System.out.println("Cap nhat thanh cong!");
                break;
            }
        }

        if (!found) {
            System.out.println("Khong tim thay thuoc!");
        }
    }



    public static void deleteMedicine(Scanner sc) {
        List<Medicine> list = loadMedicines();

        System.out.print("Nhap ma thuoc can xoa: ");
        String id = sc.nextLine().trim();

        boolean removed = list.removeIf(medicine -> medicine.getId().equals(id));

        if (removed) {
            saveMedicines(list);
            System.out.println("Xoa thuoc thanh cong!");
        } else {
            System.out.println("Khong tim thay thuoc!");
        }
    }



    public static void listMedicines() {
        List<Medicine> list = loadMedicines();

        if (list.isEmpty()) {
            System.out.println("Chua co thuoc nao!");
            return;
        }

        System.out.println("\n===== DANH SACH THUOC =====");
        System.out.println("Ma | Ten | Gia | SL | SL Ban | SL Con | Nha PP");
        System.out.println("---+-----+-----+----+--------+--------+------");

        for (Medicine medicine : list) {
            System.out.println(medicine.getId() + " | " + medicine.getName() + " | " +
                    medicine.getPrice() + " | " + medicine.getQuantity() + " | " +
                    medicine.getQuantitySold() + " | " + medicine.getQuantityRemaining() +
                    " | " + medicine.getDistributor());
        }
        System.out.println("===== HET DANH SACH =====");
    }


    public static void searchMedicine(Scanner sc) {
        System.out.print("Nhap ten thuoc can tim: ");
        String searchName = sc.nextLine().trim().toLowerCase();

        List<Medicine> results = new ArrayList<>();
        for (Medicine medicine : loadMedicines()) {
            if (medicine.getName().toLowerCase().contains(searchName)) {
                results.add(medicine);
            }
        }

        if (results.isEmpty()) {
            System.out.println("Khong tim thay thuoc!");
        } else {
            System.out.println("Tim thay " + results.size() + " thuoc:");
            System.out.println("Ma | Ten | Gia | SL | SL Ban | SL Con | Nha PP");
            System.out.println("---+-----+-----+----+--------+--------+------");
            for (Medicine medicine : results) {
                System.out.println(medicine.getId() + " | " + medicine.getName() + " | " +
                        medicine.getPrice() + " | " + medicine.getQuantity() + " | " +
                        medicine.getQuantitySold() + " | " + medicine.getQuantityRemaining() +
                        " | " + medicine.getDistributor());
            }
        }
    }



    public static Medicine findById(String id) {
        for (Medicine medicine : loadMedicines()) {
            if (medicine.getId().equals(id)) {
                return medicine;
            }
        }
        return null;
    }



    // Thong ke thuoc con it (duoi 10)
    public static void reportLowStock() {
        List<Medicine> list = loadMedicines();
        List<Medicine> lowStock = new ArrayList<>();

        for (Medicine medicine : list) {
            if (medicine.getQuantityRemaining() < 10) {
                lowStock.add(medicine);
            }
        }

        if (lowStock.isEmpty()) {
            System.out.println("Khong co thuoc nao can nhap hang!");
        } else {
            System.out.println("Danh sach thuoc can nhap hang (con < 10):");
            System.out.println("Ma | Ten | SL Con | Nha PP");
            System.out.println("---+-----+--------+------");
            for (Medicine medicine : lowStock) {
                System.out.println(medicine.getId() + " | " + medicine.getName() + " | " +
                        medicine.getQuantityRemaining() + " | " + medicine.getDistributor());
            }
        }
    }

    // Thong ke tong chi phi thuoc
    public static void totalMedicineValue() {
        List<Medicine> list = loadMedicines();
        long totalValue = 0;
        int totalQuantity = 0;

        for (Medicine medicine : list) {
            totalValue += medicine.getPrice() * medicine.getQuantity();
            totalQuantity += medicine.getQuantity();
        }

        System.out.println("Thong ke tong chi phi thuoc:");
        System.out.println("Tong so loai thuoc: " + list.size());
        System.out.println("Tong so luong thuoc: " + totalQuantity);
        System.out.println("Tong chi phi: " + totalValue + " VND");
    }




    public static void menu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== QUAN LY THUOC =====");
            System.out.println("1 Them thuoc");
            System.out.println("2 Cap nhat thuoc");
            System.out.println("3 Xoa thuoc");
            System.out.println("4 Danh sach thuoc");
            System.out.println("5 Tim kiem thuoc");
            System.out.println("6 Thong ke thuoc can nhap hang");
            System.out.println("7 Thong ke tong chi phi thuoc");
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
                    addMedicine(sc);
                    break;
                case 2:
                    updateMedicine(sc);
                    break;
                case 3:
                    deleteMedicine(sc);
                    break;
                case 4:
                    listMedicines();
                    break;
                case 5:
                    searchMedicine(sc);
                    break;
                case 6:
                    reportLowStock();
                    break;
                case 7:
                    totalMedicineValue();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lua chon khong hop le!");
            }
        }
    }
}
