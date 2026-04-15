package healthsystem.model;


 //Patient class inherits from Person.

public class Patient extends Person {

    private String bloodType;
    private String medicalHistory;
    private boolean isActive;

    // Constructor
    public Patient(String id, String name, int age, String phone, String email,
                   String bloodType, String medicalHistory) {
        super(id, name, age, phone, email); // Call parent constructor
        this.bloodType = bloodType;
        this.medicalHistory = medicalHistory;
        this.isActive = true;
    }

    // Override abstract methods from Person (polymorphism)
    @Override
    public String getRole() {
        return "Patient";
    }

    @Override
    public String getSummary() {
        return "Patient: " + getName() +
               " | Age: " + getAge() +
               " | Blood Type: " + bloodType +
               " | History: " + medicalHistory;
    }

    // Getters and Setters
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // Serialize to CSV line for file storage
    public String toCsvLine() {
        return String.join(",",
            getId(), getName(), String.valueOf(getAge()), getPhone(), getEmail(),
            bloodType, medicalHistory.replace(",", ";"), String.valueOf(isActive));
    }

    // Deserialize from CSV line
    public static Patient fromCsvLine(String line) {
        String[] parts = line.split(",", 8);
        Patient p = new Patient(parts[0], parts[1], Integer.parseInt(parts[2]),
                                parts[3], parts[4], parts[5], parts[6].replace(";", ","));
        p.setActive(Boolean.parseBoolean(parts[7]));
        return p;
    }
}
