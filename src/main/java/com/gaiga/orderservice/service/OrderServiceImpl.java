package com.gaiga.orderservice.service;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gaiga.orderservice.dto.OrderDto;
import com.gaiga.orderservice.jpa.OrderEntity;
import com.gaiga.orderservice.jpa.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {
	
	OrderRepository orderRepository;
	
	@Autowired
	public OrderServiceImpl(OrderRepository orderRepository) {
		this.orderRepository=orderRepository;
	}
	
	@Override
	public OrderDto createOrder(OrderDto orderDto) {
		
		orderDto.setOrderId(UUID.randomUUID().toString());
		orderDto.setTotalPrice(orderDto.getQty()*orderDto.getUnitPrice());
		
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		OrderEntity orderEntity = mapper.map(orderDto, OrderEntity.class);
		
		orderRepository.save(orderEntity);
		
		//근데 앞에 orderDto 그대로 받아오니 이걸 리턴하면 될텐데
		//굳이 OrderEntity로 변환한 걸 다시 orderDto로 변환하는 이유는??
		OrderDto returnValue = mapper.map(orderEntity, OrderDto.class);
		
		return returnValue;
	}

	@Override
	public OrderDto getOrderByOrderId(String orderId) {
		OrderEntity orderEntity = orderRepository.findByOrderId(orderId);
		OrderDto orderDto = new ModelMapper().map(orderEntity, OrderDto.class);
		return orderDto;
	}

	@Override
	public Iterable<OrderEntity> getOrdersByUserId(String userId) {

		return orderRepository.findByUserId(userId);
	}

}
