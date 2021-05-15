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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "INVOICE_SEQ")
	private Long invoiceId;
	@ManyToOne
	@JoinColumn
	private Company company;
	//private Float totalCost;
	private String author;
	private Boolean paid;
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "invoice")
	private List<Item> item;
	@CreationTimestamp
	@Column(nullable = false, updatable = false, insertable = false,
			columnDefinition = "DATE DEFAULT CURRENT_DATE"
	)
	private Timestamp createDt;
	@LastModifiedDate
	@Column(nullable = false, insertable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE"
	)
	private Timestamp modifiedDt;

    public Invoice(Company company, String author, Boolean paid) {
        this.company = company;
        this.author = author;
        this.paid = paid;
    }

	public Invoice(String author, Boolean paid, Timestamp createDt) {
		this.author = author;
		this.paid = paid;
		this.createDt = createDt;
	}

	public Invoice(Company company, String author, Boolean paid, List<Item> item) {
		this.company = company;
		this.author = author;
		this.paid = paid;
		this.item = item;
	}
}
