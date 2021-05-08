package com.groot.invoicify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long itemId;
	private String description;
	private Integer rateHourBilled;
	private Float ratePrice;
	private Float flatPrice;

	public Item(String description, Integer rateHourBilled, Float ratePrice, Float flatPrice) {
		this.description = description;
		this.rateHourBilled = rateHourBilled;
		this.ratePrice = ratePrice;
		this.flatPrice = flatPrice;
	}
}
