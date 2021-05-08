package com.groot.invoicify.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groot.invoicify.dto.ItemDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.function.ServerResponse;

import javax.transaction.Transactional;
import java.util.ArrayList;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class ItemServiceTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	public void createItemTest() throws Exception {
		var itemDto = new ItemDto("description", 1, 1.1f, 1.1f);

		this.mockMvc.perform(MockMvcRequestBuilders.post("/item")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(itemDto)))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andDo(MockMvcRestDocumentation.document("Post-Item", PayloadDocumentation.requestFields(
						PayloadDocumentation.fieldWithPath("description").description("Item description"),
						PayloadDocumentation.fieldWithPath("rateHourBilled").description("Item quantity"),
						PayloadDocumentation.fieldWithPath("ratePrice").description("Item rate price"),
						PayloadDocumentation.fieldWithPath("flatPrice").description("Item flat price")
				)));
	}

	@Test
	public void getItemTest() throws Exception {
		var itemDto = new ItemDto("description", 1, 1.1f, 1.1f);
		var items=new ArrayList<ItemDto>();

		this.mockMvc.perform(MockMvcRequestBuilders.post("/item")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(itemDto)))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		this.mockMvc.perform(MockMvcRequestBuilders.get("/item")
		.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("[0].description").value("description"))
				.andExpect(MockMvcResultMatchers.jsonPath("[0].rateHourBilled").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("[0].ratePrice").value(1.1))
				.andExpect(MockMvcResultMatchers.jsonPath("[0].flatPrice").value(1.1))
				.andDo(MockMvcRestDocumentation.document("Get-Item", PayloadDocumentation.responseFields(
						PayloadDocumentation.fieldWithPath("[]").description("Array of Items"),
						PayloadDocumentation.fieldWithPath("[].description").description("Item description"),
						PayloadDocumentation.fieldWithPath("[].rateHourBilled").description("Item quantity"),
						PayloadDocumentation.fieldWithPath("[].ratePrice").description("Item rate price"),
						PayloadDocumentation.fieldWithPath("[].flatPrice").description("Item flat price")
				)));
	}
}
