package com.p3.p3POO.infrastructure.command;

import com. p3.p3POO.application.factory.TicketFactory;
import com.p3.p3POO.application.service.*;
import com.p3.p3POO.application.strategy.BasicTicketPrintStrategy;
import com.p3.p3POO.application. strategy.DetailedTicketPrintStrategy;
import com.p3.p3POO.application.validator.EventValidator;
import com.p3.p3POO.domain.model.Ticket;
import com.p3.p3POO.domain.model.enums.ServiceType;
import com.p3.p3POO.domain.model.enums.TCategory;
import com.p3.p3POO.domain.model.enums.TicketMode;
import com.p3.p3POO.domain.model. TicketLine;
import com.p3.p3POO.domain.model.product.*;
import com.p3.p3POO.domain.model.service.Service;
import com.p3.p3POO.domain.model.user.Cashier;
import com.p3.p3POO.domain.model.user.Client;
import com.p3.p3POO.domain.model.user.CompanyClient;
import com.p3.p3POO.infrastructure.exception.DomainException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        if (commandLine == null || commandLine. trim().isEmpty()) {
            return "";
        }

        // Parsear la línea de comando
        List<String> tokens = CommandParser.parse(commandLine. trim());

        if (tokens.isEmpty()) {
            return "";
        }

        String command = tokens.get(0).toLowerCase();

        try {
            switch (command) {
                // ========== ECHO ==========
                case "echo":
                    return executeEcho(tokens);

                // ========== AYUDA ==========
                case "help":
                    return getHelpMessage();

                // ========== SALIR ==========
                case "exit":
                    return "exit"; // Señal especial para ConsoleRunner

                // ========== CASH ==========
                case "cash":
                    return executeCash(tokens);

                // ========== CLIENT ==========
                case "client":
                    return executeClient(tokens);

                // ========== PROD ==========
                case "prod":
                    return executeProd(tokens);
                // ========== TICKET ==========
                case "ticket":
                    return executeTicket(tokens);

                default:
                    return "Unknown command: '" + command + "'.  Type 'help' for available commands.";
            }
        } catch (DomainException e) {
            return "Error: " + e.getMessage();
        } catch (IndexOutOfBoundsException e) {
            return "Error: Invalid number of arguments for command '" + command + "'";
        } catch (Exception e) {
            return "Error processing ->" + command + " ->" + e.getMessage();
        }
    }

    // ==================== COMANDO ECHO ====================

    private String executeEcho(List<String> tokens) {
        // Formato: echo "texto"
        if (tokens.size() < 2) {
            return "Usage: echo \"<text>\"";
        }

        // El texto está en tokens. get(1) (ya sin comillas por el parser)
        return "\"" + tokens.get(1) + "\"";
    }

    // ==================== COMANDO CASH ====================

    private String executeCash(List<String> tokens) {
        if (tokens.size() < 2) {
            return "Usage: cash add|list|remove|tickets [args]";
        }

        String subcommand = tokens.get(1).toLowerCase();

        switch (subcommand) {
            case "add":
                return executeCashAdd(tokens);
            case "list":
                return executeCashList(tokens);
            case "remove":
                return executeCashRemove(tokens);
            case "tickets":
                return executeCashTickets(tokens);
            default:
                return "Unknown cash subcommand: " + subcommand;
        }
    }
    private String executeCashAdd(List<String> tokens) {
        // Formato 1: cash add <id> "<nombre>" <email>
        // Formato 2: cash add "<nombre>" <email>  (ID autogenerado)

        String id = null;
        String name;
        String email;

        if (tokens.size() == 5) {
            // Formato 1: con ID
            id = tokens.get(2);
            name = tokens.get(3);
            email = tokens. get(4);
        } else if (tokens.size() == 4) {
            // Formato 2: sin ID (autogenerado)
            name = tokens.get(2);
            email = tokens.get(3);
        } else {
            return "Usage: cash add [<id>] \"<nombre>\" <email>";
        }

        try {
            Cashier cashier;

            if (id != null) {
                cashier = userService.createCashier(id, name, email);
            } else {
                cashier = userService.createCashierAutoId(name, email);
            }

            return cashier. toString() + "\ncash add: ok";

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }


    private String executeCashList(List<String> tokens) {
        List<Cashier> cashiers = userService.findAllCashiers();

        if (cashiers.isEmpty()) {
            return "Cash:\ncash list: ok";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Cash:\n");

        // Ordenar por ID
        cashiers. sort((c1, c2) -> c1.getEmployeeCode().compareTo(c2.getEmployeeCode()));

        for (Cashier cashier : cashiers) {
            sb.append("  ").append(cashier.toString()).append("\n");
        }

        sb.append("cash list: ok");
        return sb.toString();
    }

    private String executeCashRemove(List<String> tokens) {
        // Formato:  cash remove <id>
        if (tokens.size() < 3) {
            return "Usage:  cash remove <id>";
        }

        String id = tokens.get(2);

        try {
            userService.deleteCashier(id);
            return "cash remove: ok";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String executeCashTickets(List<String> tokens) {
        // Formato: cash tickets <id>
        if (tokens.size() < 3) {
            return "Usage: cash tickets <id>";
        }

        String cashierId = tokens.get(2);

        try {
            List<Ticket> tickets = ticketService. findTicketsByCashier(cashierId);

            if (tickets.isEmpty()) {
                return "Tickets:  \ncash tickets:  ok";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Tickets: \n");

            for (Ticket ticket : tickets) {
                String idToShow = ticket.getDisplayId();
                sb.append("  ").append(idToShow)
                        .append("->").append(ticket.getState())
                        .append("\n");
            }

            sb.append("cash tickets: ok");
            return sb.toString();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // ==================== COMANDOS CLIENT ====================

    private String executeClient(List<String> tokens) {
        if (tokens.size() < 2) {
            return "Usage:  client add|list|remove [args]";
        }

        String subcommand = tokens.get(1).toLowerCase();

        switch (subcommand) {
            case "add":
                return executeClientAdd(tokens);
            case "list":
                return executeClientList(tokens);
            case "remove":
                return executeClientRemove(tokens);
            default:
                return "Unknown client subcommand: " + subcommand;
        }
    }

    private String executeClientAdd(List<String> tokens) {
        // Formato:  client add "<nombre>" (<DNI>|<NIF>) <email> <cashId>
        // DNI/NIE: 8 dígitos + letra (ej: 55630667S, Y8682724P)
        // NIF: letra + 8 dígitos (ej:  B12345674)

        if (tokens.size() < 6) {
            return "Usage: client add \"<nombre>\" (<DNI>|<NIF>) <email> <cashId>";
        }

        String name = tokens.get(2);
        String identifier = tokens.get(3);
        String email = tokens.get(4);
        String cashId = tokens.get(5);

        try {
            // Detectar si es NIF (letra + 8 dígitos, sin letra al final)
            // Formato NIF: B12345674
            if (identifier.matches("^[A-Z]\\d{8}$")) {
                // Es un NIF (CompanyClient)
                CompanyClient companyClient = userService.createCompanyClient(identifier, name, email, cashId);
                return companyClient.toString() + "\nclient add: ok";
            } else {
                // Es un DNI/NIE (Client normal)
                // Formatos: 12345678A, Y8682724P, X1234567Z
                Client client = userService.createClient(identifier, name, email, cashId);
                return client.toString() + "\nclient add: ok";
            }

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String executeClientList(List<String> tokens) {
        List<Client> clients = userService.findAllClients();

        if (clients.isEmpty()) {
            return "Client:\nclient list: ok";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Client:\n");

        // Ordenar por ID (descendente alfabéticamente como en el output esperado)
        clients.sort((c1, c2) -> c2.getId().compareTo(c1.getId()));

        for (Client client : clients) {
            sb.append("  ").append(client.toString()).append("\n");
        }

        sb.append("client list: ok");
        return sb.toString();
    }

    private String executeClientRemove(List<String> tokens) {
        // Formato: client remove <DNI>
        if (tokens.size() < 3) {
            return "Usage:  client remove <DNI>";
        }

        String dni = tokens.get(2);

        try {
            userService.deleteClient(dni);
            return "client remove: ok";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // ==================== COMANDOS PROD ====================

    private String executeProd(List<String> tokens) {
        if (tokens.size() < 2) {
            return "Usage: prod add|addMeeting|addFood|list|remove|update [args]";
        }

        String subcommand = tokens.get(1).toLowerCase();

        switch (subcommand) {
            case "add":
                return executeProdAdd(tokens);
            case "addmeeting":
                return executeProdAddMeeting(tokens);
            case "addfood":
                return executeProdAddFood(tokens);
            case "list":
                return executeProdList(tokens);
            case "remove":
                return executeProdRemove(tokens);
            case "update":
                return executeProdUpdate(tokens);
            default:
                return "Unknown prod subcommand: " + subcommand;
        }
    }

    private String executeProdAdd(List<String> tokens) {
        try {
            // CASO 1: prod add <fecha> <SERVICIO>
            // Ejemplo: prod add 2025-12-21 INSURANCE
            if (tokens.size() == 4 && tokens.get(2).matches("\\d{4}-\\d{2}-\\d{2}")) {
                return executeProdAddService(tokens);
            }

            // CASO 2: prod add <id> "<name>" <category> <price> <maxPersonalizations>
            // Ejemplo: prod add 5 "Camiseta talla: M UPM" CLOTHES 15 3
            if (tokens.size() == 7) {
                String id = tokens.get(2);
                String name = tokens.get(3);
                String categoryStr = tokens.get(4);
                double price = Double.parseDouble(tokens.get(5));
                int maxPersonalizations = Integer.parseInt(tokens.get(6));

                TCategory category = TCategory.valueOf(categoryStr.toUpperCase());
                ProductPersonalized product = productService.createPersonalizedProduct(id, name, price, category, maxPersonalizations);
                return product.toString() + "\nprod add:  ok";
            }

            // CASO 3: prod add <id> "<name>" <category> <price>
            if (tokens.size() == 6) {
                String id = tokens.get(2);
                String name = tokens.get(3);
                String categoryStr = tokens. get(4);
                double price = Double.parseDouble(tokens. get(5));

                TCategory category = TCategory.valueOf(categoryStr.toUpperCase());
                BasicProduct product = productService.createBasicProduct(id, name, price, category);
                return product. toString() + "\nprod add: ok";
            }

            // CASO 4: prod add "<name>" <category> <price>
            if (tokens.size() == 5) {
                String name = tokens.get(2);
                String categoryStr = tokens. get(3);
                double price = Double.parseDouble(tokens. get(4));

                TCategory category = TCategory.valueOf(categoryStr.toUpperCase());
                String id = "0"; // ID por defecto
                BasicProduct product = productService.createBasicProduct(id, name, price, category);
                return product. toString() + "\nprod add: ok";
            }

            return "Usage: prod add [<id>] \"<name>\" <category> <price> [<maxPersonalizations>]";

        } catch (IllegalArgumentException e) {
            return "Error: Invalid category or format";
        } catch (Exception e) {
            return "Error: " + e. getMessage();
        }
    }

    private String executeProdAddService(List<String> tokens) {
        // Formato: prod add <fecha> <SERVICIO>
        // Ejemplo: prod add 2025-12-21 INSURANCE

        try {
            String dateStr = tokens.get(2);
            String serviceTypeStr = tokens.get(3);

            LocalDate maxUsageDate = LocalDate.parse(dateStr);
            ServiceType serviceType = ServiceType.valueOf(serviceTypeStr.toUpperCase());

            Service service = serviceService.createService(serviceType, maxUsageDate);
            return service.toString() + "\nprod add: ok";

        } catch (IllegalArgumentException e) {
            return "Error: Invalid service type";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String executeProdAddMeeting(List<String> tokens) {
        // Formato: prod addMeeting <id> "<name>" <price> <date> <maxPeople>
        // Ejemplo: prod addMeeting 23456 "Reunion Rotonda" 12 2025-12-21 100

        try {
            if (tokens.size() < 7) {
                return "Usage:  prod addMeeting <id> \"<name>\" <price> <date: yyyy-MM-dd> <maxPeople>";
            }

            String id = tokens.get(2);
            String name = tokens.get(3);
            double price = Double.parseDouble(tokens.get(4));
            String dateStr = tokens.get(5);
            int maxPeople = Integer.parseInt(tokens.get(6));

            // Parsear fecha con hora de mediodía para evitar problemas de validación
            LocalDate eventDateLocal = LocalDate.parse(dateStr);
            LocalDateTime eventDate = eventDateLocal.atTime(12, 0); // 12:00 PM

            MeetingProduct meeting = productService.createMeetingProduct(id, name, price, eventDate, maxPeople);
            return meeting.toString() + "\nprod addMeeting:  ok";

        } catch (DomainException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Error processing ->prod addMeeting ->Error adding product";
        }
    }

    private String executeProdAddFood(List<String> tokens) {
        // Formato: prod addFood <id> "<name>" <price> <date> <maxPeople>
        // Ejemplo: prod addFood 23459 "Restaurante Asador" 50 2025-12-21 40

        try {
            if (tokens.size() < 7) {
                return "Usage:  prod addFood <id> \"<name>\" <price> <date:yyyy-MM-dd> <maxPeople>";
            }

            String id = tokens. get(2);
            String name = tokens.get(3);
            double price = Double.parseDouble(tokens.get(4));
            String dateStr = tokens.get(5);
            int maxPeople = Integer.parseInt(tokens. get(6));

            // Parsear fecha con hora de mediodía
            LocalDate eventDateLocal = LocalDate.parse(dateStr);
            LocalDateTime eventDate = eventDateLocal.atTime(12, 0); // 12:00 PM

            // Fecha de caducidad = fecha del evento
            LocalDate expirationDate = eventDateLocal;

            FoodProduct food = productService.createFoodProduct(id, name, price, eventDate, maxPeople, expirationDate);
            return food.toString() + "\nprod addFood: ok";

        } catch (DomainException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Error processing ->prod addFood ->Error adding product";
        }
    }

    private String executeProdList(List<String> tokens) {
        List<Product> products = productService.findAllProducts();
        List<Service> services = serviceService.findAllServices();

        if (products.isEmpty() && services.isEmpty()) {
            return "Catalog:\nprod list: ok";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Catalog:\n");

        // Ordenar productos por ID
        products.sort((p1, p2) -> {
            try {
                int id1 = Integer.parseInt(p1.getId());
                int id2 = Integer.parseInt(p2.getId());
                return Integer.compare(id1, id2);
            } catch (NumberFormatException e) {
                return p1.getId().compareTo(p2.getId());
            }
        });

        // Añadir productos
        for (Product product : products) {
            sb.append("  ").append(product.toString()).append("\n");
        }

        // Añadir servicios
        for (Service service : services) {
            sb.append("  ").append(service.toString()).append("\n");
        }

        sb.append("prod list: ok");
        return sb.toString();
    }

    private String executeProdRemove(List<String> tokens) {
        if (tokens.size() < 3) {
            return "Usage:  prod remove <id>";
        }

        String id = tokens.get(2);

        try {
            Product product = productService.findProductById(id);
            productService.deleteProduct(id);
            return product.toString() + "\nprod remove: ok";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String executeProdUpdate(List<String> tokens) {
        if (tokens.size() < 5) {
            return "Usage:  prod update <id> NAME|CATEGORY|PRICE <value>";
        }

        String id = tokens.get(2);
        String field = tokens.get(3);
        String value = tokens.get(4);

        try {
            BasicProduct product = productService.updateProduct(id, field, value);
            return product.toString() + "\nprod update: ok\n";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // ==================== COMANDOS TICKET ====================

    private String executeTicket(List<String> tokens) {
        if (tokens.size() < 2) {
            return "Usage: ticket new|add|remove|print|list [args]";
        }

        String subcommand = tokens.get(1).toLowerCase();

        switch (subcommand) {
            case "new":
                return executeTicketNew(tokens);
            case "add":
                return executeTicketAdd(tokens);
            case "remove":
                return executeTicketRemove(tokens);
            case "print":
                return executeTicketPrint(tokens);
            case "list":
                return executeTicketList(tokens);
            default:
                return "Unknown ticket subcommand: " + subcommand;
        }
    }

    private String executeTicketNew(List<String> tokens) {
        // Formato 1: ticket new <cashId> <clientId>
        // Formato 2: ticket new <id> <cashId> <clientId>
        // Formato 3: ticket new <id> <cashId> <clientId> -[c|p|s]

        try {
            String ticketId = null;
            String cashId;
            String clientId;
            TicketMode mode = TicketMode.BASIC; // Default:  -p (solo productos)

            if (tokens.size() >= 6) {
                // Formato 3: con ID y opción
                ticketId = tokens.get(2);
                cashId = tokens. get(3);
                clientId = tokens.get(4);
                String option = tokens.get(5);

                // Parsear opción -c, -p, -s
                if (option. equals("-c")) {
                    mode = TicketMode.DETAILED; // Combinado (productos + servicios)
                } else if (option.equals("-p")) {
                    mode = TicketMode.BASIC; // Solo productos
                } else if (option.equals("-s")) {
                    mode = TicketMode. DETAILED; // Solo servicios (usa DETAILED)
                }
            } else if (tokens.size() >= 5) {
                // Formato 2: con ID
                ticketId = tokens.get(2);
                cashId = tokens.get(3);
                clientId = tokens.get(4);
            } else if (tokens.size() >= 4) {
                // Formato 1: sin ID (autogenerado)
                cashId = tokens.get(2);
                clientId = tokens.get(3);
            } else {
                return "Usage: ticket new [<id>] <cashId> <userId> -[c|p|s] (default -p option)";
            }

            Ticket ticket;

            if (ticketId != null) {
                // Crear con ID específico
                ticket = ticketService.createTicketWithId(ticketId, cashId, clientId, mode);
            } else {
                // Crear con ID autogenerado
                ticket = ticketService.createTicket(cashId, clientId, mode);
            }

            return ticket.formatForDisplay() + "\nticket new:  ok";

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String executeTicketAdd(List<String> tokens) {
        // Formato:  ticket add <ticketId> <cashId> <productId|serviceId> <quantity> [--p<txt> --p<txt>]

        try {
            if (tokens.size() < 5) {
                return "Usage:  ticket add <ticketId> <cashId> <prodId> <amount> [--p<txt> --p<txt>]";
            }

            String ticketId = tokens.get(2);
            String cashId = tokens. get(3);
            String itemId = tokens.get(4);

            // Validar cashier (por ahora solo verificar que existe)
            userService.findCashierById(cashId);

            // Detectar si es servicio (termina en S) o producto
            if (itemId.endsWith("S")) {
                // Es un servicio
                ticketService.addServiceToTicket(ticketId, itemId, 1); // Los servicios son cantidad 1
            } else {
                int quantity = Integer.parseInt(tokens.get(5));

                // Detectar personalizaciones (tokens que empiezan con --p)
                List<String> personalizations = new ArrayList<>();
                for (int i = 6; i < tokens.size(); i++) {
                    if (tokens.get(i).startsWith("--p")) {
                        String personalization = tokens.get(i).substring(3); // Quitar "--p"
                        personalizations.add(personalization);
                    }
                }

                if (! personalizations.isEmpty()) {
                    // Añadir productos personalizados
                    ticketService.addPersonalizedProductToTicket(ticketId, itemId, quantity, personalizations);
                } else {
                    // Añadir producto normal
                    ticketService.addProductToTicket(ticketId, itemId, quantity);
                }
            }

            // Mostrar el ticket completo después de añadir
            Ticket ticket = ticketService.findTicketById(ticketId);
            return ticket.formatForDisplay() + "\nticket add: ok";

        } catch (NumberFormatException e) {
            return "Error: Invalid quantity format";
        } catch (Exception e) {
            return "Error:  " + e.getMessage();
        }
    }

    private String executeTicketRemove(List<String> tokens) {
        // Formato: ticket remove <ticketId> <cashId> <prodId>

        try {
            if (tokens.size() < 5) {
                return "Usage: ticket remove <ticketId> <cashId> <prodId>";
            }

            String ticketId = tokens.get(2);
            String cashId = tokens.get(3);
            String productId = tokens.get(4);

            // Validar cashier
            userService.findCashierById(cashId);

            // Eliminar todas las líneas con ese producto
            ticketService.removeProductFromTicket(ticketId, productId);

            // Mostrar el ticket después de eliminar
            Ticket ticket = ticketService.findTicketById(ticketId);
            return ticket.formatForDisplay() + "\nticket remove: ok";

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String executeTicketPrint(List<String> tokens) {
        // Formato: ticket print <ticketId> <cashId>

        try {
            if (tokens.size() < 4) {
                return "Usage:  ticket print <ticketId> <cashId>";
            }

            String ticketId = tokens.get(2);
            String cashId = tokens.get(3);

            // Validar cashier
            userService.findCashierById(cashId);

            // Cerrar el ticket (esto añade el timestamp al ID)
            Ticket ticket = ticketService.closeTicket(ticketId);

            // Mostrar el ticket (ya ordenado alfabéticamente)
            return ticket. formatForDisplay() + "\nticket print: ok";

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String executeTicketList(List<String> tokens) {
        // Formato: ticket list

        try {
            List<Ticket> tickets = ticketService.findAllTickets();

            if (tickets.isEmpty()) {
                return "Ticket List:\nticket list: ok";
            }

            // Ordenar por cashier ID
            tickets.sort((t1, t2) -> t1.getCashier().getId().compareTo(t2.getCashier().getId()));

            StringBuilder sb = new StringBuilder();
            sb.append("Ticket List:\n");

            for (Ticket ticket : tickets) {
                String idToShow = ticket.getDisplayId();
                sb.append("  ").append(idToShow)
                        .append(" - ").append(ticket.getState())
                        .append("\n");
            }

            sb.append("ticket list: ok");
            return sb.toString();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }



    // ==================== AYUDA ====================

    private String getHelpMessage() {
        return """
            Commands:
              client add "<nombre>" (<DNI>|<NIF>) <email> <cashId>
              client remove <DNI>
              client list
              cash add [<id>] "<nombre>"<email>
              cash remove <id>
              cash list
              cash tickets <id>
              ticket new [<id>] <cashId> <userId> -[c|p|s] (default -p option)
              ticket add <ticketId><cashId> <prodId> <amount> [--p<txt> --p<txt>] 
              ticket remove <ticketId><cashId> <prodId> 
              ticket print <ticketId> <cashId> 
              ticket list
              prod add ([<id>] "<name>" <category> <price> [<maxPers>]) || ("<name>" <category> )
              prod update <id> NAME|CATEGORY|PRICE <value>
              prod addFood [<id>] "<name>" <price> <expiration: yyyy-MM-dd> <max_people>
              prod addMeeting [<id>] "<name>" <price> <expiration:yyyy-MM-dd> <max_people>
              prod list
              prod remove <id>
              help
              echo "<text>" 
              exit
            
            Categories:  MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS
            Discounts if there are ≥2 units in the category:  MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%. 
            """;
    }
}
