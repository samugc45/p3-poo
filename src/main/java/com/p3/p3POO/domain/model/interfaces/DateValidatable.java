package com.p3.p3POO.domain.model.interfaces;

import java.time.LocalDate;

public interface DateValidatable {
    boolean isValidForDate(LocalDate date);
}
