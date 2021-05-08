package com.groot.invoicify.unitTests;

import com.groot.invoicify.dto.ItemDto;
import com.groot.invoicify.entity.Item;
import com.groot.invoicify.repository.ItemRepository;
import com.groot.invoicify.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemService itemService;

    @Test
    public void fetchItems() {
        var item1 = new Item("Description", 10, 14.50F, 60F);
        when(itemRepository.findAll()).thenReturn(List.of(item1));

        var result = itemService.fetchItems();

        assertThat(result).isEqualTo(
                List.of(new ItemDto("Description", 10, 14.50F, 60F)));
    }

    @Test
    public void insertItem() {
        var itemDto = new ItemDto("Description", 10, 14.50F, 60F);
        var item = new Item("Description", 10, 14.50F, 60F);
        itemService.saveItem(itemDto);

        verify(itemRepository, times(1)).save(item);
    }

    @Test
    public void  updateItem() {
        var itemDto = new ItemDto("Description1", 10, 14.50F, 60F);
        var item1 = new Item(1L,"Description1", 10, 14.50F, 60F);
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item1));

        itemService.updateItem(1L, itemDto);

        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(item1);
    }
}
