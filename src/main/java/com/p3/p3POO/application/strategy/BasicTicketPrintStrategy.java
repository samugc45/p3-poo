package com.p3.p3POO.application.strategy;

import com.p3.p3POO.domain.model.Ticket;
import com.p3.p3POO.domain.model.TicketLine;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Estrategia de impresión BÁSICA
 * Para tickets normales (solo productos)
 */
@Component
public class BasicTicketPrintStrategy implements TicketPrintStrategy {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public String print(Ticket ticket) {
        StringBuilder sb = new StringBuilder();

        // Encabezado
        sb.append("=". repeat(50)).append("\n");
        sb.append("                 TICKET\n");
        sb.append("=".repeat(50)).append("\n");
        sb.append(String.format("ID: %s\n", ticket. getId()));
        sb.append(String.format("Estado: %s\n", ticket.getState()));
        sb.append(String. format("Fecha apertura: %s\n", ticket.getOpenDate().format(DATE_FORMATTER)));
        if (ticket.getCloseDate() != null) {
            sb.append(String.format("Fecha cierre: %s\n", ticket.getCloseDate().format(DATE_FORMATTER)));
        }
        sb. append("-".repeat(50)).append("\n");

        // Cliente
        sb.append(String.format("Cliente: %s (%s)\n",
                ticket.getClient().getName(),
                ticket.getClient().getId()));

        // Cajero
        sb.append(String.format("Cajero: %s (%s)\n",
                ticket.getCashier().getName(),
                ticket.getCashier().getEmployeeCode()));

        sb.append("-".repeat(50)).append("\n");

        // Líneas de productos
        sb.append("PRODUCTOS:\n");
        for (TicketLine line : ticket. getTicketLines()) {
            if (line.isProduct()) {
                sb.append(String.format("  %-30s x%d  %8. 2f€\n",
                        line.getItemName(),
                        line.getQuantity(),
                        line.getTotalPrice()));
            }
        }

        sb.append("-".repeat(50)).append("\n");

        // Total
        sb.append(String. format("TOTAL: %. 2f€\n", ticket. calculateTotal()));
        sb.append("=".repeat(50)).append("\n");

        return sb.toString();
    }
}
