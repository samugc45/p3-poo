package com.p3.p3POO.application.service;

import com.p3.p3POO.domain.model.Ticket;
import com.p3.p3POO.domain.model.enums.TicketMode;
import com.p3.p3POO.domain.model.enums.TicketState;

import java.util.List;

public interface TicketService {

    Ticket createTicket(String cashierId, String clientId, TicketMode mode);
    Ticket createTicketWithId(String ticketId, String cashierId, String clientId, TicketMode mode);
    Ticket findTicketById(String ticketId);
    List<Ticket> findAllTickets();
    List<Ticket> findTicketsByCashier(String cashierId);
    List<Ticket> findTicketsByClient(String clientId);
    List<Ticket> findTicketsByState(TicketState state);

    void addProductToTicket(String ticketId, String productId, Integer quantity);
    void addPersonalizedProductToTicket(String ticketId, String productId, Integer quantity, List<String> personalizations);
    void addServiceToTicket(String ticketId, String serviceId, Integer quantity);

    void removeLineFromTicket(String ticketId, int lineNumber);
    void removeProductFromTicket(String ticketId, String productId);


    Ticket closeTicket(String ticketId);
    Double calculateTicketTotal(String ticketId);

    String printTicket(String ticketId);

}
