package com.gaiga.orderservice.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import lombok.Data;

@Data
@Entity
@Table(name="orders")
public class OrderEntity implements Serializable {
	//직렬화하는 이유는 마셜링, 언마셜링 때문?
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 120, unique = true)
	private String productId;
	
	@Column(nullable = false)
	private Integer qty;
	
	@Column(nullable = false)
	private Integer unitPrice;
	
	@Column(nullable = false)
	private Integer totalPrice;

	@Column(nullable = false)
	private String userId;
	
	@Column(nullable = false, unique = true)
	private String orderId;
	
	@Column(nullable = false, updatable = false, insertable = false)
	//디폴트값 넣어주기. 현재 DATE 값 넣어주는 것. 
	@ColumnDefault(value = "CURRENT_TIMESTAMP")
	private Date createdAt;
}
