package com.gaiga.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

//어떤 필드가 사용될지 지정
@Data
@AllArgsConstructor
public class Field {
	private String type;
	private boolean optional;
	private String field;
}
