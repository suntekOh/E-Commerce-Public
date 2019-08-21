package com.example.e_commerce_navigation;

import java.util.Date;

/**
 * Class related to getting or setting DB Customer Data
 * @author      Seontaek Oh
 * @version     1.0
 * @since       1.0
 */
public class Customer {
    private String Email;
    private String Password;
    private String Type;
    private Date CreatedDttm;

    public Customer(String email, String password, String type, Date createdDttm) {
        Email = email;
        Password = password;
        Type = type;
        CreatedDttm = createdDttm;
    }

    /**
     * Constuctor of the class related to getting or setting DB Customer Data
     * @param email
     * @param password
     * @param type
     */
    public Customer(String email, String password, String type) {
        Email = email;
        Password = password;
        Type = type;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public Date getCreatedDttm() {
        return CreatedDttm;
    }

    public void setCreatedDttm(Date createdDttm) {
        CreatedDttm = createdDttm;
    }
}
