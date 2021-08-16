package com.gaiga.orderservice.controller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaiga.orderservice.dto.OrderDto;
import com.gaiga.orderservice.jpa.OrderEntity;
import com.gaiga.orderservice.service.OrderService;
import com.gaiga.orderservice.vo.RequestOrder;
import com.gaiga.orderservice.vo.ResponseOrder;

@RestController
@RequestMapping("/order-service")
public class OrderController {

	Environment env;
	OrderService orderService;
	
	@Autowired
	public OrderController(Environment env, OrderService orderService) {
		this.env = env;
		this.orderService = orderService;
	}
	
	@GetMapping("/health_check")
	public String status() {
		return String.format("It's Working in Order Service on Port %s", 
						env.getProperty("local.server.port")
						);
	}
	
	//~/order-service/{user_id}/orders 로 조
	@PostMapping("/{userId}/orders")
	public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
													@RequestBody RequestOrder requestOrder){
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		OrderDto orderDto = mapper.map(requestOrder, OrderDto.class);
		orderDto.setUserId(userId);
		OrderDto createdOrder = orderService.createOrder(orderDto);
		
		ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class);
		
		return ResponseEntity.status(HttpStatus.OK).body(responseOrder);
	
	}
	
	@GetMapping("/{userId}/orders")
	public ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable("userId") String userId){
		
		Iterable<OrderEntity> orderList= orderService.getOrdersByUserId(userId);
		
		List<ResponseOrder> result = new ArrayList<>();
		orderList.forEach(v -> {
			result.add(new ModelMapper().map(v, ResponseOrder.class));
		});
		
		return ResponseEntity.status(HttpStatus.OK).body(result);
		
	}
	
}
