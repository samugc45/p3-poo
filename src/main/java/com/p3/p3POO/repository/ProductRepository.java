package com.p3.p3POO.repository;

import com.p3.p3POO.model.product.Product;
import com.p3.p3POO.model.enums.TCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByCategory(TCategory category);

    List<Product> findByNameContainingIgnoreCase(String name);

    Optional<Product> findByName(String name);

    boolean existsById(String id);

    List<Product> findAllByOrderByNameAsc();
}
