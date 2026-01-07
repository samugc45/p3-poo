package com.p3.p3POO.domain.model;

import com.p3.p3POO.application.service.ProductService;
import com.p3.p3POO.domain.model.enums.TicketState;
import com.p3.p3POO.domain.model.product.Product;
import com.p3.p3POO.domain.model.user.Cashier;

import java.util.LinkedHashMap;
import java.util.Map;

public class Ticket {
    private TicketState state;
    private final String id;
    private ProductService productService;

    private Cashier cashier;

    private final Map<Product, Integer> products = new LinkedHashMap<>();

    public Ticket( String id) {
        this.id = id;
        this.state = TicketState.EMPTY;
    }

    public TicketState getState() {
        return state;
    }

    public void setState(TicketState state) {
        this.state = state;
    }


    public String getId() {
        return id;
    }

    public ProductService getProductService() {
        return productService;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public void setCashier(Cashier cashier) {
        this.cashier = cashier;
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }
}
