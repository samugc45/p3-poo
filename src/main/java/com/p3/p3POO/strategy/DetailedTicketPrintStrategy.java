package com.p3.p3POO.strategy;

import com.p3.p3POO.model.Ticket;
import com.p3.p3POO.model.TicketLine;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class DetailedTicketPrintStrategy implements TicketPrintStrategy {

    @Override
    public String print(Ticket ticket) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== DETAILED TICKET ==========\n");
        sb.append("ID: ").append(ticket.getId()).append("\n");
        sb.append("Client: ").append(ticket.getClient().getName())
                .append(" (").append(ticket.getClient().getId()).append(")\n");
        sb.append("Cashier: ").append(ticket.getCashier().getName())
                .append(" (").append(ticket.getCashier().getId()).append(")\n");

        // Formatear fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        sb.append("Date: ").append(ticket.getOpenDate().format(formatter)).append("\n");
        sb.append("Mode: ").append(ticket.getMode()).append("\n");

        sb.append("=====================================\n");
        sb.append("PRODUCTS:\n");

        int lineNum = 1;
        double productsTotal = 0.0;

        for (TicketLine line : ticket.getTicketLines()) {
            if (line.isProduct()) {
                String formattedPrice = String.format(Locale.US, "%.2f", line.getTotalPrice());
                sb.append(lineNum).append(". ")
                        .append(line.getProduct().getName())
                        .append(" x").append(line.getQuantity())
                        .append(" - ").append(formattedPrice).append("€\n");
                lineNum++;
                productsTotal += line.getTotalPrice();
            }
        }

        sb.append("-------------------------------------\n");
        String formattedSubtotal = String.format(Locale.US, "%.2f", productsTotal);
        sb.append("Products Subtotal: ").append(formattedSubtotal).append("€\n");

        sb.append("=====================================\n");
        sb.append("SERVICES:\n");

        int serviceNum = 1;
        for (TicketLine line : ticket.getTicketLines()) {
            if (line.isService()) {
                sb.append(serviceNum).append(". Service ")
                        .append(line.getServiceProduct().getId())
                        .append(" (").append(line.getServiceProduct().getServiceType()).append(")")
                        .append(" x").append(line.getQuantity()).append("\n");
                serviceNum++;
            }
        }

        sb.append("=====================================\n");
        String formattedTotal = String.format(Locale.US, "%. 2f", ticket.calculateTotal());
        sb.append("TOTAL: ").append(formattedTotal).append("€\n");
        sb.append("=====================================");

        return sb.toString();
    }
}
