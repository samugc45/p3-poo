package com.p3.p3POO.domain.model.user;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType. JOINED)
public abstract class User {

    @Id
    protected String id;

    @Column(nullable = false, length = 100)
    protected String name;

    @Column(nullable = false, unique = true)
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
}
