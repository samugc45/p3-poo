package com.p3.p3POO.model.product;

import com.p3.p3POO.model.enums.TCategory;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "personalized_products")
public class ProductPersonalized extends Product {

    @Column(nullable = false)
    private Integer maxPersonalizations;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "personalizations", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "personalization")
    private List<String> personalizationList;

    public ProductPersonalized() {
        super();
        this.personalizationList = new ArrayList<>();
    }

    public ProductPersonalized(String id, String name, Double basePrice, TCategory category, Integer maxPersonalizations) {
        super(id, name, basePrice, category);
        this.maxPersonalizations = maxPersonalizations;
        this.personalizationList = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductPersonalized that = (ProductPersonalized) o;
        return Objects.equals(maxPersonalizations, that.maxPersonalizations) && Objects.equals(personalizationList, that.personalizationList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxPersonalizations, personalizationList);
    }

    public Integer getMaxPersonalizations() {
        return maxPersonalizations;
    }

    public void setMaxPersonalizations(Integer maxPersonalizations) {
        this.maxPersonalizations = maxPersonalizations;
    }

    public List<String> getPersonalizationList() {
        return personalizationList;
    }

    public void setPersonalizationList(List<String> personalizationList) {
        this.personalizationList = personalizationList;
    }

    public void addPersonalization(String text) {
        if (personalizationList.size() >= maxPersonalizations) {
            throw new IllegalStateException("Max personalizations reached:  " + maxPersonalizations);
        }
        this.personalizationList.add(text);
    }

    @Override
    public Double calculateFinalPrice() {
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
        double surcharge = personalizations.size() * basePrice * 0.10;
        double priceWithPersonalizations = basePrice + surcharge;
        return String.format(java.util.Locale.US, "{class: ProductPersonalized, id:%s, name:'%s', category:%s, price:%.1f, maxPersonal:%d, personalizationList:%s}", id, name, category, priceWithPersonalizations, maxPersonalizations, personalizations);
    }
}
