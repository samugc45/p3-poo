package com.p3.p3POO.service;

import com.p3.p3POO.model.service.ServiceProduct;
import com.p3.p3POO.model.enums.ServiceType;

import java.time.LocalDate;
import java.util.List;

public interface ServiceServiceProduct {

    ServiceProduct createService(ServiceType serviceType, LocalDate maxUsageDate);
    ServiceProduct findServiceById(String id);
    List<ServiceProduct> findAllServices();
    List<ServiceProduct> findServicesByType(ServiceType type);
    boolean serviceExists(String id);
}
