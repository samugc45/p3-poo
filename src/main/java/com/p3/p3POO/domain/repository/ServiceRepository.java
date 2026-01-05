package com.p3.p3POO.domain.repository;

import com.p3.p3POO.domain.model.enums.ServiceType;
import com.p3.p3POO.domain.model.service.Service;

import java.util.List;

public interface ServiceRepository {
    List<Service> findAll();

    Service findById(String id);

    Service save(Service toSave);

    void deleteById(String id);

    List<Service> findByServiceType(ServiceType serviceType);
}
