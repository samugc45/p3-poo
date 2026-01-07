package com.p3.p3POO.application.service.impl;

import com.p3.p3POO.application.factory.TicketFactory;
import com.p3.p3POO.application.service.ProductService;
import com.p3.p3POO.application.service.ServiceService;
import com.p3.p3POO.application.service.UserService;
import com.p3.p3POO.domain.model.Ticket;
import com.p3.p3POO.domain.model.enums.TicketState;
import com.p3.p3POO.domain.model.product.Product;
import com.p3.p3POO.domain.model.user.Cashier;
import com.p3.p3POO.domain.repository.TicketRepository;

import java.util.Map;

public class TicketServiceImpl {
    TicketRepository ticketRepository;
    TicketFactory ticketFactory;
    ProductService productService;
    ServiceService serviceService;
    UserService userService;

    public void addProduct(String ticketId, Product p, int quantity) {
        Ticket t = TicketRepository.findById(ticketId);
        if (t.getState() == TicketState.CLOSE) {
            throw new IllegalStateException("Error: No se pueden añadir productos a un ticket cerrado.");
        }
        t.getProducts().put(p, t.getProducts().getOrDefault(p, 0) + quantity);
        t.setState(TicketState.OPEN);
    }

    public void removeProduct(String ticketId, int prodId) {
        Ticket t = TicketRepository.findById(ticketId);
        if (t.getState() == TicketState.CLOSE) {
            throw new IllegalStateException("Error: No se pueden borrar productos de un ticket cerrado.");
        }
        Product remove = null;
        for (Product p : t.getProducts().keySet()) {
            if (p.getID() == prodId) {
                remove = p;
                break;
            }
        }

        if (remove != null) {
            t.getProducts().remove(remove);
            if (t.getProducts().isEmpty()) {
                t.setState(TicketState.EMPTY);
            }
        }
    }


    public boolean containsProduct(String ticketId, Product p) {
        Ticket t = TicketRepository.findById(ticketId);
        return t.getProducts().containsKey(p);
    }
    public ProductService getProductService(String ticketId) {
        return ticketRepository.findById(ticketId).getProductService();
    }
    public Cashier getCashier(String ticketId) {
        return ticketRepository.findById(ticketId).getCashier();
    }

    public void setCashier(String ticketId, Cashier cashier) {
        Ticket t = ticketRepository.findById(ticketId);
        t.setCashier(cashier);
    }

    public void closeTicket(String id) {
        Ticket t = ticketRepository.findById(id);
        t.setState(TicketState.CLOSE);
    }

    public Product getProductTicket(String ticketId, Integer productId){
        Ticket t = ticketRepository.findById(ticketId);
        return t.getProductService().getProductById(productId);
    }



    public String ticketPrint(String ticketId) {
        double totalPrice = 0;
        double totalDiscount = 0;
        StringBuilder sb = new StringBuilder();
        Ticket t = ticketRepository.findById(ticketId);

        for (Map.Entry<Product, Integer> entry : t.getProducts().entrySet()) {
            Product p = entry.getKey();
            int quantity = entry.getValue();

            for (int i = 0; i < quantity; i++) {
                double discountRate = p.getCategory().getDiscount();
                double discount = 0.0;

                totalPrice += p.getPrice();

                if (entry.getValue() > 1) {
                    discount = p.getPrice() * discountRate;
                    totalDiscount += discount;
                }

                sb.append(p.toString());
                if (discount > 0) {
                    sb.append(" **discount -").append(String.format("%.1f", discount));
                }
                sb.append("\n");
            }
        }
        sb.append("Total price: ").append(totalPrice).append("\n");
        sb.append("Total discount: ").append(totalDiscount).append("\n");
        sb.append("Final Price: ").append(totalPrice - totalDiscount);

        return sb.toString();
    }
    public String toString(String ticketId) {
        Ticket t = ticketRepository.findById(ticketId);
        return ticketId + " - " + t.getState();
    }
}
