package com.groot.invoicify.service;

import com.groot.invoicify.dto.ItemDto;
import com.groot.invoicify.entity.Item;
import com.groot.invoicify.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<ItemDto> fetchItems() {
        var items = this.itemRepository.findAll();
        return items.stream().map(ItemService::MapToDto).collect(Collectors.toList());
    }

    private static ItemDto MapToDto(Item item) {
        return new ItemDto(item.getDescription(),
                item.getRateHourBilled(),
                item.getRatePrice(),
                item.getFlatPrice());
    }
}
