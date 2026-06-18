package models;

/** INHERITANCE: Admin extends Person. */
public class Admin extends Person {
    public Admin(int id, String username, String password, String fullName) {
        super(id, username, password, fullName);
    }
    @Override
    public String getRole() { return "ADMIN"; }   // POLYMORPHISM
}
