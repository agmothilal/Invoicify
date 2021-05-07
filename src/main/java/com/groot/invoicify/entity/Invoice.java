package com.groot.invoicify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long invoiceId;
	private Company company;
	private Float totalCost;
	private String author;
	private Boolean paid;
	@ManyToMany(cascade = CascadeType.REMOVE,
			fetch = FetchType.LAZY)
	@JoinTable(name = "item_invoice",
			joinColumns = @JoinColumn(name = "invoice_id"),
			inverseJoinColumns = @JoinColumn(name = "item_id"))
	private List<Item> item;
	@CreationTimestamp
	@Column(nullable = false, updatable = false, insertable = false)
	private Timestamp createDt;
	@LastModifiedDate
	@Column(nullable = false, insertable = false)
	private Timestamp modifiedDt;
}
