package com.groot.invoicify.unitTests;

import com.groot.invoicify.dto.ItemDto;
import com.groot.invoicify.entity.Item;
import com.groot.invoicify.repository.ItemRepository;
import com.groot.invoicify.service.ItemService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * ItemServiceTest
 *
 */
@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

	@Mock
	ItemRepository itemRepository;

	@InjectMocks
	ItemService itemService;

	/**
	 *
	 */
	@Test
	public void fetchItems() {
		var item1 = new Item("Description", 10, 14.50F, 60F);
		when(itemRepository.findAll()).thenReturn(List.of(item1));

		var result = itemService.fetchItems();

		assertThat(result).isEqualTo(
				List.of(new ItemDto(null, "Description", 10, 14.50F, 60F)));
	}

	/**
	 *
	 */
	@Test
	public void insertItem() {
		var itemDto = new ItemDto("Description", 10, 14.50F, 60F);
		var item = new Item("Description", 10, 14.50F, 60F);
		var itemResult = new Item(1L, "Description", 10, 14.50F, 60F);
		when(itemRepository.save(item)).thenReturn(itemResult);
		var result = itemService.saveItem(itemDto);

		verify(itemRepository, times(1)).save(item);
		assertThat(result).isEqualTo(itemResult.getItemId());
	}

	/**
	 *
	 */
	@Test
	public void updateItem() {
		var itemDto = new ItemDto("Description1", 10, 14.50F, 60F);
		var item1 = new Item(1L, "Description1", 10, 14.50F, 60F);
		when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item1));

		itemService.updateItem(false, 1L, itemDto);

		verify(itemRepository, times(1)).findById(1L);
		verify(itemRepository, times(1)).save(item1);
	}

	/**
	 *
	 */
	@Test
	public void updateItemFailed() {
		var itemDto = new ItemDto("Description1", 10, 14.50F, 60F);
		when(itemRepository.findById(1L)).thenReturn(java.util.Optional.empty());

		itemService.updateItem(false, 1L, itemDto);

		verify(itemRepository, times(1)).findById(1L);
		verify(itemRepository, times(0)).save(null);
	}
}
