package com.p3.p3POO.command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    public static List<String> parse(String commandLine) {
        List<String> tokens = new ArrayList<>();

        // Regex para capturar:
        // - Palabras sin comillas
        // - Frases entre comillas dobles
        // - Flags (--pred, -s, etc.)
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|--p(\\w+)|-(\\w+)|(\\S+)");
        Matcher matcher = pattern.matcher(commandLine);

        while (matcher. find()) {
            if (matcher.group(1) != null) {
                // Texto entre comillas
                tokens.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                // Flag de personalización (--pred → "red")
                tokens.add("--p" + matcher.group(2));
            } else if (matcher.group(3) != null) {
                // Flag corto (-s, -c, -p)
                tokens.add("-" + matcher.group(3));
            } else if (matcher.group(4) != null) {
                // Palabra normal
                tokens.add(matcher.group(4));
            }
        }
        return tokens;
    }

    /**
     * Extrae flags de personalización de una lista de tokens
     * Ejemplo:  ["--pred", "--pblue"] → ["red", "blue"]
     */
    public static List<String> extractPersonalizationFlags(List<String> tokens) {
        List<String> personalizations = new ArrayList<>();

        for (String token : tokens) {
            if (token.startsWith("--p")) {
                personalizations.add(token.substring(3)); // Quitar "--p"
            }
        }

        return personalizations;
    }

    /**
     * Extrae el flag de modo de ticket (-c, -p, -s)
     * Por defecto retorna "-p" si no hay flag
     */
    public static String extractTicketModeFlag(List<String> tokens) {
        for (String token : tokens) {
            if (token.equals("-c") || token.equals("-p") || token.equals("-s")) {
                return token;
            }
        }
        return "-p"; // Default
    }

    /**
     * Verifica si un token es un número
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}