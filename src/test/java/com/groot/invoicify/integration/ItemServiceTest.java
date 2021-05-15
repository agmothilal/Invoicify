package com.groot.invoicify.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groot.invoicify.dto.CompanyDto;
import com.groot.invoicify.dto.InvoiceDto;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;
import java.util.Arrays;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
						fieldWithPath("itemId").description("Item id"),
						fieldWithPath("description").description("Item description"),
						fieldWithPath("rateHourBilled").description("Item quantity"),
						fieldWithPath("ratePrice").description("Item rate price"),
						fieldWithPath("flatPrice").description("Item flat price"),
						fieldWithPath("state").description("Item modified state")
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
						fieldWithPath("[].itemId").description("Item id"),
						fieldWithPath("[].description").description("Item description"),
						fieldWithPath("[].rateHourBilled").description("Item quantity"),
						fieldWithPath("[].ratePrice").description("Item rate price"),
						fieldWithPath("[].flatPrice").description("Item flat price"),
						fieldWithPath("[].state").description("Item modified state")
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
						fieldWithPath("itemId").description("Item id"),
						fieldWithPath("description").description("Item description"),
						fieldWithPath("rateHourBilled").description("Item quantity"),
						fieldWithPath("ratePrice").description("Item rate price"),
						fieldWithPath("flatPrice").description("Item flat price"),
						fieldWithPath("state").description("Item modified state")
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
						fieldWithPath("itemId").description("Item id"),
						fieldWithPath("description").description("Item description"),
						fieldWithPath("rateHourBilled").description("Item quantity"),
						fieldWithPath("ratePrice").description("Item rate price"),
						fieldWithPath("flatPrice").description("Item flat price"),
						fieldWithPath("state").description("Item modified state")
				)));

		this.mockMvc.perform(MockMvcRequestBuilders.get("/item")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("[0].description").value("description1"))
				.andExpect(jsonPath("[0].rateHourBilled").isEmpty())
				.andExpect(jsonPath("[0].ratePrice").value(1.1))
				.andExpect(jsonPath("[0].flatPrice").value(1.1));
	}

	@Test
	public void addItemExistingInvoiceTest() throws Exception {

		CompanyDto companyObject1 = new CompanyDto("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		var itemsDto = Arrays.asList(
				new ItemDto("itemdescription", 10, 14.50F, 60F),
				new ItemDto("itemdescription2", 10, 14.50F, 30F),
				new ItemDto("itemdescription3", 10, 14.50F,0F)

		);

		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);

		this.mockMvc.perform(post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto)))
				.andExpect(status().isCreated());

		var itemsDtoAdditional = Arrays.asList(
				new ItemDto("itemdescription4", 10, 14.50F, 60F),
				new ItemDto("itemdescription5", 10, 14.50F, 30F),
				new ItemDto("itemdescription6", 10, 14.50F,0F)

		);

		mockMvc.perform(post("/invoice/additem/1")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(itemsDtoAdditional)))
				.andExpect(status().isCreated())
				.andExpect(content().string("Items Added to the given invoice number successfully"))
		;

	}

	@Test
	public void addItemInvalidInvoiceTest() throws Exception {

		var itemsDtoAdditional = Arrays.asList(
				new ItemDto("itemdescription4", 10, 14.50F, 60F),
				new ItemDto("itemdescription5", 10, 14.50F, 30F),
				new ItemDto("itemdescription6", 10, 14.50F,0F)

		);

			mockMvc.perform(post("/invoice/additem/0")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(itemsDtoAdditional)))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Invoice id  0 does not exist."))
			;

	}
}
