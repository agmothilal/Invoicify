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
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.function.ServerResponse;

import javax.transaction.Transactional;

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
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(itemDto)))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andDo(MockMvcRestDocumentation.document("Post-Item", PayloadDocumentation.requestFields(
						PayloadDocumentation.fieldWithPath("description").description("Item description"),
						PayloadDocumentation.fieldWithPath("rateHourBilled").description("Item quantity"),
						PayloadDocumentation.fieldWithPath("ratePrice").description("Item rate price"),
						PayloadDocumentation.fieldWithPath("flatPrice").description("Item flat price")
				)));
	}
}
