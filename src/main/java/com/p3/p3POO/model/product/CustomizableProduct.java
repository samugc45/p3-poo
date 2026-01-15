package com.p3.p3POO.model.product;

import com.p3.p3POO.model.enums.TCategory;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customizable_products")
public class CustomizableProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_product_id", nullable = false)
    private BasicProduct baseProduct;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "customization_texts", joinColumns = @JoinColumn(name = "customizable_product_id"))
    @Column(name = "text")
    private List<String> customTexts;

    @Column(nullable = false)
    private Integer maxCustomTexts;

    public CustomizableProduct() {
        this.customTexts = new ArrayList<>();
    }

    public CustomizableProduct(BasicProduct baseProduct, Integer maxCustomTexts) {
        this.baseProduct = baseProduct;
        this.maxCustomTexts = maxCustomTexts;
        this.customTexts = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BasicProduct getBaseProduct() {
        return baseProduct;
    }

    public void setBaseProduct(BasicProduct baseProduct) {
        this.baseProduct = baseProduct;
    }

    public List<String> getCustomTexts() {
        return customTexts;
    }

    public void setCustomTexts(List<String> customTexts) {
        this.customTexts = customTexts;
    }

    public Integer getMaxCustomTexts() {
        return maxCustomTexts;
    }

    public void setMaxCustomTexts(Integer maxCustomTexts) {
        this.maxCustomTexts = maxCustomTexts;
    }

    public void addCustomText(String text) {
        if (customTexts.size() >= maxCustomTexts) {
            throw new IllegalStateException("Max customizations reached:  " + maxCustomTexts);
        }
        this.customTexts.add(text);
    }

    public Double calculateFinalPrice() {
        Double basePrice = baseProduct.getBasePrice();
        Double surcharge = customTexts.size() * basePrice * 0.10;
        Double totalPrice = basePrice + surcharge;
        return baseProduct.applyDiscount(totalPrice);
    }

    public String getProductId() {
        return baseProduct.getId();
    }

    public String getName() {
        return baseProduct.getName();
    }

    public TCategory getCategory() {
        return baseProduct.getCategory();
    }

    public boolean isValidForDate(LocalDate date) {
        return baseProduct.isValidForDate(date);
    }
}
