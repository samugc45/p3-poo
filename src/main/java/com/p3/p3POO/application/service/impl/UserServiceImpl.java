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
    public Cashier createCashier(String id, String name, String email) {
        if (userRepository.existsById(id)) {
            throw new DomainException("Cashier ID already exists: " + id);
        }

        Cashier cashier = new Cashier(id, name, email);
        return userRepository.save(cashier);
    }

    @Override
    public Cashier createCashierAutoId(String name, String email) {
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
    public void deleteCashier(String employeeCode) {
        Cashier cashier = findCashierById(employeeCode);
        userRepository.deleteById(cashier.getId());
    }

    private void validateDni(String dni) {
        // Validar formato:  DNI (8 dígitos + letra) o NIE (X/Y/Z + 7 dígitos + letra)
        if (!dni.matches("^[0-9]{8}[A-Z]$") && !dni.matches("^[XYZ][0-9]{7}[A-Z]$")) {
            throw new DomainException("Invalid DNI format: " + dni);
        }

        // Validar letra del DNI/NIE
        String dniLetters = "TRWAGMYFPDXBNJZSQVHLCKE";
        String numberPart = dni.substring(0, dni.length() - 1);

        // Para NIE, convertir primera letra a número
        if (numberPart.startsWith("X")) {
            numberPart = "0" + numberPart.substring(1);
        } else if (numberPart.startsWith("Y")) {
            numberPart = "1" + numberPart. substring(1);
        } else if (numberPart.startsWith("Z")) {
            numberPart = "2" + numberPart.substring(1);
        }

        int dniNumber = Integer.parseInt(numberPart);
        char expectedLetter = dniLetters.charAt(dniNumber % 23);
        char providedLetter = dni.charAt(dni.length() - 1);

        if (expectedLetter != providedLetter) {
            throw new DomainException("Invalid DNI letter: " + dni);
        }
    }

    @Override
    public Client createClient(String dni, String name, String email, String cashierId) {
        // Validar formato de DNI
        validateDni(dni);

        // Validar que no existe email duplicado
        if (userRepository.existsByEmail(email)) {
            throw new DomainException("Email already exists: " + email);
        }

        // Validar que no existe cliente con ese DNI
        if (userRepository.existsById(dni)) {
            throw new DomainException("Client already exists: " + dni);
        }

        // Buscar cajero
        Cashier cashier = findCashierById(cashierId);

        // Crear cliente (sin validación en constructor)
        Client client = new Client(dni, name, email, cashier);

        return userRepository.save(client);
    }

    @Override
    public void deleteClient(String dni) {
        Client client = findClientById(dni);
        userRepository.deleteById(client.getId());
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
