package models;

/** INHERITANCE: Staff extends Person. */
public class Staff extends Person {
    public Staff(int id, String username, String password, String fullName) {
        super(id, username, password, fullName);
    }
    @Override
    public String getRole() { return "STAFF"; }   // POLYMORPHISM
}
