package com.groot.invoicify.entity;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Invoice
 *
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {

	@Id
	@SequenceGenerator(name="INVOICE_SEQ",
			sequenceName="INVOICE_SEQ",
			allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INVOICE_SEQ")
	private Long invoiceId;
	@ManyToOne
	@JoinColumn(name = "company_id")
	private Company company;
	private String author;
	private Boolean paid;
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "invoice")
	private List<Item> item;
	@CreationTimestamp
	@Column(nullable = false, updatable = false, insertable = false,
			columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
	)
	private Timestamp createDt;
	@LastModifiedDate
	@Column(nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
	)
	private Timestamp modifiedDt;

	/**
	 *
	 * @param invoiceId
	 */
	public Invoice(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	/**
	 *
	 * @param company
	 * @param author
	 * @param paid
	 */
	public Invoice(Company company, String author, Boolean paid) {
		this.company = company;
		this.author = author;
		this.paid = paid;
	}

	/**
	 *
	 * @param author
	 * @param paid
	 * @param createDt
	 */
	public Invoice(String author, Boolean paid, Timestamp createDt) {
		this.author = author;
		this.paid = paid;
		this.createDt = createDt;
	}

	/**
	 *
	 * @param company
	 * @param author
	 * @param paid
	 * @param item
	 */
	public Invoice(Company company, String author, Boolean paid, List<Item> item) {
		this.company = company;
		this.author = author;
		this.paid = paid;
		this.item = item;
	}
}
