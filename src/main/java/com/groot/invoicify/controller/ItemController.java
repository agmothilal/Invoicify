package com.groot.invoicify.controller;

import com.groot.invoicify.dto.ItemDto;
import com.groot.invoicify.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/item")
public class ItemController {
	@Autowired
	ItemService itemService;

	@PostMapping()
	@ResponseStatus(HttpStatus.CREATED)
	public void createItem(@RequestBody ItemDto itemDto){
		this.itemService.saveItem(itemDto);
	}
}
