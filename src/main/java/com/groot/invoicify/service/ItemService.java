package com.groot.invoicify.service;

import com.groot.invoicify.dto.ItemDto;
import com.groot.invoicify.entity.Invoice;
import com.groot.invoicify.entity.Item;
import com.groot.invoicify.repository.ItemRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ItemService
 *
 */
@Service
public class ItemService {

	private final ItemRepository itemRepository;

	/**
	 *
	 * @param itemRepository
	 */
	@Autowired
	public ItemService(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	/**
	 *
	 * @param item
	 * @return
	 */
	public static ItemDto MapToDto(Item item) {
		return new ItemDto(item.getItemId(),
				item.getDescription(),
				item.getRateHourBilled(),
				item.getRatePrice(),
				item.getFlatPrice());
	}

	/**
	 *
	 * @param itemDto
	 * @return
	 */
	public static Item MapToEntity(ItemDto itemDto) {
		return new Item(itemDto.getDescription(),
				itemDto.getRateHourBilled(),
				itemDto.getRatePrice(),
				itemDto.getFlatPrice());
	}

	/**
	 *
	 * @param itemDto
	 * @param item
	 * @return
	 */
	private static Item MapToEntityPatch(ItemDto itemDto, Item item) {
		return new Item(((itemDto.getDescription() != null) ? itemDto.getDescription() : item.getDescription()),
				((itemDto.getRateHourBilled() != null) ? itemDto.getRateHourBilled() : item.getRateHourBilled()),
				((itemDto.getRatePrice() != null) ? itemDto.getRatePrice() : item.getRatePrice()),
				((itemDto.getFlatPrice() != null) ? itemDto.getFlatPrice() : item.getFlatPrice()));
	}

	/**
	 *
	 * @return
	 */
	public List<ItemDto> fetchItems() {
		var items = this.itemRepository.findAll();
		return items.stream().map(ItemService::MapToDto).collect(Collectors.toList());
	}

	/**
	 *
	 * @param itemDto
	 * @return
	 */
	public Long saveItem(ItemDto itemDto) {
		var result = this.itemRepository.save(MapToEntity(itemDto));
		return result.getItemId();
	}

	/**
	 *
	 * @param put
	 * @param itemId
	 * @param itemDto
	 */
	public void updateItem(boolean put, Long itemId, ItemDto itemDto) {
		var item = this.itemRepository.findById(itemId);
		if (item.isPresent()) {
			var updatedItem = ((put) ? MapToEntity(itemDto) : MapToEntityPatch(itemDto, item.get()));
			updatedItem.setItemId(item.get().getItemId());
			this.itemRepository.save(updatedItem);
		}
	}

	/**
	 *
	 * @param invoiceEntityObject
	 * @param itemsDtoList
	 */
	public void addItemsToGivenInvoiceNumber(Invoice invoiceEntityObject, List<ItemDto> itemsDtoList) {
		List<Item> itemEntityList = itemsDtoList.stream().map(ItemService::MapToEntity).collect(Collectors.toList());
		itemEntityList.stream().forEach(itemEntity -> {
			itemEntity.setInvoice(invoiceEntityObject);
		});
		itemRepository.saveAll(itemEntityList);

	}
}
