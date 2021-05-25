package com.groot.invoicify.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Item
 *
 */
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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_id")
	private Invoice invoice;

	/**
	 *
	 * @param itemId
	 * @param description
	 * @param rateHourBilled
	 * @param ratePrice
	 * @param flatPrice
	 */
	public Item(Long itemId, String description, Integer rateHourBilled, Float ratePrice, Float flatPrice) {
		this.itemId = itemId;
		this.description = description;
		this.rateHourBilled = rateHourBilled;
		this.ratePrice = ratePrice;
		this.flatPrice = flatPrice;
	}

	/**
	 *
	 * @param description
	 * @param rateHourBilled
	 * @param ratePrice
	 * @param flatPrice
	 */
	public Item(String description, Integer rateHourBilled, Float ratePrice, Float flatPrice) {
		this.description = description;
		this.rateHourBilled = rateHourBilled;
		this.ratePrice = ratePrice;
		this.flatPrice = flatPrice;
	}

	/**
	 *
	 * @param description
	 * @param rateHourBilled
	 * @param ratePrice
	 * @param flatPrice
	 * @param invoice
	 */
	public Item(String description, Integer rateHourBilled, Float ratePrice, Float flatPrice, Invoice invoice) {
		this.description = description;
		this.rateHourBilled = rateHourBilled;
		this.ratePrice = ratePrice;
		this.flatPrice = flatPrice;
		this.invoice = invoice;
	}
}
