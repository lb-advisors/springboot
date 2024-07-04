package com.lbadvisors.pffc.orders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(value = "/customers/{id}/orders")
    public ResponseEntity<OrderGetDto> createOrder(@RequestBody OrderPostDto orderPostDto) {

        return new ResponseEntity<>(
                orderService.saveOrder(orderPostDto), HttpStatus.OK);
    }

}
