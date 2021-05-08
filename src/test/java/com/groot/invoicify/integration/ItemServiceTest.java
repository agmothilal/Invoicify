package com.groot.invoicify.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groot.invoicify.dto.ItemDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.*;

import javax.transaction.Transactional;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class ItemServiceTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	private ResultActions createItem(ItemDto itemDto) throws Exception {
		return this.mockMvc.perform(MockMvcRequestBuilders.post("/item")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(itemDto)));
	}

	@Test
	public void createItemTest() throws Exception {
		var itemDto = new ItemDto("description", 1, 1.1f, 1.1f);
		createItem(itemDto).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(jsonPath("$").isNumber())
				.andDo(MockMvcRestDocumentation.document("Post-Item", requestFields(
						fieldWithPath("description").description("Item description"),
						fieldWithPath("rateHourBilled").description("Item quantity"),
						fieldWithPath("ratePrice").description("Item rate price"),
						fieldWithPath("flatPrice").description("Item flat price")
						)));
	}

	@Test
	public void getItemTest() throws Exception {
		var itemDto = new ItemDto("description", 1, 1.1f, 1.1f);
		this.mockMvc.perform(MockMvcRequestBuilders.get("/item")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$").isEmpty());

		createItem(itemDto).andExpect(MockMvcResultMatchers.status().isCreated());

		this.mockMvc.perform(MockMvcRequestBuilders.get("/item")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("length()").value(1))
				.andExpect(jsonPath("[0].description").value("description"))
				.andExpect(jsonPath("[0].rateHourBilled").value(1))
				.andExpect(jsonPath("[0].ratePrice").value(1.1))
				.andExpect(jsonPath("[0].flatPrice").value(1.1))
				.andDo(MockMvcRestDocumentation.document("Get-Item", responseFields(
						fieldWithPath("[]").description("Array of Items"),
						fieldWithPath("[].description").description("Item description"),
						fieldWithPath("[].rateHourBilled").description("Item quantity"),
						fieldWithPath("[].ratePrice").description("Item rate price"),
						fieldWithPath("[].flatPrice").description("Item flat price")
				)));
	}

	@Test
	public void patchItemTest() throws Exception {
		var itemDto = new ItemDto("description", 1, 1.1f, 1.1f);

		var result = createItem(itemDto);
		result.andExpect(MockMvcResultMatchers.status().isCreated());
		var itemId = result.andReturn().getResponse().getContentAsString();

		itemDto.setDescription("description1");
		itemDto.setRateHourBilled(null);

		this.mockMvc.perform(MockMvcRequestBuilders.patch("/item")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("itemId", itemId)
				.content(this.objectMapper.writeValueAsString(itemDto)))
				.andExpect(MockMvcResultMatchers.status().isAccepted())
				.andDo(MockMvcRestDocumentation.document("Patch-Item", requestFields(
						fieldWithPath("description").description("Item description"),
						fieldWithPath("rateHourBilled").description("Item quantity"),
						fieldWithPath("ratePrice").description("Item rate price"),
						fieldWithPath("flatPrice").description("Item flat price")
				)));

		this.mockMvc.perform(MockMvcRequestBuilders.get("/item")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("[0].description").value("description1"))
				.andExpect(jsonPath("[0].rateHourBilled").value(1))
				.andExpect(jsonPath("[0].ratePrice").value(1.1))
				.andExpect(jsonPath("[0].flatPrice").value(1.1));

	}

	@Test
	public void putItemTest() throws Exception {
		var itemDto = new ItemDto("description", 1, 1.1f, 1.1f);

		var result = createItem(itemDto);
		result.andExpect(MockMvcResultMatchers.status().isCreated());
		var itemId = result.andReturn().getResponse().getContentAsString();

		itemDto.setDescription("description1");
		itemDto.setRateHourBilled(null);

		this.mockMvc.perform(MockMvcRequestBuilders.put("/item")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("itemId", itemId)
				.content(this.objectMapper.writeValueAsString(itemDto)))
				.andExpect(MockMvcResultMatchers.status().isAccepted())
				.andDo(MockMvcRestDocumentation.document("Put-Item", requestFields(
						fieldWithPath("description").description("Item description"),
						fieldWithPath("rateHourBilled").description("Item quantity"),
						fieldWithPath("ratePrice").description("Item rate price"),
						fieldWithPath("flatPrice").description("Item flat price")
				)));

		this.mockMvc.perform(MockMvcRequestBuilders.get("/item")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("[0].description").value("description1"))
				.andExpect(jsonPath("[0].rateHourBilled").isEmpty())
				.andExpect(jsonPath("[0].ratePrice").value(1.1))
				.andExpect(jsonPath("[0].flatPrice").value(1.1));
	}
}
