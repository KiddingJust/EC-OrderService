package com.gaiga.orderservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import com.gaiga.orderservice.messagequeue.KafkaProducer;
import com.gaiga.orderservice.messagequeue.OrderProducer;
import com.gaiga.orderservice.service.OrderService;
import com.gaiga.orderservice.vo.RequestOrder;
import com.gaiga.orderservice.vo.ResponseOrder;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/order-service")
@Slf4j
public class OrderController {

	Environment env;
	OrderService orderService;
	KafkaProducer kafkaProducer;
	OrderProducer orderProducer;
	
	@Autowired
	public OrderController(Environment env, OrderService orderService, KafkaProducer kafkaProducer, OrderProducer orderProducer) {
		this.env = env;
		this.orderService = orderService;
		this.kafkaProducer = kafkaProducer;
		this.orderProducer = orderProducer;
	}
	
	@GetMapping("/health_check")
	public String status() {
		return String.format("It's Working in Order Service on Port %s", 
						env.getProperty("local.server.port")
						);
	}
	
	//~/order-service/{user_id}/orders 로 
	@PostMapping("/{userId}/orders")
	public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
													@RequestBody RequestOrder requestOrder){
		log.info("Before add orders data");
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		
		OrderDto orderDto = mapper.map(requestOrder, OrderDto.class);
		orderDto.setUserId(userId);
		
		/* JPA */
		OrderDto createdOrder = orderService.createOrder(orderDto);		
		ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class);
		
//		/* Kafka 통해 orders 토픽에 전달 */
//		orderDto.setOrderId(UUID.randomUUID().toString());
//		orderDto.setTotalPrice(requestOrder.getQty() * requestOrder.getUnitPrice());
//		
//		/* Kafka Topic에 Message 전달 */
		kafkaProducer.send("ecommerce-product-topic", orderDto);
//		orderProducer.send("orders", orderDto);
//		
//		ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);

		log.info("after add orders data");
		return ResponseEntity.status(HttpStatus.OK).body(responseOrder);
	}
	
	@GetMapping("/{userId}/orders")
	public ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable("userId") String userId){
		log.info("Before return orders data ");
		Iterable<OrderEntity> orderList= orderService.getOrdersByUserId(userId);
		
		
		List<ResponseOrder> result = new ArrayList<>();
		orderList.forEach(v -> {
			result.add(new ModelMapper().map(v, ResponseOrder.class));
		});
		log.info("after return orders data ");

		return ResponseEntity.status(HttpStatus.OK).body(result);
		
	}
	
}
