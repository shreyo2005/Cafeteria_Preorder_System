package models;

/**
 * Abstract base class demonstrating ABSTRACTION.
 * All system users (Admin, Staff, Customer) inherit from Person.
 */
public abstract class Person {
    private int id;
    private String username;
    private String password;
    private String fullName;

    public Person(int id, String username, String password, String fullName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    // Abstract method -> POLYMORPHISM (each subclass returns its own role)
    public abstract String getRole();

    // ---------- Encapsulation: getters / setters ----------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    @Override
    public String toString() {
        return fullName + " (" + getRole() + ")";
    }
}
