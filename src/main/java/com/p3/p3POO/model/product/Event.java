package com.p3.p3POO.model.product;

import com.p3.p3POO.model.enums.TCategory;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

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

    protected Event() {
        super();
        this.maxParticipants = 100;
        this.actualPeople = 0;
    }

    protected Event(String id, String name, Double basePrice, LocalDateTime eventDate, Integer maxParticipants) {
        super(id, name, basePrice, null);
        this.eventDate = eventDate;
        this.maxParticipants = maxParticipants != null ? maxParticipants :  100;
        this.actualPeople = 0;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Integer getActualPeople() {
        return actualPeople;
    }

    public void setActualPeople(Integer actualPeople) {
        this.actualPeople = actualPeople;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(eventDate, event.eventDate) && Objects.equals(maxParticipants, event.maxParticipants) && Objects.equals(actualPeople, event.actualPeople);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventDate, maxParticipants, actualPeople);
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
