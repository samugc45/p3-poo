package com.p3.p3POO.domain.model.service;

import com.p3.p3POO.domain.model.enums.ServiceType;
import com.p3.p3POO.domain.model.interfaces.DateValidatable;
import com.p3.p3POO.domain.model.interfaces.IdGenerable;

import java.time.LocalDate;

import java.time.LocalDate;

public class Service implements DateValidatable {
    private final String id;
    private final ServiceType serviceType;
    private final LocalDate maxUsageDate;
    private Double calculatedPrice;

    public Service(ServiceType serviceType) {
        this.serviceType = serviceType;
        this.maxUsageDate = maxUsageDate;
        this.id = id;
        this.calculatedPrice = null;
    }

    public String getId() { return id; }
    public ServiceType getServiceType() { return serviceType; }
    public LocalDate getMaxUsageDate() { return maxUsageDate; }
    public Double getCalculatedPrice() { return calculatedPrice; }

    public void setCalculatedPrice(Double calculatedPrice) {
        this.calculatedPrice = calculatedPrice;
    }

    public Double calculateFinalPrice() {
        // falta la logica
        return calculatedPrice;
    }

    @Override
    public boolean isValidForDate(LocalDate date) {
        return date != null && !date.isAfter(maxUsageDate); // válido si date <= maxUsageDate
    }
}