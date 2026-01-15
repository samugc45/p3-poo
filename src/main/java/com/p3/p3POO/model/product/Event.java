package com.p3.p3POO.model.product;

import com.p3.p3POO.model.enums.TCategory;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "events")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Event extends Product {

    @Column(nullable = false)
    protected LocalDateTime eventDate;

    @Column(nullable = false)
    protected Integer maxParticipants;

    @Column(nullable = false)
    protected Integer actualPeople;

    // Constructor sin argumentos (JPA)
    protected Event() {
        super();
        this.maxParticipants = 100;
        this.actualPeople = 0;
    }

    // Constructor con parámetros
    protected Event(String id, String name, Double basePrice, LocalDateTime eventDate, Integer maxParticipants) {
        super(id, name, basePrice, null);  // Events NO tienen categoría
        this.eventDate = eventDate;
        this.maxParticipants = maxParticipants != null ? maxParticipants :  100;
        this.actualPeople = 0;
    }

    @Override
    public Double calculateFinalPrice() {
        return basePrice * actualPeople;
    }

    @Override
    public void setCategory(TCategory category) {
        throw new UnsupportedOperationException("Events cannot have a category");
    }
}
