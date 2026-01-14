package com.p3.p3POO.domain.model.product;

import com.p3.p3POO.domain.model.enums.TCategory;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "personalized_products")
public class ProductPersonalized extends Product {

    @Column(nullable = false)
    private Integer maxPersonalizations;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "personalizations", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "personalization")
    private List<String> personalizationList;

    // Constructor vacío (JPA)
    public ProductPersonalized() {
        super();
        this.personalizationList = new ArrayList<>();
    }

    // Constructor completo
    public ProductPersonalized(String id, String name, Double basePrice, TCategory category, Integer maxPersonalizations) {
        super(id, name, basePrice, category);
        this.maxPersonalizations = maxPersonalizations;
        this.personalizationList = new ArrayList<>();
    }

    public void addPersonalization(String text) {
        if (personalizationList.size() >= maxPersonalizations) {
            throw new IllegalStateException("Max personalizations reached:  " + maxPersonalizations);
        }
        this.personalizationList.add(text);
    }

    @Override
    public Double calculateFinalPrice() {
        // Precio base + 10% por cada personalización
        Double surcharge = personalizationList.size() * basePrice * 0.10;
        Double totalPrice = basePrice + surcharge;
        return applyDiscount(totalPrice);
    }

    @Override
    public boolean isValidForDate(LocalDate date) {
        return true; // Siempre válidos
    }

    @Override
    public String toString() {
        if (personalizationList == null || personalizationList.isEmpty()) {
            return String.format(java.util.Locale.US, "{class:ProductPersonalized, id:%s, name:'%s', category:%s, price:%.1f, maxPersonal:%d}", id, name, category, basePrice, maxPersonalizations);
        } else {
            return String.format(java.util.Locale.US, "{class:ProductPersonalized, id:%s, name:'%s', category:%s, price:%.1f, maxPersonal:%d, personalizationList:%s}", id, name, category, basePrice, maxPersonalizations, personalizationList);
        }
    }

    public String toStringWithPersonalizations(List<String> personalizations) {
        if (personalizations == null || personalizations.isEmpty()) {
            return toString();
        }
        // Calcula precio con personalizaciones
        double surcharge = personalizations.size() * basePrice * 0.10;
        double priceWithPersonalizations = basePrice + surcharge;
        return String.format(java.util.Locale.US, "{class: ProductPersonalized, id:%s, name:'%s', category:%s, price:%.1f, maxPersonal:%d, personalizationList:%s}", id, name, category, priceWithPersonalizations, maxPersonalizations, personalizations);
    }
}
