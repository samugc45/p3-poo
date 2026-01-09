package com.p3.p3POO.application.service;

import com.p3.p3POO.domain.model.user. Cashier;
import com.p3.p3POO.domain. model.user.Client;
import com.p3.p3POO.domain.model.user.CompanyClient;

import java.util.List;

public interface UserService {

    // Cashier operations
    Cashier createCashier(String name, String email);
    Cashier findCashierById(String employeeCode);
    List<Cashier> findAllCashiers();
    boolean cashierExists(String employeeCode);

    // Client operations
    Client createClient(String dni, String name, String email, String cashierCode);
    Client findClientById(String dni);
    List<Client> findAllClients();
    boolean clientExists(String dni);

    // Company client operations
    CompanyClient createCompanyClient(String nif, String companyName, String email, String cashierCode);
    CompanyClient findCompanyClientById(String nif);
}
