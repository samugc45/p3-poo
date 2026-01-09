package com.p3.p3POO.domain.model.product;

import jakarta.persistence.*;
import lombok.Data;
import lombok. EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "food_products")
public class FoodProduct extends Event {

    private static final int MIN_PLANNING_DAYS = 3;

    @Column(nullable = false)
    private LocalDate expirationDate;

    // Constructor sin argumentos (JPA)
    public FoodProduct() {
        super();
    }

    // Constructor completo
    public FoodProduct(String id, String name, Double basePrice, LocalDateTime eventDate,
                       Integer maxParticipants, LocalDate expirationDate) {
        super(id, name, basePrice, eventDate, maxParticipants);
        this.expirationDate = expirationDate;
    }

    // ✅ IMPLEMENTACIÓN OBLIGATORIA del método abstracto isValidForDate() heredado de Product
    @Override
    public boolean isValidForDate(LocalDate currentDate) {
        LocalDate eventDay = eventDate.toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(currentDate, eventDay);

        // Verificar que no haya caducado
        boolean notExpired = currentDate. isBefore(expirationDate) || currentDate.isEqual(expirationDate);

        return daysBetween >= MIN_PLANNING_DAYS && notExpired;
    }

    // Método estático auxiliar para validar al crear
    public static boolean canBeCreated(LocalDateTime eventDate, LocalDate expirationDate) {
        LocalDate now = LocalDate.now();
        LocalDate eventDay = eventDate.toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(now, eventDay);

        boolean notExpired = now. isBefore(expirationDate) || now.isEqual(expirationDate);

        return daysBetween >= MIN_PLANNING_DAYS && notExpired;
    }
}
