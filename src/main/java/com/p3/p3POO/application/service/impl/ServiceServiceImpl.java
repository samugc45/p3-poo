package com.p3.p3POO.application. service.impl;

import com. p3.p3POO. application.service.ServiceService;
import com.p3.p3POO.domain.model.enums.ServiceType;
import com.p3.p3POO.domain.repository.ServiceRepository;
import com. p3.p3POO. infrastructure.exception.DomainException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java. util.List;

@org.springframework.stereotype.Service
@Transactional
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public com.p3.p3POO.domain.model.service.Service createService(ServiceType serviceType, LocalDate maxUsageDate) {
        long count = serviceRepository.countAll();
        String id = com.p3.p3POO.domain.model.service.Service.generateId((int) count + 1);

        com.p3.p3POO.domain.model.service.Service service =
                new com.p3.p3POO.domain.model.service.Service(id, serviceType, maxUsageDate);

        return serviceRepository.save(service);
    }

    @Override
    @Transactional(readOnly = true)
    public com.p3.p3POO.domain.model.service.Service findServiceById(String id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new DomainException("Service not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<com. p3.p3POO. domain.model.service.Service> findAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.p3.p3POO.domain.model.service.Service> findServicesByType(ServiceType type) {
        return serviceRepository.findByServiceType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean serviceExists(String id) {
        return serviceRepository.existsById(id);
    }
}