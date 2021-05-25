package com.groot.invoicify.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Company
 *
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long companyId;
	@Column(unique = true)
	private String name;
	private String address;
	private String city;
	private String state;
	private String zip;
	private String contactName;
	private String contactTitle;
	private String contactPhoneNumber;

	/**
	 *
	 * @param name
	 * @param address
	 * @param city
	 * @param state
	 * @param zip
	 * @param contactName
	 * @param contactTitle
	 * @param contactPhoneNumber
	 */
	public Company(String name, String address, String city, String state, String zip, String contactName, String contactTitle, String contactPhoneNumber) {
		this.name = name;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.contactName = contactName;
		this.contactTitle = contactTitle;
		this.contactPhoneNumber = contactPhoneNumber;
	}

	/**
	 *
	 * @param name
	 */
	public Company(String name) {
		this.name = name;
	}
}
