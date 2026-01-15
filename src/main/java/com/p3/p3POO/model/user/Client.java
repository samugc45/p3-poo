package com.p3.p3POO.model.user;

import com.p3.p3POO.model.Ticket;
import com.p3.p3POO.model.enums.ClientType;
import jakarta.persistence.*;
import lombok. EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "clients")
public class Client extends User {

    @Column(nullable = false, unique = true, length = 9)
    private String dni;  // DNI es el ID único del cliente

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientType clientType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by_cashier_id")
    protected Cashier registeredBy;

    @OneToMany(mappedBy = "client", cascade = CascadeType. ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    // Constructor sin argumentos (JPA)
    public Client() {
        super();
        this.clientType = ClientType.NORMAL;
        this.tickets = new ArrayList<>();
    }

    // Constructor completo
    public Client(String dni, String name, String email, Cashier registeredBy) {
        super(dni, name, email);  // El ID es el DNI
        this.dni = dni;
        this.clientType = ClientType.NORMAL;
        this.registeredBy = registeredBy;
        this.tickets = new ArrayList<>();
    }

    // Método para añadir ticket
    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
        ticket.setClient(this);
    }

    @Override
    public String toString() {
        return String.format("USER{identifier='%s', name='%s', email='%s', cash=%s}", dni, name, email, registeredBy.getEmployeeCode());
    }
}