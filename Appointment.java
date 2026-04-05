// Appointment.java
import java.time.LocalDate;

public class Appointment {
    private String stt;              // So thu tu
    private LocalDate appointmentDate; // Nam-thang-ngay
    private String patientId;        // IDBN
    private String patientName;      // Ten benh nhan
    private String doctorId;         // IDNV
    private String doctorName;       // Ten bac si
    private String status;           // Trang thai (cho, da kham, huy)

    // Constructor
    public Appointment(String stt, LocalDate appointmentDate, String patientId, 
                      String patientName, String doctorId, String doctorName) {
        this.stt = stt;
        this.appointmentDate = appointmentDate;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.status = "Cho";  // Mac dinh la "Cho"
    }

    // Getters
    public String getStt() { 
        return stt; 
    }
    
    public LocalDate getAppointmentDate() { 
        return appointmentDate; 
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
    
    public String getStatus() { 
        return status; 
    }

    // Setters
    public void setPatientName(String patientName) { 
        this.patientName = patientName; 
    }
    
    public void setDoctorName(String doctorName) { 
        this.doctorName = doctorName; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }

    // toString - de luu vao file duoi dang CSV
    @Override
    public String toString() {
        return stt + "," + appointmentDate.toString() + "," + patientId + "," + 
               patientName + "," + doctorId + "," + doctorName + "," + status;
    }
}