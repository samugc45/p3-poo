package com.p3.p3POO.application.service.impl;

import com.p3.p3POO.application.service.UserService;
import com.p3.p3POO.domain.model.user.Cashier;
import com.p3.p3POO.domain.model.user.Client;
import com.p3.p3POO.domain.model.user.CompanyClient;
import com.p3.p3POO.domain.repository.UserRepository;
import com.p3.p3POO.infrastructure.exception.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Cashier createCashier(String name, String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DomainException("Email already exists:  " + email);
        }

        String employeeCode = Cashier.generateEmployeeCode();
        while (userRepository.existsById(employeeCode)) {
            employeeCode = Cashier.generateEmployeeCode();
        }

        Cashier cashier = new Cashier(employeeCode, name, email);
        return userRepository.save(cashier);
    }

    @Override
    @Transactional(readOnly = true)
    public Cashier findCashierById(String employeeCode) {
        return userRepository.findById(employeeCode)
                .filter(user -> user instanceof Cashier)
                .map(user -> (Cashier) user)
                .orElseThrow(() -> new DomainException("Cashier not found:  " + employeeCode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cashier> findAllCashiers() {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Cashier)
                .map(user -> (Cashier) user)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean cashierExists(String employeeCode) {
        return userRepository.findById(employeeCode)
                .map(user -> user instanceof Cashier)
                .orElse(false);
    }

    @Override
    public Client createClient(String dni, String name, String email, String cashierCode) {
        if (!Client.isValidDNI(dni)) {
            throw new DomainException("Invalid DNI format: " + dni);
        }

        if (userRepository.existsById(dni)) {
            throw new DomainException("Client already exists: " + dni);
        }

        if (userRepository.existsByEmail(email)) {
            throw new DomainException("Email already exists:  " + email);
        }

        Cashier cashier = findCashierById(cashierCode);
        Client client = new Client(dni, name, email, cashier);
        return userRepository.save(client);
    }

    @Override
    @Transactional(readOnly = true)
    public Client findClientById(String dni) {
        return userRepository.findById(dni)
                .filter(user -> user instanceof Client)
                .map(user -> (Client) user)
                .orElseThrow(() -> new DomainException("Client not found: " + dni));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAllClients() {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Client)
                .map(user -> (Client) user)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean clientExists(String dni) {
        return userRepository.findById(dni)
                .map(user -> user instanceof Client)
                .orElse(false);
    }

    @Override
    public CompanyClient createCompanyClient(String nif, String companyName, String email, String cashierCode) {
        if (!CompanyClient.isValidNIF(nif)) {
            throw new DomainException("Invalid NIF format: " + nif);
        }

        if (userRepository.existsById(nif)) {
            throw new DomainException("Company client already exists: " + nif);
        }

        if (userRepository.existsByEmail(email)) {
            throw new DomainException("Email already exists: " + email);
        }

        Cashier cashier = findCashierById(cashierCode);
        CompanyClient companyClient = new CompanyClient(nif, companyName, email, cashier);
        return userRepository.save(companyClient);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyClient findCompanyClientById(String nif) {
        return userRepository.findById(nif)
                .filter(user -> user instanceof CompanyClient)
                .map(user -> (CompanyClient) user)
                .orElseThrow(() -> new DomainException("Company client not found: " + nif));
    }
}
