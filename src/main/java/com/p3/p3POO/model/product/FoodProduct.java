package com.p3.p3POO.model.product;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@Table(name = "food_products")
public class FoodProduct extends Event {

    private static final int MIN_PLANNING_DAYS = 3;

    @Column(nullable = false)
    private LocalDate expirationDate;

    public FoodProduct() {
        super();
    }

    public FoodProduct(String id, String name, Double basePrice, LocalDateTime eventDate,
                       Integer maxParticipants, LocalDate expirationDate) {
        super(id, name, basePrice, eventDate, maxParticipants);
        this.expirationDate = expirationDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FoodProduct that = (FoodProduct) o;
        return Objects.equals(expirationDate, that.expirationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expirationDate);
    }

    @Override
    public boolean isValidForDate(LocalDate currentDate) {
        LocalDate eventDay = eventDate.toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(currentDate, eventDay);

        boolean notExpired = currentDate. isBefore(expirationDate) || currentDate.isEqual(expirationDate);

        return daysBetween >= MIN_PLANNING_DAYS && notExpired;
    }

    public static boolean canBeCreated(LocalDateTime eventDate, LocalDate expirationDate) {
        LocalDate now = LocalDate.now();
        LocalDate eventDay = eventDate.toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(now, eventDay);

        boolean notExpired = now.isBefore(expirationDate) || now.isEqual(expirationDate);

        return daysBetween >= MIN_PLANNING_DAYS && notExpired;
    }

    @Override
    public String toString() {
        return String.format(java. util.Locale.US, "{class:Food, id:%s, name:'%s', price:%.1f, date of Event:%s, max people allowed:%d}", id, name, basePrice, eventDate.toLocalDate(), maxParticipants);
    }
}
