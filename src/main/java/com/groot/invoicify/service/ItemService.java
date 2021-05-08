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

    private static ItemDto MapToDto(Item item) {
        return new ItemDto(item.getDescription(),
                item.getRateHourBilled(),
                item.getRatePrice(),
                item.getFlatPrice());
    }

    private static Item MapToEntity(ItemDto itemDto) {
        return new Item(itemDto.getDescription(),
                itemDto.getRateHourBilled(),
                itemDto.getRatePrice(),
                itemDto.getFlatPrice());
    }

    public List<ItemDto> fetchItems() {
        var items = this.itemRepository.findAll();
        return items.stream().map(ItemService::MapToDto).collect(Collectors.toList());
    }

    public void saveItem(ItemDto itemDto) {
        this.itemRepository.save(MapToEntity(itemDto));
    }

    public void updateItem(Long itemId, ItemDto itemDto) {
        var item = this.itemRepository.findById(itemId);
        var updatedItem = MapToEntity(itemDto);
        if(item.isPresent()) {
            updatedItem.setItemId(item.get().getItemId());
            this.itemRepository.save(updatedItem);
        }
    }
}
