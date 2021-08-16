package com.gaiga.orderservice.service;

import com.gaiga.orderservice.dto.OrderDto;
import com.gaiga.orderservice.jpa.OrderEntity;

public interface OrderService {

	OrderDto createOrder(OrderDto orderDetails);
	//주문 번호로 조회
	OrderDto getOrderByOrderId(String orderId);
	//사용자 한명이 여러 주문을 할 수 있기 때문 
	Iterable<OrderEntity> getOrdersByUserId(String userId);
}
