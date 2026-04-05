import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private String mhd;
    private LocalDate invoiceDate;
    private String patientId;
    private String patientName;
    private List<MedicineDetail> details;
    private long totalAmount;

    public Invoice(String mhd, LocalDate invoiceDate, String patientId, String patientName){
        this.mhd = mhd;
        this.invoiceDate = invoiceDate;
        this.patientId = patientId;
        this.patientName = patientName;
        this.details = new ArrayList<>();
        this.totalAmount = 0;
    }

    public String getMhd(){
        return mhd;
    }

    public LocalDate getInvoiceDate(){
        return invoiceDate;
    }

    public String getPatientId(){
        return patientId;
    }

    public String getPatientName(){
        return patientName;
    }

    public List<MedicineDetail> getDetails(){
        return details;
    }

    public long getTotalAmount(){
        return totalAmount;
    }

    public void addMedicineDetail(MedicineDetail detail){
        details.add(detail);
        calculateTotal();
    }

    private void calculateTotal() {
        totalAmount = 0;
        for (MedicineDetail detail : details) {
            totalAmount += detail.getTotalPrice();
        }
    }


    public void removeMedicineDetail(int index) {
        if (index >= 0 && index < details.size()) {
            details.remove(index);
            calculateTotal();
        }
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(mhd).append("|");
        sb.append(invoiceDate).append("|");
        sb.append(patientId).append("|");
        sb.append(patientName).append("|");

        for(int i = 0; i < details.size(); i++){
            if(i > 0){
                sb.append(";");
            }
            sb.append(details.get(i).toString());
        }
        sb.append("|").append(totalAmount);
        return sb.toString();
    }
}
