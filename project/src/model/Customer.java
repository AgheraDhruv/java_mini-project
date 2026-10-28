package model;

import java.io.Serializable;
import model.annotation.Id;
import model.annotation.MaxLength;

/**
 * Customer — represents a bank customer with auto-generated ID.
 * Practical 2: encapsulation, static counter.
 * Practical 3: nested Address class, Cloneable.
 * Practical 6: moved into model package.
 * Practical 7: @Id and @MaxLength annotations.
 * Practical 11: implements Serializable with serialVersionUID.
 */
public class Customer implements Cloneable, Serializable {

    private static final long serialVersionUID = 1001L;

    private static long customerCounter = 100;

    @Id
    private final String customerId;

    private String name;
    private String email;

    @MaxLength(10)
    private String mobile;

    private Address address;

    /**
     * Creates a new Customer with an auto-generated ID like "CUST101".
     */
    public Customer(String name, String email, String mobile) {
        this.customerId = generateCustomerId();
        this.name = name;
        this.email = email;
        this.mobile = mobile;
    }

    private static String generateCustomerId() {
        customerCounter++;
        return "CUST" + customerCounter;
    }

    public static void updateCounterFromId(String custId) {
        if (custId != null && custId.startsWith("CUST")) {
            try {
                long num = Long.parseLong(custId.substring(4));
                if (num > customerCounter) {
                    customerCounter = num;
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    // --- Getters ---

    public String getCustomerId() { return customerId; }
    public String getName()       { return name; }
    public String getEmail()      { return email; }
    public String getMobile()     { return mobile; }
    public Address getAddress()   { return address; }

    public void setName(String name)       { this.name = name; }
    public void setEmail(String email)     { this.email = email; }
    public void setMobile(String mobile)   { this.mobile = mobile; }
    public void setAddress(Address address) { this.address = address; }

    /**
     * Returns a shallow copy of this Customer.
     */
    @Override
    public Customer clone() {
        try {
            return (Customer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone not supported", e);
        }
    }

    @Override
    public String toString() {
        String s = customerId + " | " + name + " | " + email + " | " + mobile;
        if (address != null) {
            s += " | " + address;
        }
        return s;
    }

    // --- Public Static Nested Class ---

    /**
     * Address — a static nested class holding address details.
     */
    public static class Address implements Serializable {
        private static final long serialVersionUID = 1002L;

        private String line;
        private String city;
        private String pincode;

        public Address(String line, String city, String pincode) {
            this.line = line;
            this.city = city;
            this.pincode = pincode;
        }

        public String getLine()    { return line; }
        public String getCity()    { return city; }
        public String getPincode() { return pincode; }

        @Override
        public String toString() {
            return line + ", " + city + " - " + pincode;
        }
    }
}
