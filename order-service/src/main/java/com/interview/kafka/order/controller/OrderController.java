package com.interview.kafka.order.controller;

import com.interview.kafka.common.dto.CreateOrderRequest;
import com.interview.kafka.common.dto.CreateOrderResponse;
import com.interview.kafka.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request,
                                           @RequestHeader(value = "X-Correlation-Id", required = false)
                                           String correlationId) {
        return orderService.createOrder(request, correlationId);
    }
}
