
public class MedicineDetail {
    private String medicineName;
    private int quantity;
    private long unitPrice;
    private long totalPrice;

    public MedicineDetail(String medicineName, int quantity, long unitPrice){
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }

    public String getMedicineName(){
        return medicineName;
    }

    public int getQuantity(){
        return quantity;
    }

    public long getUnitPrice(){
        return unitPrice;
    }

    public long getTotalPrice(){
        return totalPrice;
        
    }

    public String toString(){
        return medicineName + "," + quantity + "," + unitPrice + "," + totalPrice;
    }
}
