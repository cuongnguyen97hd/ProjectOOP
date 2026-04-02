// Employee.java
import java.time.LocalDate;

public class Employee {
    private String id;
    private String name;
    private LocalDate startDate; // ngay bat dau lam viec (tu session)
    private String position; // NhanVien or BacSi
    private String phone;
    private long basicSalary; // luong co ban neu lam du 26 ngay (VND)

    public Employee(String id, String name, LocalDate startDate,String position, String phone, long basicSalary) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.position = position;
        this.phone = phone;
        this.basicSalary = basicSalary;
    }

    // getters / setters
    public String getId() { 
        return id; 
    }
    public String getName() { 
        return name;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public String getPosition() { 
        return position; 
    }
    public String getPhone() { return phone; }
    public long getBasicSalary() { 
        return basicSalary; 
    }

    public void setName(String name) { 
        this.name = name; 
    }
    public void setPosition(String position) { 
        this.position = position; 
    
    this.phone = phone; 
    }
    public void setBasicSalary(long basicSalary) { 
        this.basicSalary = basicSalary; 
    }

    @Override
    public String toString() {
        return id + "," + name + "," + startDate.toString() + "," + position + "," + phone + "," + basicSalary;
    }
}
