package com.p3.p3POO.service.impl;

import com.p3.p3POO.model.service.ServiceProduct;
import com.p3.p3POO.service.ServiceServiceProduct;
import com.p3.p3POO.model.enums.ServiceType;
import com.p3.p3POO.repository.ServiceRepository;
import com.p3.p3POO.exception.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ServiceServiceProductImpl implements ServiceServiceProduct {

    private final ServiceRepository serviceRepository;

    public ServiceServiceProductImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public ServiceProduct createService(ServiceType serviceType, LocalDate maxUsageDate) {
        String id = generateNextServiceId();
        ServiceProduct serviceProduct = new ServiceProduct(id, serviceType, maxUsageDate);
        return serviceRepository.save(serviceProduct);
    }

    private String generateNextServiceId() {
        List<ServiceProduct> serviceProducts = serviceRepository.findAll();
        int maxId = 0;

        for (ServiceProduct s : serviceProducts) {
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
    public ServiceProduct findServiceById(String id) {
        return serviceRepository.findById(id).orElseThrow(() -> new DomainException("Service not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProduct> findAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProduct> findServicesByType(ServiceType type) {
        return serviceRepository.findByServiceType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean serviceExists(String id) {
        return serviceRepository.existsById(id);
    }
}