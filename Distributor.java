public class Distributor {
    private String stt;
    private String name;
    private String phone;
    private String address;
    private int totalMedicineImported;

    public Distributor(String stt, String name, String phone, String address){
        this.stt = stt;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.totalMedicineImported = 0;
    }

    public String getStt(){
        return stt;
    }

    public String getName(){
        return name;
    }

    public String getPhone(){
        return phone;
    }

    public String getAddress(){
        return address;
    }

    public int getTotalMedicineImported(){
        return totalMedicineImported;
    }

    public void setStt(String stt){
        this.stt = stt;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public void setTotalMedicineImported(int totalMedicineImported){
        this.totalMedicineImported = totalMedicineImported;
    }

    public void addMedicineImported(int quantity){
        this.totalMedicineImported += quantity;
    }

    public String toString(){
        return stt + "," + name + "," + phone + "," + address + "," + totalMedicineImported;
    }
}
