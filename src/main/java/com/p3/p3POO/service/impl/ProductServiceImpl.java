package com.p3.p3POO.service.impl;

import com.p3.p3POO.model.product.*;
import com.p3.p3POO.service.ProductService;
import com.p3.p3POO.model.enums.TCategory;
import com.p3.p3POO.repository.ProductRepository;
import com.p3.p3POO.exception.DomainException;
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
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new DomainException("Product not found:  " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public BasicProduct updateProduct(String id, String field, String value) {
        Product product = productRepository.findById(id).orElseThrow(() -> new DomainException("Product not found:  " + id));

        if (!(product instanceof BasicProduct)) {
            throw new DomainException("Only basic products can be updated");
        }

        BasicProduct basicProduct = (BasicProduct) product;

        switch (field. toUpperCase()) {
            case "NAME":
                basicProduct.setName(value);
                break;
            case "CATEGORY":
                try {
                    TCategory category = TCategory.valueOf(value.toUpperCase());
                    basicProduct.setCategory(category);
                } catch (IllegalArgumentException e) {
                    throw new DomainException("Invalid category: " + value);
                }
                break;
            case "PRICE":
                try {
                    double price = Double.parseDouble(value);
                    if (price <= 0) {
                        throw new DomainException("Price must be positive");
                    }
                    basicProduct. setBasePrice(price);
                } catch (NumberFormatException e) {
                    throw new DomainException("Invalid price format: " + value);
                }
                break;
            default:
                throw new DomainException("Invalid field: " + field + ". Valid fields: NAME, CATEGORY, PRICE");
        }

        return productRepository.save(basicProduct);
    }

    @Override
    public BasicProduct createProduct(BasicProduct product) {
        if (productRepository.existsById(product.getId())) {
            throw new DomainException("Product already exists: " + product.getId());
        }
        return productRepository.save(product);
    }

    @Override
    public BasicProduct createBasicProduct(String name, TCategory category, double price) {
        String id = generateNextProductId();
        BasicProduct product = new BasicProduct(id, name, price, category);
        return productRepository.save(product);
    }

    private String generateNextProductId() {
        List<Product> products = productRepository.findAll();

        int id = 0;
        while (true) {
            String idStr = String.valueOf(id);
            boolean exists = products.stream().anyMatch(p -> p.getId().equals(idStr));

            if (!exists) {
                return idStr;
            }
            id++;
        }
    }

    @Override
    public MeetingProduct createMeetingProduct(String id, String name, Double price, LocalDateTime eventDate, Integer maxParticipants) {
        if (productRepository.existsById(id)) {
            throw new DomainException("Product already exists:  " + id);
        }

        if (!MeetingProduct.canBeCreated(eventDate)) {
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
        return productRepository.findById(id).orElseThrow(() -> new DomainException("Product not found:  " + id));
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
        throw new UnsupportedOperationException("CustomizableProduct persistence not implemented yet");
    }

    @Override
    public ProductPersonalized createPersonalizedProduct(String id, String name, Double price, TCategory category, Integer maxPersonalizations) {
        if (productRepository.existsById(id)) {
            throw new DomainException("Product already exists:  " + id);
        }

        ProductPersonalized product = new ProductPersonalized(id, name, price, category, maxPersonalizations);
        return productRepository.save(product);
    }

    @Override
    public void addCustomTextToProduct(Long customizableProductId, String text) {
        throw new UnsupportedOperationException("CustomizableProduct not implemented yet");
    }

}
