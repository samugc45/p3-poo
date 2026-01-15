package com.p3.p3POO.model;

import com.p3.p3POO.model.product.Product;
import com.p3.p3POO.model.product.ProductPersonalized;
import com.p3.p3POO.model.service.ServiceProduct;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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
    private ServiceProduct serviceProduct;

    @Column(nullable = false)
    private Integer quantity;  // Cantidad o número de personas (para eventos)

    @Column
    private Double unitPrice;  // Precio unitario en el momento de la compra

    @Column
    private Double totalPrice;  // Precio total de la línea (unitPrice * quantity)

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ticket_line_personalizations", joinColumns = @JoinColumn(name = "ticket_line_id"))
    @Column(name = "personalization")
    private List<String> personalizations;

    // Constructor vacío (JPA)
    public TicketLine() {
        this.personalizations = new ArrayList<>();
    }

    // Constructor para PRODUCTO
    public TicketLine(Ticket ticket, Product product, Integer quantity) {
        this.ticket = ticket;
        this.product = product;
        this.serviceProduct = null;
        this.quantity = quantity;
        this.unitPrice = product.calculateFinalPrice();
        this.totalPrice = this.unitPrice * quantity;
        this.personalizations = new ArrayList<>();
    }

    // Constructor para PRODUCTO PERSONALIZADO
    public TicketLine(Ticket ticket, Product product, Integer quantity, List<String> personalizations) {
        this.ticket = ticket;
        this.product = product;
        this.serviceProduct = null;
        this.quantity = quantity;
        this.personalizations = personalizations != null ? new ArrayList<>(personalizations) : new ArrayList<>();

        // Calcular precio con personalizaciones
        if (product instanceof ProductPersonalized && ! this.personalizations.isEmpty()) {
            ProductPersonalized pp = (ProductPersonalized) product;
            double surcharge = this.personalizations.size() * pp.getBasePrice() * 0.10;
            this.unitPrice = pp.getBasePrice() + surcharge;
        } else {
            this.unitPrice = product.calculateFinalPrice();
        }

        this.totalPrice = this. unitPrice * quantity;
    }

    // Constructor para SERVICIO (sin precio)
    public TicketLine(Ticket ticket, ServiceProduct serviceProduct, Integer quantity) {
        this.ticket = ticket;
        this.product = null;
        this.serviceProduct = serviceProduct;
        this.quantity = quantity;
        this.unitPrice = null;  // Los servicios NO tienen precio al agregarlos
        this.totalPrice = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ServiceProduct getServiceProduct() {
        return serviceProduct;
    }

    public void setServiceProduct(ServiceProduct serviceProduct) {
        this.serviceProduct = serviceProduct;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<String> getPersonalizations() {
        return personalizations != null ? personalizations : new ArrayList<>();
    }

    public void setPersonalizations(List<String> personalizations) {
        this.personalizations = personalizations;
    }

    public boolean hasPersonalizations() {
        return personalizations != null && !personalizations.isEmpty();
    }

    // Verificar si la línea es un producto
    public boolean isProduct() {
        return product != null;
    }

    // Verificar si la línea es un servicio
    public boolean isService() {
        return serviceProduct != null;
    }

    // Obtener nombre del item (producto o servicio)
    public String getItemName() {
        if (isProduct()) {
            return product.getName();
        } else if (isService()) {
            return "Service " + serviceProduct.getId() + " (" + serviceProduct.getServiceType() + ")";
        }
        return "Unknown";
    }
}
