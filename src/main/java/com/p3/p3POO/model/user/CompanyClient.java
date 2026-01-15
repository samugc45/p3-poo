package com.p3.p3POO.model.user;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "company_clients")
public class CompanyClient extends Client {

    @Column(nullable = false, unique = true, length = 9)
    private String nif;

    @Column(nullable = false, length = 200)
    private String companyName;

    public CompanyClient() {
        super();
    }

    public CompanyClient(String nif, String companyName, String email, Cashier registeredBy) {
        super(nif, companyName, email, registeredBy);
        this.nif = nif;
        this.companyName = companyName;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CompanyClient that = (CompanyClient) o;
        return Objects.equals(nif, that.nif) && Objects.equals(companyName, that.companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nif, companyName);
    }

    public static boolean isValidNIF(String nif) {
        if (nif == null || nif.length() != 9) {
            return false;
        }

        return nif.matches("[A-Z]\\d{8}") || nif.matches("\\d{8}[A-Z]");
    }

    @Override
    public String toString() {
        return String.format("COMPANY{identifier='%s', name='%s', email='%s', cash=%s}", nif, companyName, email, registeredBy.getEmployeeCode());
    }
}
