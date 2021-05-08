package com.groot.invoicify.company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {

    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String contactName;
    private String contactTitle;
    private String contactPhoneNumber;

    public CompanyDto(String dts) {
        name = dts;
    }
}
