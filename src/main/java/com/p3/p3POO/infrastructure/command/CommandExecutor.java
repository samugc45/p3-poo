package com.p3.p3POO. infrastructure.command;

import com. p3.p3POO. application.factory. TicketFactory;
import com.p3.p3POO.application.service.*;
import com.p3.p3POO.application.strategy.BasicTicketPrintStrategy;
import com.p3.p3POO.application. strategy.DetailedTicketPrintStrategy;
import com.p3.p3POO.application.validator.EventValidator;
import com.p3.p3POO.domain.model.Ticket;
import com.p3.p3POO.domain.model.enums.TCategory;
import com.p3.p3POO.domain.model.product.BasicProduct;
import com.p3.p3POO.domain.model.product.Product;
import com.p3.p3POO.domain.model.user.Cashier;
import com.p3.p3POO.domain.model.user.Client;
import com.p3.p3POO.domain.model.user.CompanyClient;
import com.p3.p3POO.infrastructure.exception.DomainException;
import org.springframework.stereotype.Component;

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
            List<Ticket> tickets = ticketService.findTicketsByCashier(cashierId);

            if (tickets.isEmpty()) {
                return "Tickets:  \ncash tickets: ok";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Tickets:  \n");

            for (Ticket ticket : tickets) {
                sb.append("  ").append(ticket.getId())
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
            return "Usage:  prod add|list|remove|update [args]";
        }

        String subcommand = tokens.get(1).toLowerCase();

        switch (subcommand) {
            case "add":
                return executeProdAdd(tokens);
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
        // Formato 1: prod add <id> "<name>" <category> <price>
        // Formato 2: prod add "<name>" <category> <price>  (ID autogenerado)
        // Formato 3: prod add "<name>" <category>  (sin precio, para casos especiales)

        try {
            String id = null;
            String name;
            String categoryStr;
            Double price = null;

            if (tokens.size() >= 6) {
                // Formato 1: con ID
                if (CommandParser.isNumeric(tokens.get(2))) {
                    id = tokens.get(2);
                    name = tokens.get(3);
                    categoryStr = tokens.get(4);
                    if (tokens.size() >= 6) {
                        price = Double.parseDouble(tokens.get(5));
                    }
                } else {
                    // Formato 2: sin ID
                    name = tokens.get(2);
                    categoryStr = tokens.get(3);
                    price = Double.parseDouble(tokens.get(4));
                }
            } else if (tokens.size() == 5) {
                // Podría ser:  prod add <id> "<name>" <category> (sin precio)
                // O: prod add "<name>" <category> <price>
                if (CommandParser.isNumeric(tokens.get(2))) {
                    // Con ID, sin precio
                    id = tokens.get(2);
                    name = tokens.get(3);
                    categoryStr = tokens.get(4);
                } else {
                    // Sin ID, con precio
                    name = tokens.get(2);
                    categoryStr = tokens.get(3);
                    price = Double.parseDouble(tokens.get(4));
                }
            } else if (tokens.size() == 4) {
                // prod add "<name>" <category>
                name = tokens.get(2);
                categoryStr = tokens.get(3);
            } else {
                return "Usage: prod add [<id>] \"<name>\" <category> [<price>]";
            }

            // Convertir categoría
            TCategory category;
            try {
                category = TCategory.valueOf(categoryStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return "Error: Invalid category: " + categoryStr;
            }

            // Crear producto
            BasicProduct product;

            if (id != null) {
                // Con ID específico
                if (price == null) {
                    price = 0.0; // Precio por defecto
                }
                product = new BasicProduct(id, name, price, category);
                product = productService.createProduct(product);
            } else {
                // ID autogenerado
                if (price == null) {
                    price = 0.0;
                }
                product = productService.createBasicProduct(name, category, price);
            }

            return product.toString() + "\nprod add: ok";

        } catch (NumberFormatException e) {
            return "Error: Invalid price format";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String executeProdList(List<String> tokens) {
        List<Product> products = productService.findAllProducts();

        if (products.isEmpty()) {
            return "Catalog:\nprod list: ok";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Catalog:\n");

        // Ordenar por ID
        products.sort((p1, p2) -> {
            // Intentar ordenar numéricamente si son números
            try {
                int id1 = Integer.parseInt(p1.getId());
                int id2 = Integer.parseInt(p2.getId());
                return Integer.compare(id1, id2);
            } catch (NumberFormatException e) {
                // Si no son números, ordenar alfabéticamente
                return p1.getId().compareTo(p2.getId());
            }
        });

        for (Product product :  products) {
            sb.append("  ").append(product.toString()).append("\n");
        }

        sb.append("prod list: ok");
        return sb.toString();
    }

    private String executeProdRemove(List<String> tokens) {
        // Formato: prod remove <id>
        if (tokens.size() < 3) {
            return "Usage:  prod remove <id>";
        }

        String id = tokens.get(2);

        try {
            // Obtener producto antes de eliminar para mostrar info
            Product product = productService.findProductById(id);
            productService.deleteProduct(id);
            return product.toString() + "\nprod remove: ok";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String executeProdUpdate(List<String> tokens) {
        // Formato: prod update <id> NAME|CATEGORY|PRICE <value>
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
