package com.groot.invoicify.controller;

import com.groot.invoicify.dto.ItemDto;
import com.groot.invoicify.service.ItemService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * ItemController
 *
 */
@RestController
@RequestMapping("/item")
public class ItemController {

  @Autowired
  ItemService itemService;

  /**
   *
   * @param itemDto
   * @return
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Long createItem(@RequestBody ItemDto itemDto) {
    return this.itemService.saveItem(itemDto);
  }

  /**
   *
   * @return
   */
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<ItemDto> getItem() {
    return this.itemService.fetchItems();
  }

  /**
   *
   * @param itemId
   * @param itemDto
   */
  @PatchMapping
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void patchItem(@RequestParam Long itemId, @RequestBody ItemDto itemDto) {
    this.itemService.updateItem(false, itemId, itemDto);
  }

  /**
   *
   * @param itemId
   * @param itemDto
   */
  @PutMapping
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void putItem(@RequestParam Long itemId, @RequestBody ItemDto itemDto) {
    this.itemService.updateItem(true, itemId, itemDto);
  }
}
