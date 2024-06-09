package com.example.order_service.service;

public class OutOfStockException extends Throwable {
    public OutOfStockException(String productNotInStock) {
        super(productNotInStock);
    }
}
