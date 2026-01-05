package com.p3.p3POO.domain.model.product;

import com.p3.p3POO.domain.model.enums.TCategory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Event extends Product {

    // Constante definida en el enunciado
    public static final int MAX_PARTICIPANTS_LIMIT = 100;

    private final LocalDateTime eventDate;
    private final int maxParticipants;

    public Event(int id, String name, Double price, LocalDateTime eventDate, int maxParticipants) {
        // Llamamos al constructor de Product que NO pide categoría
        super(id, name, price);
        this.eventDate = eventDate;
        this.maxParticipants = maxParticipants;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    /**
     * Sobrescribimos toString para mostrar la fecha y participantes.
     */
    @Override
    public String toString() {
        // FIX: Formato solo fecha (sin hora) para coincidir con el output esperado
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // FIX: Ajuste de texto "date of Event:" y "max people allowed:"
        return super.toString().replace("}", "") +
                ", date of Event:" + eventDate.format(formatter) +
                ", max people allowed:" + maxParticipants +
                "}";
    }

    @Override
    public void setCategory(TCategory category) {
        throw new UnsupportedOperationException("Error: Events (Food/Meeting) cannot have a category.");
    }
}
