package com.p3.p3POO.model.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "company_clients")
public class CompanyClient extends Client {

    @Column(nullable = false, unique = true, length = 9)
    private String nif;  // NIF es el identificador fiscal de empresa

    @Column(nullable = false, length = 200)
    private String companyName;

    // Constructor sin argumentos (JPA)
    public CompanyClient() {
        super();
    }

    // Constructor completo
    public CompanyClient(String nif, String companyName, String email, Cashier registeredBy) {
        super(nif, companyName, email, registeredBy);  // El ID es el NIF
        this.nif = nif;
        this.companyName = companyName;
    }

    // Validación NIF español (letra + 8 dígitos o 8 dígitos + letra)
    public static boolean isValidNIF(String nif) {
        if (nif == null || nif.length() != 9) {
            return false;
        }

        // Formato: A12345678 o 12345678A
        return nif.matches("[A-Z]\\d{8}") || nif.matches("\\d{8}[A-Z]");
    }

    @Override
    public String toString() {
        return String.format("COMPANY{identifier='%s', name='%s', email='%s', cash=%s}", nif, companyName, email, registeredBy.getEmployeeCode());
    }
}
