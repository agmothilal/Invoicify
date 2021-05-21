package com.groot.invoicify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CompanyDto
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {

	private Long companyId;
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
	 * @param dts
	 */
	public CompanyDto(String dts) {
		name = dts;
	}

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
	public CompanyDto(String name, String address, String city, String state, String zip, String contactName, String contactTitle, String contactPhoneNumber) {
		this.name = name;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.contactName = contactName;
		this.contactTitle = contactTitle;
		this.contactPhoneNumber = contactPhoneNumber;
	}
}
