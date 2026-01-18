package com.p3.p3POO.model.service;

import com.p3.p3POO.model.enums.ServiceType;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "services")
public class ServiceProduct {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(nullable = false)
    private LocalDate maxUsageDate;


    public ServiceProduct() {}

    public ServiceProduct(String id, ServiceType serviceType, LocalDate maxUsageDate) {
        this.id = id;
        this.serviceType = serviceType;
        this.maxUsageDate = maxUsageDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public LocalDate getMaxUsageDate() {
        return maxUsageDate;
    }

    public void setMaxUsageDate(LocalDate maxUsageDate) {
        this.maxUsageDate = maxUsageDate;
    }

    public boolean isValidForDate(LocalDate currentDate) {
        return currentDate.isBefore(maxUsageDate) || currentDate.isEqual(maxUsageDate);
    }

    public static String generateId(int sequenceNumber) {
        return sequenceNumber + "S";
    }

    @Override
    public String toString() {
        java.time.ZonedDateTime zdt = maxUsageDate.atStartOfDay(java.time.ZoneId.of("CET"));
        java.util.Date date = java.util.Date.from(zdt.toInstant());
        return String.format("{class:ProductService, id:%s, category:%s, expiration:%s}", id, serviceType, date);
    }
}