package com.p3.p3POO.application.factory;

import com.p3.p3POO.application.service.UserService;
import com.p3.p3POO.application.service.impl.UserServiceImpl;
import com.p3.p3POO.domain.model.Ticket;
import com.p3.p3POO.domain.model.interfaces.IdGenerable;
import com.p3.p3POO.domain.model.user.Cashier;
import com.p3.p3POO.domain.model.user.Client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class TicketFactory implements IdGenerable {
    UserServiceImpl userService;
    public Ticket ticketNewComunes(String ticketId, String cashId, String clientId)
            throws CashierNotFoundException, DuplicatedIdException, ClientNotFoundException {
        Cashier cashier = u.getCashier(cashId);
        Client client = userService.getClient(clientId);
        if (cashier == null) {
            throw new CashierNotFoundException(cashId);
        }
        if (client == null) {
            throw new ClientNotFoundException(clientId);
        }
        if (ticketId == null || ticketId.isBlank()) {
            ticketId = generateId();
        } else {
            String timestamp = generateId();
            ticketId = ticketId + "-" + timestamp;
        }
        if (tickets.containsKey(ticketId))
            throw new DuplicatedIdException(ticketId);

        Ticket newTicket = new Ticket(ticketId);
        tickets.put(ticketId, newTicket);

        cashier.addTicket(newTicket);
        client.addTicket(newTicket);
        StringBuilder sb = new StringBuilder();
        String ticketManualId = newTicket.getId(). split("-")[0];
        sb.append("Ticket : ").append(ticketManualId). append("\n");
        System.out.print(sb);
        return newTicket;
    }

    public Ticket ticketNewEmpresa();

    @Override
    public String generateId() {
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm"));
        int random = ThreadLocalRandom.current().nextInt(0, 100000);
        return date + "-" + String.format("%05d", random);
    }
}
