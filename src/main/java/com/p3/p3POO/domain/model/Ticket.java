package com.p3.p3POO.domain.model;

import com.p3.p3POO.domain.model.enums.TCategory;
import com.p3.p3POO.domain.model.enums.TicketMode;
import com.p3.p3POO.domain.model.enums.TicketState;
import com.p3.p3POO.domain.model.product.BasicProduct;
import com.p3.p3POO.domain.model.product.Event;
import com.p3.p3POO.domain.model.product.Product;
import com.p3.p3POO.domain.model.product.ProductPersonalized;
import com.p3.p3POO.domain.model.user.Cashier;
import com.p3.p3POO.domain.model.user.Client;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    private String id;  // Formato: YY-MM-dd-HH: mm-XXXXX (con fecha cierre al cerrar)

    @Column
    private String displayId;

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
        this.displayId = id; // Inicialmente igual al ID
        this.cashier = cashier;
        this. client = client;
        this. state = TicketState.EMPTY;
        this.mode = mode != null ? mode : TicketMode.BASIC;
        this.ticketLines = new ArrayList<>();
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

    public String getDisplayId() {
        return displayId != null ? displayId : id;
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
    // Cerrar ticket
    public void close() {
        if (this.state == TicketState.CLOSE) {
            return; // Ya está cerrado
        }

        this.state = TicketState.CLOSE;
        this.closeDate = LocalDateTime.now();

        // Crear displayId con timestamp (NO modificar id)
        DateTimeFormatter formatter = DateTimeFormatter. ofPattern("yy-MM-dd-HH: mm");
        String timestamp = closeDate.format(formatter);
        this.displayId = this.id + "-" + timestamp;

        // Ordenar productos alfabéticamente por nombre
        if (ticketLines != null && !ticketLines.isEmpty()) {
            ticketLines.sort((line1, line2) -> {
                if (line1.isProduct() && line2.isProduct()) {
                    return line1.getProduct().getName().compareTo(line2.getProduct().getName());
                }
                // Servicios van primero
                if (line1.isService() && line2.isProduct()) {
                    return -1;
                }
                if (line1.isProduct() && line2.isService()) {
                    return 1;
                }
                return 0;
            });
        }
    }

    public static String generateId() {
        // Formato: TK + timestamp + random
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 10000);
        return "TK" + timestamp + String.format("%04d", random);
    }

    /**
     * Calcula el precio total SIN descuentos
     */
    public Double calculateTotalPrice() {
        double total = 0.0;

        for (TicketLine line : ticketLines) {
            if (line.isProduct() && line.getProduct() != null) {
                total += line.getProduct().getBasePrice() * line.getQuantity();
            }
        }

        return total;
    }

    /**
     * Calcula el descuento total aplicado
     */
    public Double calculateTotalDiscount() {
        double totalPrice = calculateTotalPrice();
        double finalPrice = calculateFinalPrice();
        return totalPrice - finalPrice;
    }

    /**
     * Calcula el precio final CON descuentos (por categoría y servicios)
     */
    public Double calculateFinalPrice() {
        // Agrupar productos por categoría para aplicar descuentos
        Map<TCategory, List<TicketLine>> productsByCategory = new HashMap<>();

        for (TicketLine line : ticketLines) {
            if (line.isProduct() && line.getProduct() != null) {
                Product product = line.getProduct();

                // Solo productos básicos y personalizados tienen categoría
                if (product instanceof BasicProduct || product instanceof ProductPersonalized) {
                    TCategory category = product.getCategory();
                    if (category != null) {
                        productsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(line);
                    }
                } else if (product instanceof Event) {
                    // Eventos no tienen descuento por categoría
                    // Se añaden al total sin categoría
                }
            }
        }

        double finalPrice = 0.0;

        // Calcular precio con descuento por categoría (solo si ≥2 unidades)
        for (TicketLine line : ticketLines) {
            if (line.isProduct() && line.getProduct() != null) {
                Product product = line.getProduct();
                double linePrice = product.getBasePrice() * line.getQuantity();

                // Aplicar descuento solo si hay ≥2 productos de esa categoría
                if (product.getCategory() != null) {
                    List<TicketLine> categoryLines = productsByCategory.get(product.getCategory());
                    int totalQuantity = categoryLines. stream()
                            .mapToInt(TicketLine:: getQuantity)
                            . sum();

                    if (totalQuantity >= 2) {
                        double discount = product.getCategory().getDiscount();
                        linePrice = linePrice * (1 - discount);
                    }
                }

                finalPrice += linePrice;
            }
        }

        // Aplicar descuento extra del 30% si hay servicios en ticket combinado
        if (mode == TicketMode.DETAILED && hasServices() && hasProducts()) {
            finalPrice = finalPrice * 0.70; // 30% de descuento
        }

        return finalPrice;
    }

    /**
     * Calcula el descuento de un producto individual
     */
    public Double calculateProductDiscount(Product product, int quantity) {
        if (product. getCategory() == null) {
            return 0.0;
        }

        // Agrupar productos por categoría
        Map<TCategory, Integer> quantityByCategory = new HashMap<>();

        for (TicketLine line : ticketLines) {
            if (line.isProduct() && line.getProduct() != null && line.getProduct().getCategory() != null) {
                TCategory category = line.getProduct().getCategory();
                quantityByCategory.put(category, quantityByCategory. getOrDefault(category, 0) + line.getQuantity());
            }
        }

        // Obtener cantidad total de la categoría
        int totalQuantity = quantityByCategory.getOrDefault(product.getCategory(), 0);

        // Solo aplicar descuento si hay ≥2 productos de esa categoría
        if (totalQuantity >= 2) {
            double discount = product.getCategory().getDiscount();
            return product.getBasePrice() * quantity * discount;
        }

        return 0.0;
    }

    /**
     * Verifica si el ticket tiene productos
     */
    public boolean hasProducts() {
        return ticketLines.stream().anyMatch(TicketLine::isProduct);
    }

    /**
     * Verifica si el ticket tiene servicios
     */
    public boolean hasServices() {
        return ticketLines.stream().anyMatch(TicketLine::isService);
    }

    /**
     * Formatea el ticket para mostrar en comandos
     */
    /**
     * Formatea el ticket para mostrar en comandos
     */
    public String formatForDisplay() {
        StringBuilder sb = new StringBuilder();

        // Usar displayId si existe, sino usar id
        String idToShow = (displayId != null && !displayId.equals(id)) ? displayId : id;
        sb.append("Ticket :  ").append(idToShow).append("\n");

        // Si tiene servicios, mostrarlos primero
        if (hasServices()) {
            sb.append("Services Included:  \n");
            for (TicketLine line : ticketLines) {
                if (line.isService()) {
                    sb.append("  ").append(line.getService().toString()).append("\n");
                }
            }
        }

        boolean hayProductos = hasProducts();
        if (hayProductos && hasServices()) {
            sb.append("Product Included\n");
        }

        // ---- Productos individuales ----
        if (hayProductos) {
            for (TicketLine line : ticketLines) {
                if (line.isProduct()) {
                    Product product = line.getProduct();

                    double unitDiscount = calculateUnitDiscount(product);

                    for (int i = 0; i < line.getQuantity(); i++) {
                        sb.append("  ");
                        if (product instanceof ProductPersonalized && line.hasPersonalizations()) {
                            ProductPersonalized pp = (ProductPersonalized) product;
                            sb.append(pp.toStringWithPersonalizations(line.getPersonalizations()));
                        } else {
                            sb.append(product.toString());
                        }
                        if (unitDiscount > 0) {
                            sb.append(String.format(java.util.Locale.US, " **discount -%.1f", unitDiscount));
                        }
                        sb.append("\n");
                    }
                }
            }

            // --------- TOTALES y DESCUENTO EXTRA ----------
            double totalPrice = calculateTotalPrice();
            double totalDiscount = calculateTotalDiscount();
            double finalPrice = calculateFinalPrice();

            sb.append(String.format(java.util.Locale.US, "  Total price: %.1f\n", totalPrice));
            if (mode == TicketMode.DETAILED && hasServices() && hasProducts()) {
                double extraDiscount = totalPrice * 0.30;
                sb.append(String.format(java.util.Locale.US,
                        "  Extra Discount from services:%.1f **discount -%.1f\n", extraDiscount, extraDiscount));
            }
            sb.append(String.format(java.util.Locale.US, "  Total discount: %.1f\n", totalDiscount));
            sb.append(String.format(java.util.Locale.US, "  Final Price: %.1f", finalPrice));
        } else if (!hasServices()) {
            // Ticket vacío
            sb.append("  Total price: 0.0\n");
            sb.append("  Total discount: 0.0\n");
            sb.append("  Final Price: 0.0");
        }

        return sb.toString();
    }

    /**
     * Calcula el descuento UNITARIO de un producto (considerando personalizaciones)
     */
    private double calculateUnitDiscount(Product product) {
        if (product. getCategory() == null) {
            return 0.0;
        }

        // Agrupar productos por categoría
        Map<TCategory, Integer> quantityByCategory = new HashMap<>();

        for (TicketLine line : ticketLines) {
            if (line.isProduct() && line.getProduct() != null && line.getProduct().getCategory() != null) {
                TCategory category = line.getProduct().getCategory();
                quantityByCategory.put(category, quantityByCategory. getOrDefault(category, 0) + line.getQuantity());
            }
        }

        // Obtener cantidad total de la categoría
        int totalQuantity = quantityByCategory.getOrDefault(product.getCategory(), 0);

        // Solo aplicar descuento si hay ≥2 productos de esa categoría
        if (totalQuantity >= 2) {
            double discount = product.getCategory().getDiscount();

            // Para productos personalizados, calcular sobre el precio con personalizaciones
            if (product instanceof ProductPersonalized) {
                // Buscar la línea correspondiente para obtener las personalizaciones
                for (TicketLine line : ticketLines) {
                    if (line.isProduct() && line.getProduct().equals(product) && line.hasPersonalizations()) {
                        double surcharge = line.getPersonalizations().size() * product.getBasePrice() * 0.10;
                        double priceWithPersonalizations = product.getBasePrice() + surcharge;
                        return priceWithPersonalizations * discount;
                    }
                }
            }

            return product.getBasePrice() * discount;
        }

        return 0.0;
    }

    /**
     * Cierra el ticket y añade timestamp al ID
     */
    /**
     * Cierra el ticket, añade timestamp al ID y ordena productos alfabéticamente
     */
    /**
     * Cierra el ticket, añade timestamp al displayId y ordena productos alfabéticamente
     */
    public void closeTicket() {
        if (state == TicketState. CLOSE) {
            return; // Ya está cerrado
        }

        this.state = TicketState. CLOSE;
        this.closeDate = LocalDateTime.now();

        // Crear displayId con timestamp:  212121 → 212121-26-01-13-12:00
        DateTimeFormatter formatter = DateTimeFormatter. ofPattern("yy-MM-dd-HH: mm");
        String timestamp = closeDate.format(formatter);
        this.displayId = id + "-" + timestamp;

        // Ordenar productos alfabéticamente por nombre
        if (ticketLines != null && !ticketLines.isEmpty()) {
            ticketLines.sort((line1, line2) -> {
                if (line1.isProduct() && line2.isProduct()) {
                    return line1.getProduct().getName().compareTo(line2.getProduct().getName());
                }
                // Servicios van primero
                if (line1.isService() && line2.isProduct()) {
                    return -1;
                }
                if (line1.isProduct() && line2.isService()) {
                    return 1;
                }
                return 0;
            });
        }
    }
}
