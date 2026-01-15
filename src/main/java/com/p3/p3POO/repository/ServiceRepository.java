package com.p3.p3POO.repository;

import com.p3.p3POO.model.service.ServiceProduct;
import com.p3.p3POO.model.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceProduct, String> {

    List<ServiceProduct> findByServiceType(ServiceType serviceType);

    List<ServiceProduct> findByMaxUsageDateGreaterThanEqual(LocalDate date);

    @Query("SELECT COUNT(s) FROM ServiceProduct s")
    long countAll();

    List<ServiceProduct> findAllByOrderByIdAsc();
}


