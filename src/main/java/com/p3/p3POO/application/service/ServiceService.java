package com.p3.p3POO.application.service;

import com.p3.p3POO.domain.model.service.Service;

import java.util.List;

public interface ServiceService {
    List<Service> getAllServices();

    Service getServiceById(String id);

    Service createService(Service service);

    Service updateService(String id, Service service);

    void deleteService(String id);
}
