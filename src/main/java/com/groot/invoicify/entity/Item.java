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
	private Integer quantity;
	private Float totalFee;
	private Float rate;
	private Integer rateQuantity;
	private Integer flatAmount;
	@ManyToMany(cascade = CascadeType.REMOVE,
			fetch = FetchType.LAZY)
	@JoinTable(name = "item_invoice",
			joinColumns = @JoinColumn(name = "item_id"),
			inverseJoinColumns = @JoinColumn(name = "invoice_id"))
	private List<Invoice> item;
}
