package healthsystem.model;

/**
 * Doctor class - inherits from Person.
 * Represents a doctor registered in the health system.
 */
public class Doctor extends Person {

    private String specialization;
    private String licenseNumber;
    private boolean available;

    // Constructor
    public Doctor(String id, String name, int age, String phone, String email,
                  String specialization, String licenseNumber) {
        super(id, name, age, phone, email); // Call parent constructor
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.available = true;
    }

    // Override abstract methods from Person (polymorphism)
    @Override
    public String getRole() {
        return "Doctor";
    }

    @Override
    public String getSummary() {
        return "Dr. " + getName() +
               " | Specialization: " + specialization +
               " | License: " + licenseNumber +
               " | Available: " + (available ? "Yes" : "No");
    }

    // Getters and Setters
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    // Serialize to CSV line for file storage
    public String toCsvLine() {
        return String.join(",",
            getId(), getName(), String.valueOf(getAge()), getPhone(), getEmail(),
            specialization, licenseNumber, String.valueOf(available));
    }

    // Deserialize from CSV line
    public static Doctor fromCsvLine(String line) {
        String[] parts = line.split(",", 8);
        Doctor d = new Doctor(parts[0], parts[1], Integer.parseInt(parts[2]),
                              parts[3], parts[4], parts[5], parts[6]);
        d.setAvailable(Boolean.parseBoolean(parts[7]));
        return d;
    }
}
