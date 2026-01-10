package com.p3.p3POO.domain.repository;

import com.p3.p3POO.domain.model.product.Product;
import com.p3.p3POO.domain.model.enums.TCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    // Buscar productos por categoría
    List<Product> findByCategory(TCategory category);

    // Buscar productos por nombre (contiene, ignorando mayúsculas)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Buscar producto por nombre exacto
    Optional<Product> findByName(String name);

    // Verificar si existe un producto con ese ID
    boolean existsById(String id);

    // Obtener todos los productos ordenados por nombre
    List<Product> findAllByOrderByNameAsc();
}
