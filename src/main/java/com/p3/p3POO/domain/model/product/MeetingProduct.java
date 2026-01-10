package com.p3.p3POO.domain.model.product;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "meeting_products")
public class MeetingProduct extends Event {

    private static final int MIN_PLANNING_HOURS = 12;

    // Constructor sin argumentos (JPA)
    public MeetingProduct() {
        super();
    }

    // Constructor completo
    public MeetingProduct(String id, String name, Double basePrice, LocalDateTime eventDate, Integer maxParticipants) {
        super(id, name, basePrice, eventDate, maxParticipants);
    }

    @Override
    public boolean isValidForDate(LocalDate currentDate) {
        LocalDateTime now = currentDate.atStartOfDay();
        long hoursBetween = ChronoUnit.HOURS.between(now, eventDate);
        return hoursBetween >= MIN_PLANNING_HOURS;
    }

    // Método estático auxiliar para validar al crear
    public static boolean canBeCreated(LocalDateTime eventDate) {
        LocalDateTime now = LocalDateTime.now();
        long hoursBetween = ChronoUnit.HOURS.between(now, eventDate);
        return hoursBetween >= MIN_PLANNING_HOURS;
    }
}