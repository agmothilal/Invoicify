package com.groot.invoicify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private Long invoiceNumber;
    private String companyName;
    private Float totalCost;
    private String author;
    private Boolean paid;
    @JsonProperty("items")
    private List<ItemDto> itemsDto;

    public InvoiceDto(String companyName, String author, Boolean paid, List<ItemDto> itemsDto) {
        this.companyName = companyName;
        this.author = author;
        this.paid = paid;
        this.itemsDto = itemsDto;
    }

    public InvoiceDto(Long invoiceNumber,String companyName, String author, Boolean paid, List<ItemDto> itemsDto,Float totalCost) {
        this.companyName = companyName;
        this.author = author;
        this.paid = paid;
        this.itemsDto = itemsDto;
        this.invoiceNumber = invoiceNumber;
        this.totalCost = totalCost;
    }
}
