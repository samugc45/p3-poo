package com.p3.p3POO.service.impl;

import com.p3.p3POO.model.service.ServiceProduct;
import com.p3.p3POO.service.TicketService;
import com.p3.p3POO.service.UserService;
import com.p3.p3POO.service.ProductService;
import com.p3.p3POO.service.ServiceServiceProduct;
import com.p3.p3POO.strategy.BasicTicketPrintStrategy;
import com.p3.p3POO.strategy.DetailedTicketPrintStrategy;
import com.p3.p3POO.model.Ticket;
import com.p3.p3POO.model.TicketLine;
import com.p3.p3POO.model.enums.TicketMode;
import com.p3.p3POO.model.enums.TicketState;
import com.p3.p3POO.model.product.Product;
import com.p3.p3POO.model.product.ProductPersonalized;
import com.p3.p3POO.model.user.Cashier;
import com.p3.p3POO.model.user.Client;
import com.p3.p3POO.repository.TicketRepository;
import com.p3.p3POO.exception.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final ProductService productService;
    private final ServiceServiceProduct serviceServiceProduct;

    private final BasicTicketPrintStrategy basicPrintStrategy;
    private final DetailedTicketPrintStrategy detailedPrintStrategy;

    public TicketServiceImpl(TicketRepository ticketRepository, UserService userService, ProductService productService, ServiceServiceProduct serviceServiceProduct, BasicTicketPrintStrategy basicPrintStrategy, DetailedTicketPrintStrategy detailedPrintStrategy) {
        this.ticketRepository = ticketRepository;
        this.userService = userService;
        this.productService = productService;
        this.serviceServiceProduct = serviceServiceProduct;
        this.basicPrintStrategy = basicPrintStrategy;
        this.detailedPrintStrategy = detailedPrintStrategy;
    }

    @Override
    public Ticket createTicket(String cashierId, String clientId, TicketMode mode) {
        Cashier cashier = userService.findCashierById(cashierId);
        Client client = userService.findClientById(clientId);

        String ticketId = Ticket.generateId();
        while (ticketRepository.existsById(ticketId)) {
            ticketId = Ticket.generateId();
        }

        Ticket ticket = new Ticket(ticketId, cashier, client, mode);
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket createTicketWithId(String ticketId, String cashierId, String clientId, TicketMode mode) {
        // Validar que el ID no exista
        if (ticketRepository.existsById(ticketId)) {
            throw new DomainException("Ticket already exists:  " + ticketId);
        }

        Cashier cashier = userService.findCashierById(cashierId);
        Client client = userService.findClientById(clientId);

        Ticket ticket = new Ticket(ticketId, cashier, client, mode);
        return ticketRepository.save(ticket);
    }

    @Override
    public void addPersonalizedProductToTicket(String ticketId, String productId, Integer quantity, List<String> personalizations) {
        Ticket ticket = findTicketById(ticketId);

        if (ticket. getState() == TicketState.CLOSE) {
            throw new DomainException("Cannot add products to a closed ticket");
        }

        Product product = productService.findProductById(productId);

        if (!(product instanceof ProductPersonalized)) {
            throw new DomainException("Product is not personalizable:  " + productId);
        }

        ProductPersonalized personalizable = (ProductPersonalized) product;

        // Validar que no supere el máximo de personalizaciones
        if (personalizations.size() > personalizable.getMaxPersonalizations()) {
            throw new DomainException("Exceeded max personalizations:  " + personalizable.getMaxPersonalizations());
        }

        // Crear quantity líneas, cada una con las personalizaciones
        for (int i = 0; i < quantity; i++) {
            TicketLine line = new TicketLine(ticket, personalizable, 1, personalizations);
            ticket.addLine(line);
        }

        ticketRepository.save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public Ticket findTicketById(String ticketId) {
        return ticketRepository.findById(ticketId).orElseThrow(() -> new DomainException("Ticket not found: " + ticketId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findTicketsByCashier(String cashierId) {
        Cashier cashier = userService.findCashierById(cashierId);
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

        Product product = productService.findProductById(productId);

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

        ServiceProduct serviceProduct = serviceServiceProduct.findServiceById(serviceId);

        TicketLine line = new TicketLine(ticket, serviceProduct, quantity);
        ticket.addLine(line);

        ticketRepository.save(ticket);
    }

    @Override
    public void removeLineFromTicket(String ticketId, int lineNumber) {
        Ticket ticket = findTicketById(ticketId);

        if (ticket. getState() == TicketState.CLOSE) {
            throw new DomainException("Cannot remove lines from a closed ticket");
        }

        List<TicketLine> lines = ticket. getTicketLines();

        // lineNumber empieza en 1 (no en 0)
        if (lineNumber < 1 || lineNumber > lines.size()) {
            throw new DomainException("Invalid line number: " + lineNumber);
        }

        TicketLine lineToRemove = lines.get(lineNumber - 1);
        ticket.removeLine(lineToRemove);

        ticketRepository.save(ticket);
    }

    @Override
    public void removeProductFromTicket(String ticketId, String productId) {
        Ticket ticket = findTicketById(ticketId);

        if (ticket.getState() == TicketState.CLOSE) {
            throw new DomainException("Cannot remove products from a closed ticket");
        }

        // Encontrar y eliminar TODAS las líneas con ese producto
        List<TicketLine> linesToRemove = ticket.getTicketLines().stream().filter(line -> line.isProduct() && line.getProduct().getId().equals(productId)).collect(Collectors.toList());

        if (linesToRemove.isEmpty()) {
            throw new DomainException("Product not found in ticket:  " + productId);
        }

        // Eliminar todas las líneas
        for (TicketLine line : linesToRemove) {
            ticket.removeLine(line);
        }

        ticketRepository.save(ticket);
    }


    @Override
    public String printTicket(String ticketId) {
        Ticket ticket = findTicketById(ticketId);

        // Elegir estrategia según el modo del ticket
        if (ticket.getMode() == TicketMode.BASIC) {
            return basicPrintStrategy.print(ticket);
        } else {
            return detailedPrintStrategy.print(ticket);
        }
    }

    @Override
    public Ticket closeTicket(String ticketId) {
        Ticket ticket = findTicketById(ticketId);
        ticket.close();
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateTicketTotal(String ticketId) {
        Ticket ticket = findTicketById(ticketId);
        return ticket.calculateTotal();
    }
}
