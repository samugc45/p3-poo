package com.p3.p3POO.domain.model;

import com.p3.p3POO.domain.model.enums.TicketMode;
import com.p3.p3POO.domain.model.enums.TicketState;
import com.p3.p3POO.domain.model.user.Cashier;
import com.p3.p3POO.domain.model.user.Client;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    private String id;  // Formato: YY-MM-dd-HH: mm-XXXXX (con fecha cierre al cerrar)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketState state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketMode mode;  // BASIC (productos), DETAILED (empresa con servicios)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", nullable = false)
    private Cashier cashier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TicketLine> ticketLines;

    @Column(nullable = false)
    private LocalDateTime openDate;

    @Column
    private LocalDateTime closeDate;

    // Constructor sin argumentos (JPA)
    public Ticket() {
        this.state = TicketState.EMPTY;
        this.mode = TicketMode.BASIC;
        this.ticketLines = new ArrayList<>();
        this.openDate = LocalDateTime. now();
    }

    public Ticket(String id) {
        this.id = id;
        this.state = TicketState.EMPTY;
        this.mode = TicketMode.BASIC;
        this.ticketLines = new ArrayList<>();
        this.openDate = LocalDateTime.now();
    }

    // Constructor completo
    public Ticket(String id, Cashier cashier, Client client, TicketMode mode) {
        this.id = id;
        this.cashier = cashier;
        this.client = client;
        this.state = TicketState.EMPTY;
        this.mode = mode != null ? mode : TicketMode.BASIC;
        this. ticketLines = new ArrayList<>();
        this.openDate = LocalDateTime.now();
    }

    // Añadir línea al ticket
    public void addLine(TicketLine line) {
        this.ticketLines. add(line);
        line.setTicket(this);

        if (state == TicketState.EMPTY) {
            state = TicketState.OPEN;
        }
    }

    // Eliminar línea del ticket
    public void removeLine(TicketLine line) {
        this.ticketLines.remove(line);
        line.setTicket(null);

        if (ticketLines.isEmpty()) {
            state = TicketState.EMPTY;
        }
    }

    // Calcular total del ticket
    public Double calculateTotal() {
        return ticketLines.stream()
                .filter(line -> line.getTotalPrice() != null)
                .mapToDouble(TicketLine::getTotalPrice)
                .sum();
    }

    // Contar productos en el ticket
    public long countProducts() {
        return ticketLines.stream()
                .filter(TicketLine::isProduct)
                .count();
    }

    // Contar servicios en el ticket
    public long countServices() {
        return ticketLines.stream()
                .filter(TicketLine::isService)
                .count();
    }

    // Cerrar ticket
    public void close() {
        if (state == TicketState.CLOSE) {
            throw new IllegalStateException("Ticket already closed");
        }

        this.state = TicketState.CLOSE;
        this.closeDate = LocalDateTime.now();

        // Actualizar ID con fecha de cierre:  YY-MM-dd-HH:mm-XXXXX-YY-MM-dd-HH:mm
        DateTimeFormatter formatter = DateTimeFormatter. ofPattern("yy-MM-dd-HH:mm");
        this.id = this.id + "-" + closeDate.format(formatter);
    }

    public static String generateId() {
        // Formato: TK + timestamp + random
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 10000);
        return "TK" + timestamp + String.format("%04d", random);
    }
}
