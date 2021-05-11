package com.groot.invoicify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {

    private String companyName;
    private Float totalCost;
    private String author;
    private Boolean paid;
    private List<ItemDto> itemsDto;

    public InvoiceDto(String companyName, String author, Boolean paid, List<ItemDto> itemsDto) {
        this.companyName = companyName;
        this.author = author;
        this.paid = paid;
        this.itemsDto = itemsDto;
    }
}
