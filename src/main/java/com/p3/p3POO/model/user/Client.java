package com.p3.p3POO.model.user;

import com.p3.p3POO.model.Ticket;
import com.p3.p3POO.model.enums.ClientType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "clients")
public class Client extends User {

    @Column(nullable = false, unique = true, length = 9)
    private String dni;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientType clientType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by_cashier_id")
    protected Cashier registeredBy;

    @OneToMany(mappedBy = "client", cascade = CascadeType. ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    public Client() {
        super();
        this.clientType = ClientType.NORMAL;
        this.tickets = new ArrayList<>();
    }

    public Client(String dni, String name, String email, Cashier registeredBy) {
        super(dni, name, email);
        this.dni = dni;
        this.clientType = ClientType.NORMAL;
        this.registeredBy = registeredBy;
        this.tickets = new ArrayList<>();
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public Cashier getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(Cashier registeredBy) {
        this.registeredBy = registeredBy;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(dni, client.dni) && clientType == client.clientType && Objects.equals(registeredBy, client.registeredBy) && Objects.equals(tickets, client.tickets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni, clientType, registeredBy, tickets);
    }

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
        ticket.setClient(this);
    }

    @Override
    public String toString() {
        return String.format("USER{identifier='%s', name='%s', email='%s', cash=%s}", dni, name, email, registeredBy.getEmployeeCode());
    }
}