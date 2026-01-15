package com.p3.p3POO.strategy;

import com.p3.p3POO.model.Ticket;
import com.p3.p3POO.model.TicketLine;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class BasicTicketPrintStrategy implements TicketPrintStrategy {

    @Override
    public String print(Ticket ticket) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== TICKET ==========\n");
        sb.append("ID: ").append(ticket.getId()).append("\n");
        sb.append("Client: ").append(ticket.getClient().getName()).append("\n");
        sb.append("Cashier: ").append(ticket.getCashier().getName()).append("\n");

        // Formatear fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        sb.append("Date: ").append(ticket.getOpenDate().format(formatter)).append("\n");

        sb.append("============================\n");

        int lineNum = 1;
        for (TicketLine line :  ticket.getTicketLines()) {
            if (line.isProduct()) {
                // Formatear línea por línea sin el símbolo € dentro del format
                String formattedPrice = String.format(Locale. US, "%.2f", line.getTotalPrice());
                sb.append(lineNum).append(". ")
                        .append(line.getProduct().getName())
                        .append(" x").append(line.getQuantity())
                        .append(" - ").append(formattedPrice).append("€\n");
                lineNum++;
            }
        }

        sb.append("============================\n");

        // Formatear total
        String formattedTotal = String.format(Locale.US, "%.2f", ticket.calculateTotal());
        sb.append("TOTAL:  ").append(formattedTotal).append("€\n");

        sb.append("============================");

        return sb.toString();
    }
}
