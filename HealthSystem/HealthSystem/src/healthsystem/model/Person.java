package healthsystem.model;

/**
 * Abstract base class representing a person in the health system.
 */
public abstract class Person {

    // Encapsulated fields with private access
    private String id;
    private String name;
    private int age;
    private String phone;
    private String email;

    // Constructor
    public Person(String id, String name, int age, String phone, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.email = email;
    }

    // Abstract method
    public abstract String getRole();

    // Abstract method for health summary (polymorphism)
    public abstract String getSummary();

    // Getters and Setters (Encapsulation)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "[" + getRole() + "] " + name + " (ID: " + id + ")";
    }
}
