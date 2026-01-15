package com.p3.p3POO.repository;

import com.p3.p3POO.model.Ticket;
import com.p3.p3POO.model.enums.TicketState;
import com.p3.p3POO.model.user.Cashier;
import com.p3.p3POO.model.user.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {

    // Buscar tickets por cajero
    List<Ticket> findByCashier(Cashier cashier);

    // Buscar tickets por cajero ordenados por ID
    List<Ticket> findByCashierOrderByIdAsc(Cashier cashier);

    // Buscar tickets por cliente
    List<Ticket> findByClient(Client client);

    // Buscar tickets por estado
    List<Ticket> findByState(TicketState state);

    // Buscar tickets por cajero y estado
    List<Ticket> findByCashierAndState(Cashier cashier, TicketState state);

    // Verificar si existe un ticket con ese ID
    boolean existsById(String id);

    // Obtener todos los tickets ordenados por cajero
    List<Ticket> findAllByOrderByCashierIdAsc();
}
