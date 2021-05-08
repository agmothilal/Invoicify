package com.groot.invoicify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
}
