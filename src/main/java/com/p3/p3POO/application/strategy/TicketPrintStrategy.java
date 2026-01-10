package com.p3.p3POO.application.strategy;

import com.p3.p3POO.domain.model.Ticket;

/**
 * Patrón Strategy para la impresión de tickets
 * Permite diferentes formatos según el tipo de ticket
 */
public interface TicketPrintStrategy {

    /**
     * Imprime un ticket según la estrategia implementada
     * @param ticket El ticket a imprimir
     * @return String con el formato del ticket
     */
    String print(Ticket ticket);
}
