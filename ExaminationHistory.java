
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExaminationHistory {
    private LocalDate examinationDate;  // Ngay kham
    private String patientId;           // IDBN
    private String patientName;         // Ten benh nhan
    private String doctorId;            // IDNV
    private String doctorName;          // Ten bac si
    private String diagnosis;           // Chan doan
    private List<String> medicines;     // Danh sach thuoc da su dung

    public ExaminationHistory(LocalDate examinationDate, String patientId, String patientName,
                             String doctorId, String doctorName, String diagnosis) {
        this.examinationDate = examinationDate;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.diagnosis = diagnosis;
        this.medicines = new ArrayList<>();
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

    public List<String> getMedicines() {
        return medicines;
    }

    public void addMedicine(String medicineName) {
        if (!medicines.contains(medicineName)) {
            medicines.add(medicineName);
        }
    }

    // toString - de luu vao file
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(examinationDate.toString()).append(",");
        sb.append(patientId).append(",");
        sb.append(patientName).append(",");
        sb.append(doctorId).append(",");
        sb.append(doctorName).append(",");
        sb.append(diagnosis).append(",");
        
        // Them danh sach thuoc 
        for (int i = 0; i < medicines.size(); i++) {
            if (i > 0) sb.append(";");
            sb.append(medicines.get(i));
        }
        
        return sb.toString();
    }
}