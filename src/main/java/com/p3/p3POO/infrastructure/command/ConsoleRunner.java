package com.p3.p3POO.infrastructure.command;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleRunner implements CommandLineRunner {

    private final CommandExecutor commandExecutor;

    public ConsoleRunner(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the ticket module App.");
        System.out.println("Ticket module.  Type 'help' to see commands.");

        while (true) {
            System.out. print("tUPM> ");
            String input = scanner.nextLine();

            if (input. trim().isEmpty()) {
                continue;
            }

            String result = commandExecutor.execute(input);

            // Si el resultado es "exit", salir
            if ("exit".equals(result)) {
                System.out.println("Closing application.");
                System.out.println("Goodbye!");
                break;
            }

            // Mostrar resultado (si no está vacío)
            if (!result.isEmpty()) {
                System.out.println(result);
            }

            System.out.println(); // Línea en blanco después de cada comando
        }

        scanner.close();
    }
}