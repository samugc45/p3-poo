package com.p3.p3POO.application.service;

import com.p3.p3POO.domain.model.product.*;
import com.p3.p3POO.domain.model.enums.TCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductService {

    BasicProduct createBasicProduct(String id, String name, Double price,TCategory category);
    MeetingProduct createMeetingProduct(String id, String name, Double price, LocalDateTime eventDate, Integer maxParticipants);
    FoodProduct createFoodProduct(String id, String name, Double price, LocalDateTime eventDate, Integer maxParticipants, LocalDate expirationDate);
    void deleteProduct(String id);
    BasicProduct updateProduct(String id, String field, String value);
    BasicProduct createProduct(BasicProduct product);
    BasicProduct createBasicProduct(String name, TCategory category, double price);

    // General product operations
    Product findProductById(String id);
    List<Product> findAllProducts();
    List<Product> findProductsByCategory(TCategory category);
    boolean productExists(String id);

    // Customizable products
    CustomizableProduct createCustomizableProduct(String baseProductId, Integer maxCustomTexts);
    void addCustomTextToProduct(Long customizableProductId, String text);

    // Personalized products
    ProductPersonalized createPersonalizedProduct(String id, String name, Double price, TCategory category, Integer maxPersonalizations);
}
