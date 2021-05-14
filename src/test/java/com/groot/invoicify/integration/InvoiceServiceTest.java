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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;
import java.util.Arrays;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InvoiceServiceTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	public void createInvoiceTest() throws Exception {
		var itemsDto = Arrays.asList(
				new ItemDto("Description", 10, 14.50F, null)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);

		this.mockMvc.perform(post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$").isNumber())
				.andDo(MockMvcRestDocumentation.document("Post-Invoice", PayloadDocumentation.requestFields(
						PayloadDocumentation.fieldWithPath("invoiceNumber").description("Invoice Number."),
						PayloadDocumentation.fieldWithPath("companyName").description("Name of company on invoice."),
						PayloadDocumentation.fieldWithPath("totalCost").description("Total cost of invoice."),
						PayloadDocumentation.fieldWithPath("author").description("Author of invoice."),
						PayloadDocumentation.fieldWithPath("paid").description("If company paid the invoice."),
						PayloadDocumentation.subsectionWithPath("itemsDto[]").description("A list of items in the invoice.")
						)));
	}

	@Test
	public void fetchInvoiceByCompanyNameTest() throws Exception {

		CompanyDto companyObject1 = new CompanyDto("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());


		var itemsDto = Arrays.asList(
				new ItemDto("itemdescription", 10, 14.50F, 60F)
		);
		//var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var invoiceDto2 = new InvoiceDto("Test", "rest", false, itemsDto);

		this.mockMvc.perform(post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$").isNumber())
		;

		this.mockMvc.perform(post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto2)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$").isNumber())
		;

		mockMvc.perform(get("/invoice/Test")
		).andExpect(status().isOk())
				.andExpect(jsonPath("[0].invoiceNumber").value(1L))
				.andExpect(jsonPath("[1].invoiceNumber").value(2L))
				.andDo(print())
				.andDo(document("Get-InvoiceBy-Company-Name"));
		;

	}


	@Test
	public void fetchInvoiceByInvalidCompanyNameTest() throws Exception {

		mockMvc.perform(get("/invoice/Rest")
		).andExpect(status().isNotFound())
				.andDo(print())
				.andDo(document("Get-InvoiceBy-Invalid-Company-Name"))
		;


	}

	@Test
	public void fetchInvoiceByCompanyNameWithNoInvoicesTest() throws Exception {

		CompanyDto companyObject1 = new CompanyDto("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		mockMvc.perform(get("/invoice/Test")
		).andExpect(status().isNotFound())
				.andDo(print())
				.andDo(document("Get-InvoiceBy-valid-Company-Name-With-No-Invoices"))
		;


	}

	@Test
	public void fetchInvoiceByCompanyNameTotalCostTest() throws Exception {

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

		var itemsDto2 = Arrays.asList(
				new ItemDto("itemdescription4", 10, 50F, 60F),
				new ItemDto("itemdescription5", 10, 50F, 30F)


		);
		//var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var invoiceDto2 = new InvoiceDto("Test", "rest", false, itemsDto2);

		this.mockMvc.perform(post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$").isNumber())
		;

		this.mockMvc.perform(post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto2)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$").isNumber())
		;

		mockMvc.perform(get("/invoice/Test")
		).andExpect(status().isOk())
				.andExpect(jsonPath("[0].invoiceNumber").value(1L))
				.andExpect(jsonPath("[1].invoiceNumber").value(2L))
				.andExpect(jsonPath("[0].totalCost").value(525F))
				.andExpect(jsonPath("[1].totalCost").value(1090F))
		;

	}



	@Test
	public void fetchUnPaidInvoiceByCompanyName() throws Exception {

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

		var itemsDto2 = Arrays.asList(
				new ItemDto("itemdescription4", 10, 50F, 60F),
				new ItemDto("itemdescription5", 10, 50F, 30F)


		);
		//var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var invoiceDto2 = new InvoiceDto("Test", "rest", true, itemsDto2);

		this.mockMvc.perform(post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$").isNumber())
		;

		this.mockMvc.perform(post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto2)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$").isNumber())
		;

		mockMvc.perform(get("/invoice/unpaid/Test")
		).andExpect(status().isOk())
				.andExpect(jsonPath("[0].invoiceNumber").value(1L))
				.andExpect(jsonPath("[0].totalCost").value(525F))
		;

	}

	@Test
	public void fetchUnPaidInvoiceByInValidCompanyName() throws Exception {

		mockMvc.perform(get("/invoice/unpaid/Test")
		).andExpect(status().isNotFound())
		;

	}

	@Test
	public void fetchUnPaidInvoiceByCompanyNameWithEmptyData() throws Exception {

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

		var itemsDto2 = Arrays.asList(
				new ItemDto("itemdescription4", 10, 50F, 60F),
				new ItemDto("itemdescription5", 10, 50F, 30F)


		);
		//var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var invoiceDto = new InvoiceDto("Test", "test", true, itemsDto);
		var invoiceDto2 = new InvoiceDto("Test", "rest", true, itemsDto2);

		this.mockMvc.perform(post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$").isNumber())
		;

		this.mockMvc.perform(post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto2)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$").isNumber())
		;

		mockMvc.perform(get("/invoice/unpaid/Test")
		).andExpect(status().isNotFound())

		;

	}

	@Test
	public void fetchInvoiceByIdValidInvoiceNumberTest() throws Exception {

		CompanyDto companyObject1 = new CompanyDto("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());


		var itemsDto = Arrays.asList(
				new ItemDto("itemdescription", 10, 14.50F, 60F)
		);
		//var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);

		this.mockMvc.perform(post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$").isNumber())
		;

		mockMvc.perform(get("/invoice/id/1")
		).andExpect(status().isOk())
				.andExpect(jsonPath("$.invoiceNumber").value(1L))
				.andExpect(jsonPath("$.itemsDto[0].flatPrice").value(60F))
				.andExpect(jsonPath("$.totalCost").value(205F))
				.andExpect(jsonPath("$.companyName").value("Test"))
				.andDo(print())
				.andDo(document("Get-InvoiceBy-valid-InvoiceNumber"))
		;

	}

	@Test
	public void fetchUnPaidInvoiceByInValidInvoiceNumberTest() throws Exception {

		mockMvc.perform(get("/invoice/id/2")
		).andExpect(status().isNotFound())
				.andDo(print())
				.andDo(document("Get-InvoiceBy-Invalid-InvoiceNumber"))
		;

	}


}
