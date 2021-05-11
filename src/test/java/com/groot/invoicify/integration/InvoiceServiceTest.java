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
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;
import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class InvoiceServiceTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	public void createInvoiceTest() throws Exception {
		var itemsDto = Arrays.asList(
				new ItemDto("Description", 10, 14.50F, 60F)
		);
		var companyDto = new CompanyDto("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);

//		this.mockMvc.perform(MockMvcRequestBuilders.post("/item")
//				.contentType(MediaType.APPLICATION_JSON_VALUE)
//				.content(this.objectMapper.writeValueAsString(itemDto)))
		this.mockMvc.perform(MockMvcRequestBuilders.post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto)))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$").isNumber())
//				.andDo(MockMvcRestDocumentation.document("Post-Item", PayloadDocumentation.requestFields(
//						PayloadDocumentation.fieldWithPath("companyName").description("Name of company on invoice."),
//						PayloadDocumentation.fieldWithPath("author").description("Author of invoice."),
//						PayloadDocumentation.fieldWithPath("paid").description("If company paid the invoice."))));
		;
	}
}
