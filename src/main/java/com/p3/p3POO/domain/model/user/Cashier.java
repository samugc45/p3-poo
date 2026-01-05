package com.p3.p3POO. domain.model.user;

import com.p3.p3POO.domain.model.Ticket;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java. util.ArrayList;
import java. util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cashiers")
public class Cashier extends User {

    @Column(nullable = false, unique = true, length = 9)
    private String employeeCode;  // Formato: UW + 7 dígitos

    @OneToMany(mappedBy = "cashier", cascade = CascadeType. REMOVE, orphanRemoval = true)
    private List<Ticket> createdTickets;

    @OneToMany(mappedBy = "registeredBy")
    private List<Client> registeredClients;

    // Constructor sin argumentos (JPA)
    public Cashier() {
        super();
        this.createdTickets = new ArrayList<>();
        this.registeredClients = new ArrayList<>();
    }

    // Constructor con código generado
    public Cashier(String employeeCode, String name, String email) {
        super(employeeCode, name, email);  // El ID es el employeeCode
        this.employeeCode = employeeCode;
        this.createdTickets = new ArrayList<>();
        this.registeredClients = new ArrayList<>();
    }

    // Método para añadir ticket creado
    public void addCreatedTicket(Ticket ticket) {
        this.createdTickets.add(ticket);
        ticket.setCashier(this);
    }

    // Método para eliminar ticket
    public void removeCreatedTicket(Ticket ticket) {
        this.createdTickets.remove(ticket);
        ticket.setCashier(null);
    }

    // Validación código cajero (UW + 7 dígitos)
    public static boolean isValidEmployeeCode(String code) {
        return code != null && code.matches("UW\\d{7}");
    }

    // Generador de código aleatorio
    public static String generateEmployeeCode() {
        int randomNumber = (int) (Math.random() * 10000000);
        return String.format("UW%07d", randomNumber);
    }
}