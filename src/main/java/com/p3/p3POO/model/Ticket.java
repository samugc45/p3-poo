package com.p3.p3POO.model;

import com.p3.p3POO.model.enums.TCategory;
import com.p3.p3POO.model.enums.TicketMode;
import com.p3.p3POO.model.enums.TicketState;
import com.p3.p3POO.model.product.BasicProduct;
import com.p3.p3POO.model.product.Product;
import com.p3.p3POO.model.product.ProductPersonalized;
import com.p3.p3POO.model.user.Cashier;
import com.p3.p3POO.model.user.Client;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    private String id;

    @Column
    private String displayId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketState state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketMode mode;

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

    public Ticket(String id, Cashier cashier, Client client, TicketMode mode) {
        this.id = id;
        this.displayId = id;
        this.cashier = cashier;
        this. client = client;
        this. state = TicketState.EMPTY;
        this.mode = mode != null ? mode : TicketMode.BASIC;
        this.ticketLines = new ArrayList<>();
        this.openDate = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    public TicketState getState() {
        return state;
    }

    public void setState(TicketState state) {
        this.state = state;
    }

    public TicketMode getMode() {
        return mode;
    }

    public void setMode(TicketMode mode) {
        this.mode = mode;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public void setCashier(Cashier cashier) {
        this.cashier = cashier;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<TicketLine> getTicketLines() {
        return ticketLines;
    }

    public void setTicketLines(List<TicketLine> ticketLines) {
        this.ticketLines = ticketLines;
    }

    public LocalDateTime getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDateTime openDate) {
        this.openDate = openDate;
    }

    public LocalDateTime getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDateTime closeDate) {
        this.closeDate = closeDate;
    }

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

    public void removeLine(TicketLine line) {
        this.ticketLines.remove(line);
        line.setTicket(null);

        if (ticketLines.isEmpty()) {
            state = TicketState.EMPTY;
        }
    }

    public Double calculateTotal() {
        return ticketLines.stream().filter(line -> line.getTotalPrice() != null).mapToDouble(TicketLine::getTotalPrice).sum();
    }

    public long countProducts() {
        return ticketLines.stream().filter(TicketLine::isProduct).count();
    }

    public long countServices() {
        return ticketLines.stream().filter(TicketLine::isService).count();
    }

    public void close() {
        if (this.state == TicketState.CLOSE) {
            return;
        }

        this.state = TicketState.CLOSE;
        this.closeDate = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd-HH: mm");
        String timestamp = closeDate.format(formatter);
        this.displayId = this.id + "-" + timestamp;

        if (ticketLines != null && !ticketLines.isEmpty()) {
            ticketLines.sort((line1, line2) -> {
                if (line1.isProduct() && line2.isProduct()) {
                    return line1.getProduct().getName().compareTo(line2.getProduct().getName());
                }
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
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm"));
        int random = (int) (Math.random() * 100000);
        return timestamp + "-" + String.format("%05d", random);
    }

    public Double calculateTotalPrice() {
        double total = 0.0;

        for (TicketLine line : ticketLines) {
            if (line.isProduct() && line.getProduct() != null) {
                Product product = line.getProduct();

                // Si tiene personalizaciones, calcular precio con personalizaciones
                if (product instanceof ProductPersonalized && line.hasPersonalizations()) {
                    ProductPersonalized pp = (ProductPersonalized) product;
                    double surcharge = line.getPersonalizations().size() * pp.getBasePrice() * 0.10;
                    double priceWithPersonalizations = pp.getBasePrice() + surcharge;
                    total += priceWithPersonalizations * line.getQuantity();
                } else {
                    total += product.getBasePrice() * line.getQuantity();
                }
            }
        }

        return total;
    }

    public Double calculateTotalDiscount() {
        double totalPrice = calculateTotalPrice();
        double finalPrice = calculateFinalPrice();
        return totalPrice - finalPrice;
    }

    public Double calculateFinalPrice() {
        Map<TCategory, List<TicketLine>> productsByCategory = new HashMap<>();

        for (TicketLine line : ticketLines) {
            if (line.isProduct() && line.getProduct() != null) {
                Product product = line.getProduct();

                if (product instanceof BasicProduct || product instanceof ProductPersonalized) {
                    TCategory category = product.getCategory();
                    if (category != null) {
                        productsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(line);
                    }
                }
            }
        }

        double finalPrice = 0.0;

        for (TicketLine line : ticketLines) {
            if (line.isProduct() && line.getProduct() != null) {
                Product product = line.getProduct();
                double linePrice = product.getBasePrice() * line.getQuantity();

                if (product.getCategory() != null) {
                    List<TicketLine> categoryLines = productsByCategory.get(product.getCategory());
                    int totalQuantity = categoryLines. stream().mapToInt(TicketLine:: getQuantity).sum();

                    if (totalQuantity >= 2) {
                        double discount = product.getCategory().getDiscount();
                        linePrice = linePrice * (1 - discount);
                    }
                }

                finalPrice += linePrice;
            }
        }

        if (mode == TicketMode.DETAILED && hasServices() && hasProducts()) {
            finalPrice = finalPrice * 0.70;
        }

        return finalPrice;
    }

    public Double calculateProductDiscount(Product product, int quantity) {
        if (product. getCategory() == null) {
            return 0.0;
        }

        Map<TCategory, Integer> quantityByCategory = new HashMap<>();

        for (TicketLine line : ticketLines) {
            if (line.isProduct() && line.getProduct() != null && line.getProduct().getCategory() != null) {
                TCategory category = line.getProduct().getCategory();
                quantityByCategory.put(category, quantityByCategory.getOrDefault(category, 0) + line.getQuantity());
            }
        }

        int totalQuantity = quantityByCategory.getOrDefault(product.getCategory(), 0);

        if (totalQuantity >= 2) {
            double discount = product.getCategory().getDiscount();
            return product.getBasePrice() * quantity * discount;
        }

        return 0.0;
    }

    public boolean hasProducts() {
        return ticketLines.stream().anyMatch(TicketLine::isProduct);
    }

    public boolean hasServices() {
        return ticketLines.stream().anyMatch(TicketLine::isService);
    }

    public String formatForDisplay() {
        StringBuilder sb = new StringBuilder();

        String idToShow = (displayId != null && !displayId.equals(id)) ? displayId : id;
        sb.append("Ticket :  ").append(idToShow).append("\n");

        if (hasServices()) {
            sb.append("Services Included:  \n");
            for (TicketLine line : ticketLines) {
                if (line.isService()) {
                    sb.append("  ").append(line.getServiceProduct().toString()).append("\n");
                }
            }
        }

        boolean hayProductos = hasProducts();
        if (hayProductos && hasServices()) {
            sb.append("Product Included\n");
        }

        if (hayProductos) {
            for (TicketLine line : ticketLines) {
                if (line.isProduct()) {
                    Product product = line.getProduct();

                    double unitDiscount = calculateUnitDiscount(product,line);

                    for (int i = 0; i < line.getQuantity(); i++) {
                        sb.append("  ");
                        if (product instanceof ProductPersonalized && line.hasPersonalizations()) {
                            ProductPersonalized pp = (ProductPersonalized) product;
                            sb.append(pp.toStringWithPersonalizations(line.getPersonalizations()));
                        } else {
                            sb.append(product.toString());
                        }
                        if (unitDiscount > 0) {
                            sb.append(String.format(java.util.Locale.US, " **discount -%.3f", unitDiscount));
                        }
                        sb.append("\n");
                    }
                }
            }

            double totalPrice = calculateTotalPrice();
            double totalDiscount = calculateTotalDiscount();
            double finalPrice = calculateFinalPrice();

            sb.append(String.format(java.util.Locale.US, "  Total price: %.1f\n", totalPrice));
            if (mode == TicketMode.DETAILED && hasServices() && hasProducts()) {
                double extraDiscount = totalPrice * 0.30;
                sb.append(String. format(java.util.Locale.US, "  Extra Discount from services:%.1f **discount -%.1f\n", extraDiscount, extraDiscount));
            }
            sb.append(String. format(java.util.Locale.US, "  Total discount: %.1f\n", totalDiscount));
            sb.append(String.format(java.util. Locale.US, "  Final Price: %.1f", finalPrice));
        } else if (mode == TicketMode. BASIC) {
            sb.append("  Total price: 0.0\n");
            sb.append("  Total discount: 0.0\n");
            sb.append("  Final Price: 0.0");
        }
        return sb.toString();
    }


    private double calculateUnitDiscount(Product product, TicketLine currentLine) {
        if (product.getCategory() == null) {
            return 0.0;
        }

        Map<TCategory, Integer> quantityByCategory = new HashMap<>();

        for (TicketLine line : ticketLines) {
            if (line.isProduct() && line.getProduct() != null && line.getProduct().getCategory() != null) {
                TCategory category = line.getProduct().getCategory();
                quantityByCategory.put(category, quantityByCategory.getOrDefault(category, 0) + line.getQuantity());
            }
        }

        int totalQuantity = quantityByCategory.getOrDefault(product.getCategory(), 0);

        if (totalQuantity >= 2) {
            double discount = product. getCategory().getDiscount();

            if (product instanceof ProductPersonalized && currentLine. hasPersonalizations()) {
                double surcharge = currentLine.getPersonalizations().size() * product.getBasePrice() * 0.10;
                double priceWithPersonalizations = product.getBasePrice() + surcharge;
                return priceWithPersonalizations * discount;
            }

            return product.getBasePrice() * discount;
        }

        return 0.0;
    }

    public void closeTicket() {
        if (state == TicketState.CLOSE) {
            return;
        }

        this.state = TicketState.CLOSE;
        this.closeDate = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd-HH: mm");
        String timestamp = closeDate.format(formatter);
        this.displayId = id + "-" + timestamp;

        if (ticketLines != null && !ticketLines.isEmpty()) {
            ticketLines.sort((line1, line2) -> {
                if (line1.isProduct() && line2.isProduct()) {
                    return line1.getProduct().getName().compareTo(line2.getProduct().getName());
                }
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
