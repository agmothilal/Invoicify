package com.groot.invoicify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ITEM_SEQ")
	private Long itemId;
	private String description;
	private Integer rateHourBilled;
	private Float ratePrice;
	private Float flatPrice;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="invoice_id")
	private Invoice invoice;

	public Item(Long itemId, String description, Integer rateHourBilled, Float ratePrice, Float flatPrice) {
		this.itemId = itemId;
		this.description = description;
		this.rateHourBilled = rateHourBilled;
		this.ratePrice = ratePrice;
		this.flatPrice = flatPrice;
	}

	public Item(String description, Integer rateHourBilled, Float ratePrice, Float flatPrice) {
		this.description = description;
		this.rateHourBilled = rateHourBilled;
		this.ratePrice = ratePrice;
		this.flatPrice = flatPrice;
	}

	public Item(String description, Integer rateHourBilled, Float ratePrice, Float flatPrice,
				Invoice invoice) {
		this.description = description;
		this.rateHourBilled = rateHourBilled;
		this.ratePrice = ratePrice;
		this.flatPrice = flatPrice;
		this.invoice = invoice;
	}
}
