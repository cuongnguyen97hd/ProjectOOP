import java.util.*;
import java.io.*;


public class DistributorManager{
    private static final String DISTRIBUTOR_FILE = "distributors.txt";

    // doc file danh sach distributors
    public static List<Distributor> loadDistributors(){
        List<Distributor> list = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(DISTRIBUTOR_FILE))){
            String line;
            while((line = br.readLine()) != null){
                String[] p = line.split(",");
                if(p.length >= 5){
                    String stt = p[0];
                    String name = p[1];
                    String phone = p[2];
                    String address = p[3];
                    int totalMedicineImported = Integer.parseInt(p[4]);

                    Distributor distributor = new Distributor(stt, name, phone, address);
                    distributor.setTotalMedicineImported(totalMedicineImported);
                    list.add(distributor);
                }
            }
        }
        catch(IOException e){
            //File chua ton tai
        }
        catch(Exception e){
            System.out.println("Loi doc file nha phan phoi");
        }
        return list;
    }

    //ghi vao file txt
    public static void saveDistributors(List<Distributor> list){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(DISTRIBUTOR_FILE))){
            for(Distributor distributor : list){
                bw.write(distributor.toString());
                bw.newLine();
            }
        }
        catch(IOException e){
            System.out.println("Loi ghi file distributor.txt");
        }
    }

    //tao so thu tu
    private static String generateStt(){
        List<Distributor> list = loadDistributors();
        if(list.isEmpty()){
            return "NPP001";
        }

        String lastStt = list.get(list.size() - 1).getStt();
        try{
            int number = Integer.parseInt(lastStt.substring(3));
            number++;
            return "NPP" + String.format("%03d", number);
        }
        catch(Exception e){
            return "NPP" + String.format("%03d", list.size() + 1);
        }
    }

    //them nha phan phoi
    public static void addDistributor(Scanner sc){
        List<Distributor> list = loadDistributors();

        System.out.println("Nhap ten nha phan phoi: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Ten nha phan phoi khong duoc de trong!");
            return;
        }

        for (Distributor d : list) {
            if (d.getName().equalsIgnoreCase(name)) {
                System.out.println("Ten nha phan phoi da ton tai!");
                return;
            }
        }

        System.out.print("Nhap so dien thoai: ");
        String phone = sc.nextLine().trim();
        if (!isValidPhone(phone)) {
            System.out.println("So dien thoai khong hop le!");
            return;
        }


        System.out.print("Nhap dia chi: ");
        String address = sc.nextLine().trim();
        if (address.isEmpty()) {
            System.out.println("Dia chi khong duoc de trong!");
            return;
        }


        String stt = generateStt();
        Distributor distributor = new Distributor(stt, name, phone, address);
        list.add(distributor);
        saveDistributors(list);
        System.out.println("Them nha phan phoi thanh cong! So thu tu: " + stt);
    }

    //cap nhat npp
    public static void updateDistributor(Scanner sc) {
        List<Distributor> list = loadDistributors();

        System.out.print("Nhap so thu tu nha phan phoi can cap nhat: ");
        String stt = sc.nextLine().trim();

        boolean found = false;
        for (Distributor distributor : list) {
            if (distributor.getStt().equals(stt)) {
                found = true;
                System.out.println("Thong tin nha phan phoi hien tai:");
                System.out.println("Ten: " + distributor.getName());
                System.out.println("SDT: " + distributor.getPhone());
                System.out.println("Dia chi: " + distributor.getAddress());
                System.out.println("Tong so luong thuoc da nhap: " +
                        distributor.getTotalMedicineImported());

                // Cap nhat ten
                System.out.print("Nhap ten moi (enter de giu): ");
                String name = sc.nextLine().trim();
                if (!name.isEmpty()) {

                    boolean duplicate = false;
                    for (Distributor d : list) {
                        if (!d.getStt().equals(stt) && d.getName().equalsIgnoreCase(name)) {
                            duplicate = true;
                            break;
                        }
                    }
                    if (duplicate) {
                        System.out.println("Ten nha phan phoi da ton tai - giu gia tri cu!");
                    } else {
                        distributor.setName(name);
                    }
                }


                System.out.print("Nhap so dien thoai moi (enter de giu): ");
                String phone = sc.nextLine().trim();
                if (!phone.isEmpty()) {
                    if (isValidPhone(phone)) {
                        distributor.setPhone(phone);
                    } else {
                        System.out.println("So dien thoai khong hop le - giu gia tri cu!");
                    }
                }


                System.out.print("Nhap dia chi moi (enter de giu): ");
                String address = sc.nextLine().trim();
                if (!address.isEmpty()) {
                    distributor.setAddress(address);
                }

                saveDistributors(list);
                System.out.println("Cap nhat thanh cong!");
                break;
            }
        }

        if (!found) {
            System.out.println("Khong tim thay nha phan phoi!");
        }
    }

    //Xóa npp
    public static void deleteDistributor(Scanner sc){
        List<Distributor> list = loadDistributors();
        System.out.println("Nhap so thu tu nha phan phoi can xoa: ");
        String stt = sc.nextLine().trim();

        String distributorName = "";
        for(Distributor d : list){
            if(d.getStt().equals(stt)){
                distributorName = d.getName();
                break;
            }
        }

        if(distributorName.isEmpty()){
            System.out.println("Khong tim thay nha phan phoi");
            return;
        }

        List<Medicine> medicines = MedicineManager.loadMedicines();
        boolean isInUse = false;
        for (Medicine m : medicines) {
            if (m.getDistributor().equalsIgnoreCase(distributorName)) {
                isInUse = true;
                break;
            }
        }

        if (isInUse) {
            System.out.println("Khong the xoa! Nha phan phoi nay dang duoc su dung.");
            return;
        }


        boolean removed = list.removeIf(d -> d.getStt().equals(stt));

        if (removed) {
            saveDistributors(list);
            System.out.println("Xoa nha phan phoi thanh cong!");
        } else {
            System.out.println("Khong tim thay nha phan phoi!");
        }
    }


    //liet ke npp
    public static void listDistributors() {
        List<Distributor> list = loadDistributors();

        if (list.isEmpty()) {
            System.out.println("Chua co nha phan phoi nao!");
            return;
        }

        System.out.println("\n===== DANH SACH NHA PHAN PHOI =====");
        System.out.println("STT | Ten | SDT | Dia chi | Tong SL Thuoc");
        System.out.println("----+-----+-----+---------+--------------");

        for (Distributor distributor : list) {
            System.out.println(distributor.getStt() + " | " + distributor.getName() +
                    " | " + distributor.getPhone() + " | " + distributor.getAddress() +
                    " | " + distributor.getTotalMedicineImported());
        }
        System.out.println("===== HET DANH SACH =====");
    }



    public static void searchDistributor(Scanner sc) {
        System.out.print("Nhap ten nha phan phoi can tim: ");
        String searchName = sc.nextLine().trim().toLowerCase();

        List<Distributor> results = new ArrayList<>();
        for (Distributor distributor : loadDistributors()) {
            if (distributor.getName().toLowerCase().contains(searchName)) {
                results.add(distributor);
            }
        }

        if (results.isEmpty()) {
            System.out.println("Khong tim thay nha phan phoi!");
        } else {
            System.out.println("Tim thay " + results.size() + " nha phan phoi:");
            System.out.println("STT | Ten | SDT | Dia chi | Tong SL Thuoc");
            System.out.println("----+-----+-----+---------+--------------");
            for (Distributor distributor : results) {
                System.out.println(distributor.getStt() + " | " + distributor.getName() +
                        " | " + distributor.getPhone() + " | " + distributor.getAddress() +
                        " | " + distributor.getTotalMedicineImported());
            }
        }
    }


    //tim npp theo ten
    public static Distributor findByName(String name) {
        for (Distributor distributor : loadDistributors()) {
            if (distributor.getName().equalsIgnoreCase(name)) {
                return distributor;
            }
        }
        return null;
    }



    public static void updateMedicineImported(String distributorName, int quantity) {
        List<Distributor> list = loadDistributors();

        for (Distributor distributor : list) {
            if (distributor.getName().equalsIgnoreCase(distributorName)) {
                distributor.addMedicineImported(quantity);
                saveDistributors(list);
                break;
            }
        }
    }




    private static boolean isValidPhone(String phone) {
        if (phone.isEmpty()) {
            return false;
        }
        return phone.matches("\\d{10,11}");
    }



    public static void menu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== QUAN LY NHA PHAN PHOI THUOC =====");
            System.out.println("1 Them nha phan phoi");
            System.out.println("2 Cap nhat nha phan phoi");
            System.out.println("3 Xoa nha phan phoi");
            System.out.println("4 Danh sach nha phan phoi");
            System.out.println("5 Tim kiem nha phan phoi");
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
                    addDistributor(sc);
                    break;
                case 2:
                    updateDistributor(sc);
                    break;
                case 3:
                    deleteDistributor(sc);
                    break;
                case 4:
                    listDistributors();
                    break;
                case 5:
                    searchDistributor(sc);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lua chon khong hop le!");
            }
        }
    }

}
