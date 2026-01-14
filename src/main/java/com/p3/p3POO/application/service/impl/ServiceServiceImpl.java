package com.p3.p3POO.application.service.impl;

import com.p3.p3POO.application.service.ServiceService;
import com.p3.p3POO.domain.model.enums.ServiceType;
import com.p3.p3POO.domain.model.service.Service;
import com.p3.p3POO.domain.repository.ServiceRepository;
import com.p3.p3POO.infrastructure.exception.DomainException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@org.springframework.stereotype.Service
@Transactional
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public Service createService(ServiceType serviceType, LocalDate maxUsageDate) {
        String id = generateNextServiceId();
        Service service = new Service(id, serviceType, maxUsageDate);
        return serviceRepository.save(service);
    }

    private String generateNextServiceId() {
        List<Service> services = serviceRepository.findAll();
        int maxId = 0;

        for (Service s : services) {
            try {
                // ID formato: 1S, 2S, 3S...
                String idStr = s.getId().replace("S", "");
                int currentId = Integer.parseInt(idStr);
                if (currentId > maxId) {
                    maxId = currentId;
                }
            } catch (NumberFormatException e) {
                // Ignorar IDs no numéricos
            }
        }

        return (maxId + 1) + "S";
    }

    @Override
    @Transactional(readOnly = true)
    public com.p3.p3POO.domain.model.service.Service findServiceById(String id) {
        return serviceRepository.findById(id).orElseThrow(() -> new DomainException("Service not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<com. p3.p3POO.domain.model.service.Service> findAllServices() {
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