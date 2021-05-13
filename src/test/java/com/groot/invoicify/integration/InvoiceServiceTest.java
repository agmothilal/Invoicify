package com.groot.invoicify.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.*;

import javax.transaction.Transactional;
import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class InvoiceServiceTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	private ResultActions createInvoice(InvoiceDto invoiceDto) throws Exception {
		return this.mockMvc.perform(MockMvcRequestBuilders.post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto)))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	public void createInvoiceTest() throws Exception {
		var itemsDto = Arrays.asList(
				new ItemDto("Description", 10, 14.50F, null)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);

		createInvoice(invoiceDto)
				.andExpect(jsonPath("$").isNumber())
				.andDo(MockMvcRestDocumentation.document("Post-Invoice", PayloadDocumentation.requestFields(
						PayloadDocumentation.fieldWithPath("companyName").description("Name of company on invoice."),
						PayloadDocumentation.fieldWithPath("totalCost").description("Total cost of invoice."),
						PayloadDocumentation.fieldWithPath("author").description("Author of invoice."),
						PayloadDocumentation.fieldWithPath("paid").description("If company paid the invoice."),
						PayloadDocumentation.subsectionWithPath("itemsDto[]").description("A list of items in the invoice.")
				)));
	}

	@Test
	public void updateInvoiceTest() throws Exception {
		// Create Invoice
		// Add items within invoice
		var itemsDto = Arrays.asList(
				new ItemDto("Description", 10, 14.50F, 60F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var actionResult = createInvoice(invoiceDto);
		var invoiceId = actionResult.andReturn().getResponse().getContentAsString();
		// Modify invoice fields and items
		invoiceDto.setCompanyName("Test1");
		invoiceDto.setPaid(true);
		itemsDto.get(0).setDescription("Description1");
		itemsDto.get(0).setRatePrice(25.60F);

		// Perform the PUT invoice operation
		// Verify the updated the fields
		this.mockMvc.perform(MockMvcRequestBuilders.put("/invoice")
				.contentType(MediaType.APPLICATION_JSON)
				.param("invoiceId", invoiceId)
				.content(this.objectMapper.writeValueAsString(invoiceDto)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.companyName").value("Test1"))
				.andExpect(jsonPath("$.paid").value(true))
				.andExpect(jsonPath("$.itemsDto.length()").value(1))
				.andExpect(jsonPath("$.itemsDto[0].description").value("Description1"))
				.andExpect(jsonPath("$.itemsDto[0].ratePrice").value(25.60F));
		// TODO: Document the request and response fields
	}
}
