package com.p3.p3POO;

import com.p3.p3POO.command.CommandExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Scanner;


@SpringBootApplication
public class P3PooApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(P3PooApplication.class, args);

        CommandExecutor commandExecutor = context.getBean(CommandExecutor.class);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the ticket module App.");
        System.out.println("Ticket module.  Type 'help' to see commands.");

        while (true) {
            System.out.print("tUPM> ");
            String input = scanner.nextLine();

            if (input.trim().isEmpty()) {
                continue;
            }

            String result = commandExecutor.execute(input);

            if ("exit".equals(result)) {
                System.out.println("Closing application.");
                System.out.println("Goodbye!");
                break;
            }

            if (!result.isEmpty()) {
                System.out.println(result);
            }
            System.out.println();
        }
        scanner.close();
    }
}
