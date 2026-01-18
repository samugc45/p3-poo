package com.p3.p3POO.validator;

import com.p3.p3POO.model.product.FoodProduct;
import com.p3.p3POO.model.product.MeetingProduct;
import com.p3.p3POO.exception.DomainException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class EventValidator {

    private static final int MIN_MEETING_HOURS = 12;
    private static final int MIN_FOOD_DAYS = 3;

    public void validateMeetingCreation(LocalDateTime eventDate) {
        LocalDateTime now = LocalDateTime.now();
        long hoursBetween = ChronoUnit.HOURS.between(now, eventDate);

        if (hoursBetween < MIN_MEETING_HOURS) {
            throw new DomainException(
                    String.format("Meeting must be created at least %d hours in advance.  Hours remaining: %d",
                            MIN_MEETING_HOURS, hoursBetween)
            );
        }
    }

    public void validateFoodCreation(LocalDateTime eventDate, LocalDate expirationDate) {
        LocalDate now = LocalDate.now();
        LocalDate eventDay = eventDate.toLocalDate();

        long daysBetween = ChronoUnit.DAYS.between(now, eventDay);
        if (daysBetween < MIN_FOOD_DAYS) {
            throw new DomainException(String.format("Food event must be created at least %d days in advance. Days remaining: %d", MIN_FOOD_DAYS, daysBetween));
        }

        // Validar que no haya caducado
        if (now.isAfter(expirationDate)) {
            throw new DomainException(String.format("Food product has expired. Expiration date: %s, Current date: %s", expirationDate, now));
        }
    }

    public void validateMeetingForTicket(MeetingProduct meeting, LocalDate currentDate) {
        if (!meeting.isValidForDate(currentDate)) {
            throw new DomainException(String.format("Meeting event %s is not valid for date %s (requires 12h advance)", meeting.getId(), currentDate));
        }
    }

    public void validateFoodForTicket(FoodProduct food, LocalDate currentDate) {
        if (!food.isValidForDate(currentDate)) {
            throw new DomainException(String.format("Food event %s is not valid for date %s (requires 3 days advance and not expired)", food. getId(), currentDate));
        }
    }
}
