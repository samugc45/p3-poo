package com.p3.p3POO.model.user;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Random;

@Entity
@Table(name = "cashiers")
@PrimaryKeyJoinColumn(name = "id")
public class Cashier extends User {

    @Column(nullable = false, unique = true, length = 9)
    private String employeeCode;

    protected Cashier() {
        super();
    }

    // Constructor con parámetros
    public Cashier(String employeeCode, String name, String email) {
        super();
        this.id = employeeCode;
        this.name = name;
        this.email = email;
        this.registrationDate = LocalDate.now();
        this.employeeCode = employeeCode;
    }


    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public static String generateEmployeeCode() {
        Random random = new Random();
        int code = random.nextInt(10000000);
        return String.format("UW%07d", code);
    }

    @Override
    public String toString() {
        return String.format("Cash{identifier='%s', name='%s', email='%s'}", employeeCode, name, email);
    }
}