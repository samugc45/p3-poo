package com.p3.p3POO.application.strategy;

import com.p3.p3POO.domain.model.Ticket;
import com.p3.p3POO.domain.model.TicketLine;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Estrategia de impresión DETALLADA
 * Para tickets de empresa (productos + servicios)
 */
@Component
public class DetailedTicketPrintStrategy implements TicketPrintStrategy {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public String print(Ticket ticket) {
        StringBuilder sb = new StringBuilder();

        // Encabezado
        sb.append("=".repeat(60)).append("\n");
        sb.append("             TICKET EMPRESA - DETALLADO\n");
        sb.append("=".repeat(60)).append("\n");
        sb.append(String.format("ID: %s\n", ticket.getId()));
        sb.append(String.format("Estado: %s\n", ticket.getState()));
        sb.append(String.format("Modo: %s\n", ticket. getMode()));
        sb.append(String.format("Fecha apertura: %s\n", ticket.getOpenDate().format(DATE_FORMATTER)));
        if (ticket.getCloseDate() != null) {
            sb.append(String.format("Fecha cierre: %s\n", ticket.getCloseDate().format(DATE_FORMATTER)));
        }
        sb.append("-".repeat(60)).append("\n");

        // Cliente empresa
        sb.append("CLIENTE EMPRESA:\n");
        sb.append(String.format("  Nombre: %s\n", ticket.getClient().getName()));
        sb.append(String.format("  NIF: %s\n", ticket.getClient().getId()));
        sb.append(String.format("  Email: %s\n", ticket. getClient().getEmail()));

        // Cajero
        sb.append(String.format("Cajero: %s (%s)\n",
                ticket. getCashier().getName(),
                ticket.getCashier().getEmployeeCode()));

        sb.append("-".repeat(60)).append("\n");

        // Sección de PRODUCTOS
        sb.append("PRODUCTOS:\n");
        boolean hasProducts = false;
        for (TicketLine line : ticket. getTicketLines()) {
            if (line.isProduct()) {
                hasProducts = true;
                sb. append(String.format("  %-40s x%d  %10.2f€\n",
                        line.getItemName(),
                        line.getQuantity(),
                        line.getTotalPrice()));
            }
        }
        if (!hasProducts) {
            sb.append("  (ninguno)\n");
        }

        sb.append("\n");

        // Sección de SERVICIOS
        sb.append("SERVICIOS CONTRATADOS:\n");
        boolean hasServices = false;
        for (TicketLine line : ticket.getTicketLines()) {
            if (line.isService()) {
                hasServices = true;
                sb.append(String. format("  %-40s x%d  (facturación posterior)\n",
                        line. getItemName(),
                        line.getQuantity()));
            }
        }
        if (! hasServices) {
            sb.append("  (ninguno)\n");
        }

        sb.append("-".repeat(60)).append("\n");

        // Total (solo productos)
        sb.append(String.format("SUBTOTAL PRODUCTOS: %.2f€\n", ticket.calculateTotal()));
        sb.append("SERVICIOS: A facturar\n");
        sb.append("=".repeat(60)).append("\n");

        return sb. toString();
    }
}
