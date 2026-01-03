package com.p3.p3POO.domain.model.product;

import com.p3.p3POO.domain.model.TCategory;

public class Product {git stat
    private final Integer id;
    private String name;
    private TCategory category;
    private Double price;

    public Product(Integer id, String name, TCategory category, Double price){
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public Product(Integer id, String name, Double price){
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Integer getID() {
        return id;
    }

    public String getName(){
        return name;
    }

    public TCategory getCategory() {
        return category;
    }

    public Double getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(TCategory category) {
        this.category = category;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{class:Product, ");
        sb.append("id:").append(id).append(", ");
        sb.append("name:'").append(name).append("', ");
        sb.append("category:").append(category).append(", ");
        sb.append("price:").append(price).append("}");
        return sb.toString();
    }
}
