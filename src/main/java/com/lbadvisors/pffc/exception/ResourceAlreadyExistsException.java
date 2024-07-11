package com.lbadvisors.pffc.exception;

import com.lbadvisors.pffc.controllers.orders.OrderGetDto;

import lombok.Getter;

@Getter
public class ResourceAlreadyExistsException extends RuntimeException {

    private OrderGetDto orderDto;

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String message, OrderGetDto orderDto) {
        super(message);
        this.orderDto = orderDto;
    }

}