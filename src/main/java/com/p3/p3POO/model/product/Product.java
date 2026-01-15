package com.p3.p3POO.model.product;

import com.p3.p3POO.model.enums.TCategory;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType. JOINED)
public abstract class Product {

    @Id
    protected String id;  // ← String, no Integer

    @Column(nullable = false, length = 100)
    protected String name;

    @Column(nullable = false)
    protected Double basePrice;

    @Enumerated(EnumType.STRING)
    protected TCategory category;  // ← Category, no TCategory

    protected Product() {}

    protected Product(String id, String name, Double basePrice, TCategory category) {
        this.id = id;
        this. name = name;
        this. basePrice = basePrice;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public TCategory getCategory() {
        return category;
    }

    public void setCategory(TCategory category) {
        this.category = category;
    }

    // Métodos abstractos
    public abstract Double calculateFinalPrice();
    public abstract boolean isValidForDate(LocalDate date);

    // Aplicar descuento por categoría
    public Double applyDiscount(Double price) {
        if (category == null) {
            return price;
        }
        return price * (1 - category.getDiscount());
    }
}
