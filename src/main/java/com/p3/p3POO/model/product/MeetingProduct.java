package com.p3.p3POO.model.product;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "meeting_products")
public class MeetingProduct extends Event {

    private static final int MIN_PLANNING_HOURS = 12;

    public MeetingProduct() {
        super();
    }

    public MeetingProduct(String id, String name, Double basePrice, LocalDateTime eventDate, Integer maxParticipants) {
        super(id, name, basePrice, eventDate, maxParticipants);
    }



    @Override
    public boolean isValidForDate(LocalDate currentDate) {
        LocalDateTime now = currentDate.atStartOfDay();
        long hoursBetween = ChronoUnit.HOURS.between(now, eventDate);
        return hoursBetween >= MIN_PLANNING_HOURS;
    }

    public static boolean canBeCreated(LocalDateTime eventDate) {
        LocalDateTime now = LocalDateTime.now();
        long hoursBetween = ChronoUnit.HOURS.between(now, eventDate);
        return hoursBetween >= MIN_PLANNING_HOURS;
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "{class: Meeting, id:%s, name:'%s', price: %.1f, date of Event:%s, max people allowed:%d}", id, name, basePrice, eventDate. toLocalDate(), maxParticipants);
    }
}