package com.p3.p3POO.repository;

import com.p3.p3POO.model.service.Service;
import com.p3.p3POO.model.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {

    // Buscar servicios por tipo
    List<Service> findByServiceType(ServiceType serviceType);

    // Buscar servicios válidos para una fecha (maxUsageDate >= fecha)
    List<Service> findByMaxUsageDateGreaterThanEqual(LocalDate date);

    // Contar total de servicios (para generar IDs secuenciales:  1S, 2S, 3S...)
    @Query("SELECT COUNT(s) FROM Service s")
    long countAll();

    // Obtener todos ordenados por ID
    List<Service> findAllByOrderByIdAsc();
}


