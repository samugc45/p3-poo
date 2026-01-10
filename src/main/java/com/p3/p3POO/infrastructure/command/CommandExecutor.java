package com.p3.p3POO. infrastructure.command;

import com. p3.p3POO. application.factory. TicketFactory;
import com.p3.p3POO.application.service.*;
import com.p3.p3POO.application.strategy.BasicTicketPrintStrategy;
import com.p3.p3POO.application. strategy.DetailedTicketPrintStrategy;
import com.p3.p3POO.application.validator.EventValidator;
import com.p3.p3POO.domain.model.Ticket;
import com.p3.p3POO.domain.model.user.Cashier;
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
