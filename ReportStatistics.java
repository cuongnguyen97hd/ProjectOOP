
import java.time.LocalDate;

public class ReportStatistics {
    private LocalDate date;             // Ngay hoac thang
    private int examinationCount;       // So luot kham
    private long serviceCost;           // Chi phi dich vu kham
    private long medicineCost;          // Chi phi thuoc
    private long totalRevenue;          // Tong doanh thu


    public ReportStatistics(LocalDate date) {
        this.date = date;
        this.examinationCount = 0;
        this.serviceCost = 0;
        this.medicineCost = 0;
        this.totalRevenue = 0;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getExaminationCount() {
        return examinationCount;
    }

    public long getServiceCost() {
        return serviceCost;
    }

    public long getMedicineCost() {
        return medicineCost;
    }

    public long getTotalRevenue() {
        return totalRevenue;
    }

    public void setExaminationCount(int examinationCount) {
        this.examinationCount = examinationCount;
    }

    public void setServiceCost(long serviceCost) {
        this.serviceCost = serviceCost;
    }

    public void setMedicineCost(long medicineCost) {
        this.medicineCost = medicineCost;
    }

    public void calculateTotalRevenue() {
        this.totalRevenue = this.serviceCost + this.medicineCost;
    }

    public void addExamination() {
        this.examinationCount++;
    }

    public void addServiceCost(long cost) {
        this.serviceCost += cost;
    }

    public void addMedicineCost(long cost) {
        this.medicineCost += cost;
    }
}