package com.p3.p3POO.application.service;

import com.p3.p3POO.domain.model.service.Service;
import com.p3.p3POO.domain.model.enums.ServiceType;

import java.time.LocalDate;
import java.util.List;

public interface ServiceService {

    Service createService(ServiceType serviceType, LocalDate maxUsageDate);
    Service findServiceById(String id);
    List<Service> findAllServices();
    List<Service> findServicesByType(ServiceType type);
    boolean serviceExists(String id);
}
