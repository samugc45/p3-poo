package com.p3.p3POO.application. service.impl;

import com.p3.p3POO.application.service.TicketService;
import com.p3.p3POO.application.service.UserService;
import com.p3.p3POO.application.service.ProductService;
import com.p3.p3POO.application.service.ServiceService;
import com.p3.p3POO.domain.model.Ticket;
import com.p3.p3POO.domain.model.TicketLine;
import com.p3.p3POO.domain.model.enums.TicketMode;
import com.p3.p3POO.domain.model.enums.TicketState;
import com.p3.p3POO.domain.model.product.Product;
import com.p3.p3POO.domain.model.user.Cashier;
import com.p3.p3POO.domain.model.user.Client;
import com.p3.p3POO.domain.repository.TicketRepository;
import com.p3.p3POO.infrastructure.exception.DomainException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final ProductService productService;
    private final ServiceService serviceService;

    public TicketServiceImpl(TicketRepository ticketRepository,UserService userService, ProductService productService, ServiceService serviceService) {
        this.ticketRepository = ticketRepository;
        this.userService = userService;
        this.productService = productService;
        this.serviceService = serviceService;
    }

    @Override
    public Ticket createTicket(String cashierId, String clientId, TicketMode mode) {
        Cashier cashier = userService. findCashierById(cashierId);
        Client client = userService.findClientById(clientId);

        String ticketId = Ticket.generateId();
        while (ticketRepository.existsById(ticketId)) {
            ticketId = Ticket. generateId();
        }

        Ticket ticket = new Ticket(ticketId, cashier, client, mode);
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public Ticket findTicketById(String ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new DomainException("Ticket not found: " + ticketId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findTicketsByCashier(String cashierId) {
        Cashier cashier = userService. findCashierById(cashierId);
        return ticketRepository.findByCashier(cashier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findTicketsByClient(String clientId) {
        Client client = userService.findClientById(clientId);
        return ticketRepository.findByClient(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findTicketsByState(TicketState state) {
        return ticketRepository.findByState(state);
    }

    @Override
    public void addProductToTicket(String ticketId, String productId, Integer quantity) {
        Ticket ticket = findTicketById(ticketId);

        if (ticket.getState() == TicketState.CLOSE) {
            throw new DomainException("Cannot add products to a closed ticket");
        }

        Product product = productService. findProductById(productId);

        TicketLine line = new TicketLine(ticket, product, quantity);
        ticket.addLine(line);

        ticketRepository.save(ticket);
    }

    @Override
    public void addServiceToTicket(String ticketId, String serviceId, Integer quantity) {
        Ticket ticket = findTicketById(ticketId);

        if (ticket.getState() == TicketState.CLOSE) {
            throw new DomainException("Cannot add services to a closed ticket");
        }

        if (ticket.getMode() != TicketMode.DETAILED) {
            throw new DomainException("Services can only be added to DETAILED tickets (company clients)");
        }

        com.p3.p3POO.domain.model.service.Service service = serviceService.findServiceById(serviceId);

        TicketLine line = new TicketLine(ticket, service, quantity);
        ticket.addLine(line);

        ticketRepository.save(ticket);
    }

    @Override
    public Ticket closeTicket(String ticketId) {
        Ticket ticket = findTicketById(ticketId);
        ticket.close();
        return ticketRepository. save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateTicketTotal(String ticketId) {
        Ticket ticket = findTicketById(ticketId);
        return ticket.calculateTotal();
    }
}
