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

    public Ticket createTicket(String cashierId, String clientId) {
        Client client = userService.findClientById(clientId);

        TicketMode mode = (client instanceof CompanyClient) ? TicketMode.DETAILED : TicketMode.BASIC;

        return ticketService.createTicket(cashierId, clientId, mode);
    }

    public Ticket createTicketWithMode(String cashierId, String clientId, TicketMode mode) {
        Client client = userService.findClientById(clientId);

        if (!(client instanceof CompanyClient) && mode == TicketMode. DETAILED) {
            throw new DomainException("DETAILED tickets (with services) are only allowed for company clients");
        }

        return ticketService.createTicket(cashierId, clientId, mode);
    }
}