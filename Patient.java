// Patient.java
public class Patient {
    private String id;          // IDBN - Ma benh nhan
    private String name;        // Ten benh nhan
    private int age;            // Tuoi
    private String phone;       // So dien thoai
    private String address;     // Dia chi

    // Constructor
    public Patient(String id, String name, int age, String phone, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.address = address;
    }

    // Getters
    public String getId() { 
        return id; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public int getAge() { 
        return age; 
    }
    
    public String getPhone() { 
        return phone; 
    }
    
    public String getAddress() { 
        return address; 
    }

    // Setters
    public void setName(String name) { 
        this.name = name; 
    }
    
    public void setAge(int age) { 
        this.age = age; 
    }
    
    public void setPhone(String phone) { 
        this.phone = phone; 
    }
    
    public void setAddress(String address) { 
        this.address = address; 
    }

    // toString - de luu vao file duoi dang CSV
    @Override
    public String toString() {
        return id + "," + name + "," + age + "," + phone + "," + address;
    }
}