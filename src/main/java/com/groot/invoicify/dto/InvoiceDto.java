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
    private double totalCost;
    private String author;
    private Boolean paid;
    private List<ItemDto> itemsDto;

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
}
