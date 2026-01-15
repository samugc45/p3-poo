package com.p3.p3POO.factory;

import com.p3.p3POO.service.TicketService;
import com.p3.p3POO.service.UserService;
import com.p3.p3POO.model.Ticket;
import com.p3.p3POO.model.enums.TicketMode;
import com.p3.p3POO.model.user.Client;
import com.p3.p3POO.model.user.CompanyClient;
import com.p3.p3POO.exception.DomainException;
import org.springframework.stereotype.Component;

@Component
public class TicketFactory {

    private final TicketService ticketService;
    private final UserService userService;

    public TicketFactory(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this. userService = userService;
    }

    /**
     * Crea un ticket determinando automáticamente el modo según el tipo de cliente
     * - BASIC: Para clientes normales (solo productos)
     * - DETAILED: Para clientes empresa (productos + servicios)
     */
    public Ticket createTicket(String cashierId, String clientId) {
        // Verificar que el cliente existe
        Client client = userService.findClientById(clientId);

        // Determinar el modo según el tipo de cliente
        TicketMode mode = (client instanceof CompanyClient) ? TicketMode.DETAILED : TicketMode.BASIC;

        // Crear el ticket con el modo apropiado
        return ticketService.createTicket(cashierId, clientId, mode);
    }

    /**
     * Crea un ticket forzando un modo específico
     * (útil para casos especiales)
     */
    public Ticket createTicketWithMode(String cashierId, String clientId, TicketMode mode) {
        Client client = userService.findClientById(clientId);

        // Validar que clientes normales no pueden tener tickets DETAILED
        if (!(client instanceof CompanyClient) && mode == TicketMode. DETAILED) {
            throw new DomainException("DETAILED tickets (with services) are only allowed for company clients");
        }

        return ticketService.createTicket(cashierId, clientId, mode);
    }
}