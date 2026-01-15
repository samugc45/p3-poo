package com.p3.p3POO.model.product;

import com.p3.p3POO.model.enums.TCategory;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "basic_products")
public class BasicProduct extends Product {

    public BasicProduct() {
        super();
    }

    public BasicProduct(String id, String name, Double basePrice, TCategory category) {
        super(id, name, basePrice, category);
    }

    @Override
    public Double calculateFinalPrice() {
        return applyDiscount(basePrice);
    }

    @Override
    public boolean isValidForDate(LocalDate date) {
        return true;  // Siempre válidos
    }

    @Override
    public String toString() {
        // Forzar formato inglés (punto decimal) usando Locale.US
        return String.format(java.util.Locale.US, "{class: Product, id:%s, name:'%s', category:%s, price: %.1f}", id, name, category, basePrice);
    }
}
