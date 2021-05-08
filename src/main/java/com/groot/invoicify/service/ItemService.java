package com.groot.invoicify.service;

import com.groot.invoicify.dto.ItemDto;
import com.groot.invoicify.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<ItemDto> fetchItems() {
        return null;
    }
}
