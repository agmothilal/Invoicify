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
import static org.mockito.Mockito.when;

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
}
