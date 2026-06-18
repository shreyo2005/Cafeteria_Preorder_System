package models;

/** INHERITANCE: Customer extends Person. */
public class Customer extends Person {
    public Customer(int id, String username, String password, String fullName) {
        super(id, username, password, fullName);
    }
    @Override
    public String getRole() { return "CUSTOMER"; } // POLYMORPHISM
}
