package com.p3.p3POO.application.service.impl;

import com. p3.p3POO.application.service.ProductService;
import com.p3.p3POO.domain.model.enums.TCategory;
import com.p3.p3POO.domain.model.product.*;
import com.p3.p3POO.domain.repository.ProductRepository;
import com.p3.p3POO.infrastructure.exception.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util. List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public BasicProduct createBasicProduct(String id, String name, Double price, TCategory category) {
        if (productRepository.existsById(id)) {
            throw new DomainException("Product already exists: " + id);
        }

        BasicProduct product = new BasicProduct(id, name, price, category);
        return productRepository.save(product);
    }

    @Override
    public MeetingProduct createMeetingProduct(String id, String name, Double price, LocalDateTime eventDate, Integer maxParticipants) {
        if (productRepository.existsById(id)) {
            throw new DomainException("Product already exists:  " + id);
        }

        if (! MeetingProduct.canBeCreated(eventDate)) {
            throw new DomainException("Meeting must be created at least 12 hours in advance");
        }

        MeetingProduct product = new MeetingProduct(id, name, price, eventDate, maxParticipants);
        return productRepository.save(product);
    }

    @Override
    public FoodProduct createFoodProduct(String id, String name, Double price, LocalDateTime eventDate, Integer maxParticipants, LocalDate expirationDate) {
        if (productRepository.existsById(id)) {
            throw new DomainException("Product already exists: " + id);
        }

        if (!FoodProduct.canBeCreated(eventDate, expirationDate)) {
            throw new DomainException("Food event must be created at least 3 days in advance and not be expired");
        }

        FoodProduct product = new FoodProduct(id, name, price, eventDate, maxParticipants, expirationDate);
        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Product findProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new DomainException("Product not found:  " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findProductsByCategory(TCategory category) {
        return productRepository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean productExists(String id) {
        return productRepository.existsById(id);
    }

    @Override
    public CustomizableProduct createCustomizableProduct(String baseProductId, Integer maxCustomTexts) {
        Product product = findProductById(baseProductId);

        if (!(product instanceof BasicProduct)) {
            throw new DomainException("Only BasicProducts can be customized");
        }

        CustomizableProduct customizable = new CustomizableProduct((BasicProduct) product, maxCustomTexts);
        // Note: CustomizableProduct needs its own repository or be saved differently
        throw new UnsupportedOperationException("CustomizableProduct persistence not implemented yet");
    }

    @Override
    public void addCustomTextToProduct(Long customizableProductId, String text) {
        throw new UnsupportedOperationException("CustomizableProduct not implemented yet");
    }
}
