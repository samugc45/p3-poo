package com.p3.p3POO.domain.model.service;

import com.p3.p3POO.domain.model.enums.ServiceType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "services")
public class Service {

    @Id
    private String id;  // Formato: 1S, 2S, 3S, etc.  (numérico + S)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(nullable = false)
    private LocalDate maxUsageDate;  // Fecha máxima de uso

    // Los servicios NO tienen precio al crearlos (se factura después)
    // NO tienen nombre (solo ID y tipo)

    // Constructor sin argumentos (JPA)
    public Service() {}

    // Constructor completo
    public Service(String id, ServiceType serviceType, LocalDate maxUsageDate) {
        this.id = id;
        this.serviceType = serviceType;
        this.maxUsageDate = maxUsageDate;
    }

    // Validar si el servicio puede agregarse en una fecha determinada
    public boolean isValidForDate(LocalDate currentDate) {
        return currentDate. isBefore(maxUsageDate) || currentDate.isEqual(maxUsageDate);
    }

    // Generador de ID secuencial:  1S, 2S, 3S, etc.
    public static String generateId(int sequenceNumber) {
        return sequenceNumber + "S";
    }

    @Override
    public String toString() {
        // Formato: {class:ProductService, id:1, category:INSURANCE, expiration:Sun Dec 21 00:00:00 CET 2025}
        java.time.ZonedDateTime zdt = maxUsageDate.atStartOfDay(java.time.ZoneId.of("CET"));
        java.util.Date date = java.util.Date.from(zdt.toInstant());
        return String.format("{class:ProductService, id:%s, category:%s, expiration:%s}",
                id, serviceType, date);
    }
}