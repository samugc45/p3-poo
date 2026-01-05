package com.p3.p3POO.domain.model.product;

import java.time.LocalDateTime;

public class MeetingProduct extends Event {

    public MeetingProduct(int id, String name, Double price, LocalDateTime date, int maxParticipants) {
        super(id, name, price, date, maxParticipants);
    }

}