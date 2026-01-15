package com.p3.p3POO.repository;

import com.p3.p3POO.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Buscar usuario por email
    Optional<User> findByEmail(String email);

    // Verificar si existe un usuario con ese email
    boolean existsByEmail(String email);

    // Buscar usuario por nombre (ignorando mayúsculas)
    Optional<User> findByNameIgnoreCase(String name);
}
