// MedicalRecord.java
import java.time.LocalDate;

public class MedicalRecord {
    private String stt;              // So thu tu
    private LocalDate examinationDate; // Ngay-thang-nam
    private String patientId;        // IDBN
    private String patientName;      // Ten benh nhan
    private String doctorId;         // IDNV
    private String doctorName;       // Ten bac si
    private String diagnosis;        // Chan doan benh
    private long serviceCost;        // Chi phi dich vu kham

    // Constructor
    public MedicalRecord(String stt, LocalDate examinationDate, String patientId,
                        String patientName, String doctorId, String doctorName,
                        String diagnosis, long serviceCost) {
        this.stt = stt;
        this.examinationDate = examinationDate;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.diagnosis = diagnosis;
        this.serviceCost = serviceCost;
    }

    // Getters
    public String getStt() {
        return stt;
    }

    public LocalDate getExaminationDate() {
        return examinationDate;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public long getServiceCost() {
        return serviceCost;
    }

    // Setters
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setServiceCost(long serviceCost) {
        this.serviceCost = serviceCost;
    }

    // toString - de luu vao file duoi dang CSV
    @Override
    public String toString() {
        return stt + "," + examinationDate.toString() + "," + patientId + "," +
               patientName + "," + doctorId + "," + doctorName + "," +
               diagnosis + "," + serviceCost;
    }
}