package com.p3.p3POO.infrastructure.command;

import com.p3.p3POO.application.factory.TicketFactory;
import com.p3.p3POO.application.service.*;
import com.p3.p3POO.application.strategy.BasicTicketPrintStrategy;
import com.p3.p3POO.application.strategy.DetailedTicketPrintStrategy;
import com.p3.p3POO.application.strategy.TicketPrintStrategy;
import com.p3.p3POO.application.validator.EventValidator;
import com.p3.p3POO.domain.model.Ticket;
import com.p3.p3POO.domain.model.enums.TCategory;
import com.p3.p3POO.domain.model.enums.ServiceType;
import com.p3.p3POO.domain.model.enums.TicketMode;
import com.p3.p3POO.domain.model.product.*;
import com.p3.p3POO.domain.model.user.Cashier;
import com.p3.p3POO.domain.model.user.Client;
import com.p3.p3POO.domain.model.user.CompanyClient;
import com.p3.p3POO.infrastructure.exception.DomainException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CommandExecutor {

    private final UserService userService;
    private final ProductService productService;
    private final ServiceService serviceService;
    private final TicketService ticketService;
    private final TicketFactory ticketFactory;
    private final EventValidator eventValidator;
    private final BasicTicketPrintStrategy basicPrintStrategy;
    private final DetailedTicketPrintStrategy detailedPrintStrategy;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter. ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CommandExecutor(UserService userService,
                           ProductService productService,
                           ServiceService serviceService,
                           TicketService ticketService,
                           TicketFactory ticketFactory,
                           EventValidator eventValidator,
                           BasicTicketPrintStrategy basicPrintStrategy,
                           DetailedTicketPrintStrategy detailedPrintStrategy) {
        this.userService = userService;
        this.productService = productService;
        this.serviceService = serviceService;
        this. ticketService = ticketService;
        this.ticketFactory = ticketFactory;
        this. eventValidator = eventValidator;
        this.basicPrintStrategy = basicPrintStrategy;
        this. detailedPrintStrategy = detailedPrintStrategy;
    }

    /**
     * Ejecuta un comando y retorna el resultado
     */
    public String execute(String commandLine) {
        if (commandLine == null || commandLine.trim().isEmpty()) {
            return "Error: Empty command";
        }

        String[] parts = commandLine.trim().split("\\s+");
        String command = parts[0]. toLowerCase();

        try {
            switch (command) {
                // ========== USUARIOS ==========
                case "new_cashier":
                    return executeNewCashier(parts);
                case "new_client":
                    return executeNewClient(parts);
                case "new_company_client":
                    return executeNewCompanyClient(parts);
                case "list_cashiers":
                    return executeListCashiers();
                case "list_clients":
                    return executeListClients();

                // ========== PRODUCTOS ==========
                case "new_product":
                    return executeNewProduct(parts);
                case "new_meeting":
                    return executeNewMeeting(parts);
                case "new_food":
                    return executeNewFood(parts);
                case "list_products":
                    return executeListProducts();

                // ========== SERVICIOS ==========
                case "new_service":
                    return executeNewService(parts);
                case "list_services":
                    return executeListServices();

                // ========== TICKETS ==========
                case "new_ticket":
                    return executeNewTicket(parts);
                case "add_product":
                    return executeAddProduct(parts);
                case "add_service":
                    return executeAddService(parts);
                case "close_ticket":
                    return executeCloseTicket(parts);
                case "print_ticket":
                    return executePrintTicket(parts);
                case "list_tickets":
                    return executeListTickets();
                case "ticket_total":
                    return executeTicketTotal(parts);

                // ========== AYUDA ==========
                case "help":
                    return getHelpMessage();

                default:
                    return "Error:  Unknown command '" + command + "'.  Type 'help' for available commands. ";
            }
        } catch (DomainException e) {
            return "Error: " + e.getMessage();
        } catch (ArrayIndexOutOfBoundsException e) {
            return "Error:  Invalid number of arguments for command '" + command + "'";
        } catch (Exception e) {
            return "Error:  " + e.getMessage();
        }
    }

    // ==================== COMANDOS DE USUARIOS ====================

    private String executeNewCashier(String[] parts) {
        // Formato: new_cashier <nombre> <email>
        if (parts.length < 3) {
            return "Usage: new_cashier <name> <email>";
        }

        String name = parts[1];
        String email = parts[2];

        Cashier cashier = userService.createCashier(name, email);
        return String.format("Cashier created: %s (%s)", cashier.getName(), cashier.getEmployeeCode());
    }

    private String executeNewClient(String[] parts) {
        // Formato: new_client <dni> <nombre> <email> <id_cajero>
        if (parts.length < 5) {
            return "Usage:  new_client <dni> <name> <email> <cashier_id>";
        }

        String dni = parts[1];
        String name = parts[2];
        String email = parts[3];
        String cashierId = parts[4];

        Client client = userService. createClient(dni, name, email, cashierId);
        return String.format("Client created: %s (%s)", client.getName(), client.getDni());
    }

    private String executeNewCompanyClient(String[] parts) {
        // Formato: new_company_client <nif> <nombre_empresa> <email> <id_cajero>
        if (parts.length < 5) {
            return "Usage:  new_company_client <nif> <company_name> <email> <cashier_id>";
        }

        String nif = parts[1];
        String companyName = parts[2];
        String email = parts[3];
        String cashierId = parts[4];

        CompanyClient companyClient = userService. createCompanyClient(nif, companyName, email, cashierId);
        return String.format("Company client created: %s (%s)", companyClient.getCompanyName(), companyClient.getNif());
    }

    private String executeListCashiers() {
        List<Cashier> cashiers = userService.findAllCashiers();

        if (cashiers.isEmpty()) {
            return "No cashiers found. ";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("CASHIERS:\n");
        sb.append("-".repeat(50)).append("\n");
        for (Cashier cashier : cashiers) {
            sb.append(String.format("%s - %s (%s)\n",
                    cashier. getEmployeeCode(),
                    cashier.getName(),
                    cashier.getEmail()));
        }
        return sb.toString();
    }

    private String executeListClients() {
        List<Client> clients = userService.findAllClients();

        if (clients.isEmpty()) {
            return "No clients found.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("CLIENTS:\n");
        sb.append("-".repeat(50)).append("\n");
        for (Client client : clients) {
            String type = (client instanceof CompanyClient) ? "COMPANY" : "NORMAL";
            sb.append(String.format("%s - %s (%s) [%s]\n",
                    client.getId(),
                    client.getName(),
                    client.getEmail(),
                    type));
        }
        return sb.toString();
    }

    // ==================== COMANDOS DE PRODUCTOS ====================

    private String executeNewProduct(String[] parts) {
        // Formato:  new_product <id> <nombre> <precio> <categoria>
        if (parts.length < 5) {
            return "Usage:  new_product <id> <name> <price> <category>";
        }

        String id = parts[1];
        String name = parts[2];
        Double price = Double.parseDouble(parts[3]);
        TCategory category = TCategory.valueOf(parts[4]. toUpperCase());

        BasicProduct product = productService.createBasicProduct(id, name, price, category);
        return String. format("Product created: %s (%s) - %. 2f€ [%s]",
                product.getName(), product.getId(), product.getBasePrice(), product.getCategory());
    }

    private String executeNewMeeting(String[] parts) {
        // Formato: new_meeting <id> <nombre> <precio> <fecha_hora> <max_participantes>
        if (parts.length < 6) {
            return "Usage: new_meeting <id> <name> <price> <datetime(yyyy-MM-dd HH: mm)> <max_participants>";
        }

        String id = parts[1];
        String name = parts[2];
        Double price = Double.parseDouble(parts[3]);
        LocalDateTime eventDate = LocalDateTime.parse(parts[4] + " " + parts[5], DATE_TIME_FORMATTER);
        Integer maxParticipants = Integer.parseInt(parts[6]);

        // Validar con EventValidator
        eventValidator.validateMeetingCreation(eventDate);

        MeetingProduct meeting = productService.createMeetingProduct(id, name, price, eventDate, maxParticipants);
        return String.format("Meeting created: %s (%s) - %.2f€ - Date: %s - Max: %d",
                meeting.getName(), meeting.getId(), meeting.getBasePrice(),
                eventDate.format(DATE_TIME_FORMATTER), meeting.getMaxParticipants());
    }

    private String executeNewFood(String[] parts) {
        // Formato:  new_food <id> <nombre> <precio> <fecha_hora> <max_participantes> <fecha_caducidad>
        if (parts.length < 8) {
            return "Usage:  new_food <id> <name> <price> <datetime(yyyy-MM-dd HH:mm)> <max_participants> <expiration_date(yyyy-MM-dd)>";
        }

        String id = parts[1];
        String name = parts[2];
        Double price = Double.parseDouble(parts[3]);
        LocalDateTime eventDate = LocalDateTime.parse(parts[4] + " " + parts[5], DATE_TIME_FORMATTER);
        Integer maxParticipants = Integer.parseInt(parts[6]);
        LocalDate expirationDate = LocalDate.parse(parts[7], DATE_FORMATTER);

        // Validar con EventValidator
        eventValidator.validateFoodCreation(eventDate, expirationDate);

        FoodProduct food = productService.createFoodProduct(id, name, price, eventDate, maxParticipants, expirationDate);
        return String.format("Food event created: %s (%s) - %.2f€ - Date: %s - Expiration: %s",
                food.getName(), food.getId(), food.getBasePrice(),
                eventDate.format(DATE_TIME_FORMATTER), expirationDate);
    }

    private String executeListProducts() {
        List<Product> products = productService.findAllProducts();

        if (products.isEmpty()) {
            return "No products found.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("PRODUCTS:\n");
        sb.append("-".repeat(80)).append("\n");
        for (Product product : products) {
            String type = product. getClass().getSimpleName();
            sb.append(String.format("%s - %s (%. 2f€) [%s]\n",
                    product.getId(),
                    product.getName(),
                    product.getBasePrice(),
                    type));
        }
        return sb.toString();
    }

    // ==================== COMANDOS DE SERVICIOS ====================

    private String executeNewService(String[] parts) {
        // Formato: new_service <tipo> <fecha_max_uso>
        if (parts.length < 3) {
            return "Usage: new_service <type(TRANSPORT|EVENT|INSURANCE)> <max_usage_date(yyyy-MM-dd)>";
        }

        ServiceType type = ServiceType.valueOf(parts[1].toUpperCase());
        LocalDate maxUsageDate = LocalDate.parse(parts[2], DATE_FORMATTER);

        com.p3.p3POO.domain.model.service.Service service = serviceService.createService(type, maxUsageDate);
        return String.format("Service created: %s - Type: %s - Max usage: %s",
                service.getId(), service.getServiceType(), service.getMaxUsageDate());
    }

    private String executeListServices() {
        List<com.p3.p3POO.domain.model.service.Service> services = serviceService.findAllServices();

        if (services.isEmpty()) {
            return "No services found.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("SERVICES:\n");
        sb.append("-".repeat(50)).append("\n");
        for (com.p3.p3POO.domain.model.service.Service service : services) {
            sb.append(String.format("%s - %s - Max usage: %s\n",
                    service.getId(),
                    service.getServiceType(),
                    service.getMaxUsageDate()));
        }
        return sb.toString();
    }

    // ==================== COMANDOS DE TICKETS ====================

    private String executeNewTicket(String[] parts) {
        // Formato: new_ticket <id_cajero> <id_cliente>
        if (parts.length < 3) {
            return "Usage: new_ticket <cashier_id> <client_id>";
        }

        String cashierId = parts[1];
        String clientId = parts[2];

        Ticket ticket = ticketFactory.createTicket(cashierId, clientId);
        return String.format("Ticket created: %s - Mode: %s - State: %s",
                ticket.getId(), ticket.getMode(), ticket.getState());
    }

    private String executeAddProduct(String[] parts) {
        // Formato: add_product <id_ticket> <id_producto> <cantidad>
        if (parts.length < 4) {
            return "Usage: add_product <ticket_id> <product_id> <quantity>";
        }

        String ticketId = parts[1];
        String productId = parts[2];
        Integer quantity = Integer.parseInt(parts[3]);

        ticketService.addProductToTicket(ticketId, productId, quantity);
        return String.format("Product %s (x%d) added to ticket %s", productId, quantity, ticketId);
    }

    private String executeAddService(String[] parts) {
        // Formato:  add_service <id_ticket> <id_servicio> <cantidad>
        if (parts.length < 4) {
            return "Usage: add_service <ticket_id> <service_id> <quantity>";
        }

        String ticketId = parts[1];
        String serviceId = parts[2];
        Integer quantity = Integer.parseInt(parts[3]);

        ticketService.addServiceToTicket(ticketId, serviceId, quantity);
        return String.format("Service %s (x%d) added to ticket %s", serviceId, quantity, ticketId);
    }

    private String executeCloseTicket(String[] parts) {
        // Formato: close_ticket <id_ticket>
        if (parts. length < 2) {
            return "Usage: close_ticket <ticket_id>";
        }

        String ticketId = parts[1];
        Ticket ticket = ticketService.closeTicket(ticketId);

        return String.format("Ticket closed: %s - Total: %. 2f€",
                ticket.getId(), ticket.calculateTotal());
    }

    private String executePrintTicket(String[] parts) {
        // Formato: print_ticket <id_ticket>
        if (parts.length < 2) {
            return "Usage: print_ticket <ticket_id>";
        }

        String ticketId = parts[1];
        Ticket ticket = ticketService. findTicketById(ticketId);

        // Seleccionar estrategia según el modo del ticket
        TicketPrintStrategy strategy = (ticket.getMode() == TicketMode.DETAILED)
                ? detailedPrintStrategy
                : basicPrintStrategy;

        return strategy.print(ticket);
    }

    private String executeListTickets() {
        List<Ticket> tickets = ticketService.findAllTickets();

        if (tickets.isEmpty()) {
            return "No tickets found.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("TICKETS:\n");
        sb.append("-".repeat(80)).append("\n");
        for (Ticket ticket : tickets) {
            sb.append(String. format("%s - State: %s - Mode: %s - Total: %.2f€ - Client: %s\n",
                    ticket.getId(),
                    ticket.getState(),
                    ticket.getMode(),
                    ticket.calculateTotal(),
                    ticket.getClient().getName()));
        }
        return sb.toString();
    }

    private String executeTicketTotal(String[] parts) {
        // Formato: ticket_total <id_ticket>
        if (parts.length < 2) {
            return "Usage: ticket_total <ticket_id>";
        }

        String ticketId = parts[1];
        Double total = ticketService.calculateTicketTotal(ticketId);

        return String.format("Ticket %s - Total: %.2f€", ticketId, total);
    }

    // ==================== AYUDA ====================

    private String getHelpMessage() {
        return """
            AVAILABLE COMMANDS:
            
            === USERS ===
            new_cashier <name> <email>
            new_client <dni> <name> <email> <cashier_id>
            new_company_client <nif> <company_name> <email> <cashier_id>
            list_cashiers
            list_clients
            
            === PRODUCTS ===
            new_product <id> <name> <price> <category>
            new_meeting <id> <name> <price> <datetime> <max_participants>
            new_food <id> <name> <price> <datetime> <max_participants> <expiration_date>
            list_products
            
            === SERVICES ===
            new_service <type> <max_usage_date>
            list_services
            
            === TICKETS ===
            new_ticket <cashier_id> <client_id>
            add_product <ticket_id> <product_id> <quantity>
            add_service <ticket_id> <service_id> <quantity>
            close_ticket <ticket_id>
            print_ticket <ticket_id>
            list_tickets
            ticket_total <ticket_id>
            
            === OTHER ===
            help
            """;
    }
}
