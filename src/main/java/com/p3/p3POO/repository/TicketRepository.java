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

    List<Ticket> findByCashier(Cashier cashier);

    List<Ticket> findByCashierOrderByIdAsc(Cashier cashier);

    List<Ticket> findByClient(Client client);

    List<Ticket> findByState(TicketState state);

    List<Ticket> findByCashierAndState(Cashier cashier, TicketState state);

    boolean existsById(String id);

    List<Ticket> findAllByOrderByCashierIdAsc();
}
