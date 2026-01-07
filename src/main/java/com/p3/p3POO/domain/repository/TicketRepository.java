package com.p3.p3POO.domain.repository;

import com.p3.p3POO.domain.model.Ticket;
import com.p3.p3POO.domain.model.enums.TicketState;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {

    List<Ticket> findAll();

    Ticket findById(String id);

    Ticket save(Ticket ticket);

    void deleteById(String id);

    List<Ticket> findByState(TicketState state);

    List<Ticket> findByClientId(String clientId);

    List<Ticket> findByCashierId(String cashierId);

    Boolean existsById(String id);
}
