package com.gaiga.orderservice.messagequeue;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaiga.orderservice.dto.Field;
import com.gaiga.orderservice.dto.KafkaOrderDto;
import com.gaiga.orderservice.dto.KafkaOrderDto;
import com.gaiga.orderservice.dto.OrderDto;
import com.gaiga.orderservice.dto.Payload;
import com.gaiga.orderservice.dto.Schema;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderProducer {

	private KafkaTemplate<String, String> kafkaTemplate;
	//필드 정보 입력
	List<Field> fields = Arrays.asList(
				new Field("string", true, "order_id"),
				new Field("string", true, "user_id"),
				new Field("string", true, "product_id"),
				new Field("int32", true, "qty"),
				new Field("int32", true, "unit_price"),
				new Field("int32", true, "total_price")
			);
	//빌더 패턴. 이게 더 직관적임. 
	Schema schema = Schema.builder()
					.type("struct")
					.fields(fields)
					.optional(false)
					.name("orders")
					.build();
	
	@Autowired
	public OrderProducer(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	public OrderDto send(String topic, OrderDto orderDto) {
		
		Payload payload = Payload.builder()
						.order_id(orderDto.getOrderId())
						.user_id(orderDto.getUserId())
						.product_id(orderDto.getProductId())
						.qty(orderDto.getQty())
						.unit_price(orderDto.getUnitPrice())
						.total_price(orderDto.getTotalPrice())
						.build();
		
		KafkaOrderDto kafkaOrderDto = new KafkaOrderDto(schema, payload);
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = "";
		try {
			jsonInString = mapper.writeValueAsString(kafkaOrderDto);
		}catch(JsonProcessingException e) {
			e.printStackTrace();
		}
		kafkaTemplate.send(topic, jsonInString);
		log.info("Order Producer sent data from the Order microservice: ===> " + kafkaOrderDto);
		return orderDto;
	}
}
