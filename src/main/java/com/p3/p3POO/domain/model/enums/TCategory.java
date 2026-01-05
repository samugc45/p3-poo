package com.p3.p3POO.domain.model.enums;

public enum TCategory {
    MERCH(0.0),
    STATIONERY(0.05),
    CLOTHES(0.07),
    BOOK(0.10),
    ELECTRONICS(0.03);

    private final double discountRate;

    TCategory(double rate){
        this.discountRate = rate;
    }

    public double getDiscount(){
        return discountRate;
    }
}
