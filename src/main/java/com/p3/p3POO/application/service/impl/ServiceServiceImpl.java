package com.p3.p3POO.application.service.impl;

import com.p3.p3POO.application.service.ServiceService;
import com.p3.p3POO.domain.model.enums.ServiceType;
import com.p3.p3POO.domain.model.interfaces.IdGenerable;
import com.p3.p3POO.domain.model.service.Service;
import com.p3.p3POO.domain.repository.ServiceRepository;

import java.util.List;
import java.util.Objects;

public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final IdGenerable idGenerator;

    public ServiceServiceImpl(ServiceRepository serviceRepository, IdGenerable idGenerator) {
        this.serviceRepository = Objects.requireNonNull(serviceRepository, "serviceRepository");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator");
    }

    @Override
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public Service getServiceById(String id) {
        return serviceRepository.findById(id);
    }

    @Override
    public Service createService(Service service) {
        Objects.requireNonNull(service, "service");

        String id = idGenerator.generateId();

        Service toSave = new Service(service.getServiceType());
        toSave.setCalculatedPrice(service.getCalculatedPrice());

        return serviceRepository.save(toSave);
    }

    @Override
    public Service updateService(String id, Service service) {
        Objects.requireNonNull(service, "service");
        getServiceById(id); // asegura existencia

        Service toSave = new Service(service.getServiceType());
        toSave.setCalculatedPrice(service.getCalculatedPrice());

        return serviceRepository.save(toSave);
    }

    @Override
    public void deleteService(String id) {
        serviceRepository.deleteById(id);
    }

    public List<Service> getServicesByType(ServiceType serviceType) {
        return serviceRepository.findByServiceType(serviceType);
    }
}