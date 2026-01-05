package com.p3.p3POO.infrastructure.command;

import org.springframework.boot. CommandLineRunner;
import org. springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Sistema POS iniciado ===");
        System.out.println("Escribe 'help' para ver comandos o 'exit' para salir.\n");

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("exit")) {
                System.out.println("Cerrando sistema.. .");
                break;
            }

            if (line.equalsIgnoreCase("help")) {
                System.out.println("Comandos disponibles:  help, echo \"<texto>\", exit");
                continue;
            }

            if (line.startsWith("echo ")) {
                String text = line.substring(5).replaceAll("^\"|\"$", "");
                System.out.println(text);
                continue;
            }

            System.out.println("Comando no reconocido: " + line);
        }

        scanner.close();
    }
}
