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
    private double totalCost;
    private String author;
    private Boolean paid;
    @JsonProperty("items")
    private List<ItemDto> itemsDto;

    public InvoiceDto(Long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public InvoiceDto(Long invoiceNumber, String companyName, String author, Boolean paid, List<ItemDto> itemsDto) {
        this.invoiceNumber = invoiceNumber;
        this.companyName = companyName;
        this.author = author;
        this.paid = paid;
        this.itemsDto = itemsDto;
        //Calculate total cost
        this.totalCost = itemsDto.stream().mapToDouble(item -> calculateItemTotal(item)).sum();
    }

    public InvoiceDto(String companyName, String author, Boolean paid, List<ItemDto> itemsDto) {
        this.companyName = companyName;
        this.author = author;
        this.paid = paid;
        this.itemsDto = itemsDto;
        //Calculate total cost
        this.totalCost = itemsDto.stream().mapToDouble(item -> calculateItemTotal(item)).sum();
    }
    private Float calculateItemTotal(ItemDto itemDto) {
        var itemTotal = 0F;
        if(itemDto.getFlatPrice() != null) {
            itemTotal = itemDto.getFlatPrice();
        }
        if(itemDto.getRatePrice() != null) {
            itemTotal += itemDto.getRatePrice() * itemDto.getRateHourBilled();
        }
        return itemTotal;
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
