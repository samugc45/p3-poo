package com.p3.p3POO.model.user;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType. JOINED)
public abstract class User {

    @Id
    protected String id;

    @Column(nullable = false, length = 100)
    protected String name;

    @Column(nullable = false)
    protected String email;

    @Column(nullable = false)
    protected LocalDate registrationDate;

    // Constructor sin argumentos (requerido por JPA)
    protected User() {
        this.registrationDate = LocalDate.now();
    }

    // Constructor con parámetros
    protected User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.registrationDate = LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
}
