package com.p3.p3POO.domain.model;

import com.p3.p3POO.domain.model.product.Product;
import com.p3.p3POO.domain.model.service.Service;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ticket_lines")
public class TicketLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    // Una línea puede ser PRODUCTO o SERVICIO (nunca ambos)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(nullable = false)
    private Integer quantity;  // Cantidad o número de personas (para eventos)

    @Column
    private Double unitPrice;  // Precio unitario en el momento de la compra

    @Column
    private Double totalPrice;  // Precio total de la línea (unitPrice * quantity)

    // Constructor sin argumentos (JPA)
    public TicketLine() {}

    // Constructor para PRODUCTO
    public TicketLine(Ticket ticket, Product product, Integer quantity) {
        this.ticket = ticket;
        this.product = product;
        this.service = null;
        this.quantity = quantity;
        this.unitPrice = product.calculateFinalPrice();
        this.totalPrice = this.unitPrice * quantity;
    }

    // Constructor para SERVICIO (sin precio)
    public TicketLine(Ticket ticket, Service service, Integer quantity) {
        this.ticket = ticket;
        this.product = null;
        this.service = service;
        this.quantity = quantity;
        this.unitPrice = null;  // Los servicios NO tienen precio al agregarlos
        this.totalPrice = null;
    }

    // Verificar si la línea es un producto
    public boolean isProduct() {
        return product != null;
    }

    // Verificar si la línea es un servicio
    public boolean isService() {
        return service != null;
    }

    // Obtener nombre del item (producto o servicio)
    public String getItemName() {
        if (isProduct()) {
            return product.getName();
        } else if (isService()) {
            return "Service " + service.getId() + " (" + service.getServiceType() + ")";
        }
        return "Unknown";
    }
}
