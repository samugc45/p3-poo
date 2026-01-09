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

        System.out.println("=". repeat(60));
        System.out.println("        TICKET MANAGEMENT SYSTEM - p3POO");
        System.out.println("=".repeat(60));
        System.out. println("Type 'help' for available commands or 'exit' to quit");
        System.out.println();

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("exit") || input.trim().equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                break;
            }

            String result = commandExecutor.execute(input);
            System.out.println(result);
            System.out.println();
        }

        scanner.close();
    }
}