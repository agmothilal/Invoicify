package com.groot.invoicify.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groot.invoicify.dto.DtoState;
import com.groot.invoicify.dto.InvoiceDto;
import com.groot.invoicify.dto.CompanyDto;
import com.groot.invoicify.dto.ItemDto;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.*;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * InvoiceServiceTest
 *
 */
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

	/**
	 *
	 * @param companyName
	 * @throws Exception
	 */
	private void initCompanyEntities(List<String> companyName) throws Exception {
		for (String name : companyName) {
			var company = new CompanyDto(name, "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");
			mockMvc.perform(post("/company")
					.content(objectMapper.writeValueAsString(company))
					.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isCreated());
		}
	}

	/**
	 *
	 * @param invoiceDto
	 * @return
	 * @throws Exception
	 */
	private ResultActions createInvoice(InvoiceDto invoiceDto) throws Exception {
		return this.mockMvc.perform(MockMvcRequestBuilders.post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto)))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void createInvoiceTest() throws Exception {
		initCompanyEntities(Arrays.asList("Test"));
		var itemsDto = Arrays.asList(
				new ItemDto("Description", 10, 14.50F, 20.50F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);

		createInvoice(invoiceDto)
				.andExpect(jsonPath("$.companyName").value("Test"))
				.andExpect(jsonPath("$.author").value("test"))
				.andExpect(jsonPath("$.paid").value(false))
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].description").value("Description"))
				.andExpect(jsonPath("$.items[0].ratePrice").value(14.50F))
				.andExpect(jsonPath("$.items[0].rateHourBilled").value(10))
				.andExpect(jsonPath("$.items[0].flatPrice").value(20.50F))
				.andDo(document("Post-Invoice", requestFields(
						fieldWithPath("invoiceNumber").description("Name of company on invoice."),
						fieldWithPath("companyName").description("Name of company on invoice."),
						fieldWithPath("totalCost").description("Total cost of invoice."),
						fieldWithPath("author").description("Author of invoice."),
						fieldWithPath("paid").description("If company paid the invoice."),
						subsectionWithPath("items[]").description("A list of items in the invoice.")
				), responseFields(
						fieldWithPath("invoiceNumber").description("Invoice number."),
						fieldWithPath("companyName").description("Name of company on invoice."),
						fieldWithPath("totalCost").description("Total cost of invoice."),
						fieldWithPath("author").description("Author of invoice."),
						fieldWithPath("paid").description("If company paid the invoice."),
						subsectionWithPath("items[]").description("A list of items in the invoice."),
						subsectionWithPath("items[].itemId").description("Invoice line item id."),
						subsectionWithPath("items[].description").description("Invoice line item description."),
						subsectionWithPath("items[].rateHourBilled").description("Invoice line item quantity."),
						subsectionWithPath("items[].ratePrice").description("Invoice line item hourly price."),
						subsectionWithPath("items[].flatPrice").description("Invoice line item flat price.")
				)));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void updateInvoiceTest() throws Exception {
		// Create company before insert invoice
		initCompanyEntities(Arrays.asList("Test", "Test1"));

		// Create Invoice
		// Add items within invoice
		var itemsDto = Arrays.asList(
				new ItemDto("Description", 10, 14.50F, 60F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var actionResult = createInvoice(invoiceDto);
		var invoiceJson = actionResult.andReturn().getResponse().getContentAsString();
		var dbInvoice = objectMapper.readValue(invoiceJson, InvoiceDto.class);
		// Modify invoice fields and items
		dbInvoice.setCompanyName("Test1");
		dbInvoice.setPaid(true);
		dbInvoice.getItemsDto().get(0).setDescription("Description1");
		dbInvoice.getItemsDto().get(0).setRatePrice(25.60F);
		dbInvoice.getItemsDto().get(0).setState(DtoState.Modified);

		// Perform the PUT invoice operation
		// Verify the updated the fields
		// Document the request and response fields
		this.mockMvc.perform(MockMvcRequestBuilders.put("/invoice")
				.contentType(MediaType.APPLICATION_JSON)
				.param("invoiceId", dbInvoice.getInvoiceNumber().toString())
				.content(this.objectMapper.writeValueAsString(dbInvoice)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.companyName").value("Test1"))
				.andExpect(jsonPath("$.paid").value(true))
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].description").value("Description1"))
				.andExpect(jsonPath("$.items[0].ratePrice").value(25.60F))
				.andDo(document("Put-Invoice", requestFields(
						fieldWithPath("invoiceNumber").description("Invoice number."),
						fieldWithPath("companyName").description("Name of company on invoice."),
						fieldWithPath("totalCost").description("Total cost of invoice."),
						fieldWithPath("author").description("Author of invoice."),
						fieldWithPath("paid").description("If company paid the invoice."),
						subsectionWithPath("items[]").description("A list of items in the invoice."),
						subsectionWithPath("items[].itemId").description("Invoice line item id."),
						subsectionWithPath("items[].description").description("Invoice line item description."),
						subsectionWithPath("items[].rateHourBilled").description("Invoice line item quantity."),
						subsectionWithPath("items[].ratePrice").description("Invoice line item hourly price."),
						subsectionWithPath("items[].flatPrice").description("Invoice line item flat price.")
				), responseFields(
						fieldWithPath("invoiceNumber").description("Invoice number."),
						fieldWithPath("companyName").description("Name of company on invoice."),
						fieldWithPath("totalCost").description("Total cost of invoice."),
						fieldWithPath("author").description("Author of invoice."),
						fieldWithPath("paid").description("If company paid the invoice."),
						subsectionWithPath("items[]").description("A list of items in the invoice."),
						subsectionWithPath("items[].itemId").description("Invoice line item id."),
						subsectionWithPath("items[].description").description("Invoice line item description."),
						subsectionWithPath("items[].rateHourBilled").description("Invoice line item quantity."),
						subsectionWithPath("items[].ratePrice").description("Invoice line item hourly price."),
						subsectionWithPath("items[].flatPrice").description("Invoice line item flat price.")
				)));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("Update invoice failing due to the company is not exist!")
	public void updateInvoiceFailedWhenCompanyNotExistTest() throws Exception {
		// Create company before insert invoice
		initCompanyEntities(Arrays.asList("Test"));

		// Create Invoice
		// Add items within invoice
		var itemsDto = Arrays.asList(
				new ItemDto("Description", 10, 14.50F, 60F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var actionResult = createInvoice(invoiceDto);
		var invoiceJson = actionResult.andReturn().getResponse().getContentAsString();
		var dbInvoice = objectMapper.readValue(invoiceJson, InvoiceDto.class);
		// Modify invoice fields and items
		dbInvoice.setCompanyName("Test1");

		// Perform the PUT invoice operation
		// Verify the updated the fields
		// Document the request and response fields
		this.mockMvc.perform(MockMvcRequestBuilders.put("/invoice")
				.contentType(MediaType.APPLICATION_JSON)
				.param("invoiceId", dbInvoice.getInvoiceNumber().toString())
				.content(this.objectMapper.writeValueAsString(dbInvoice)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$").value("The given Company or Invoice is not exist!"))
				.andDo(document("Put-Invoice-Company-NotFound", requestFields(
						fieldWithPath("invoiceNumber").description("Invoice number."),
						fieldWithPath("companyName").description("Name of company on invoice."),
						fieldWithPath("totalCost").description("Total cost of invoice."),
						fieldWithPath("author").description("Author of invoice."),
						fieldWithPath("paid").description("If company paid the invoice."),
						subsectionWithPath("items[]").description("A list of items in the invoice."),
						subsectionWithPath("items[].itemId").description("Invoice line item id."),
						subsectionWithPath("items[].description").description("Invoice line item description."),
						subsectionWithPath("items[].rateHourBilled").description("Invoice line item quantity."),
						subsectionWithPath("items[].ratePrice").description("Invoice line item hourly price."),
						subsectionWithPath("items[].flatPrice").description("Invoice line item flat price.")
				)));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("Update invoice failing due to the give invoice is not exist!")
	public void updateInvoiceFailedWhenInvoiceNotExistTest() throws Exception {
		// Create Invoice
		// Add items within invoice
		var itemsDto = Arrays.asList(
				new ItemDto("Description", 10, 14.50F, 60F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		// Modify invoice fields and items
		invoiceDto.setAuthor("modified author");

		// Perform the PUT invoice operation
		// Verify the updated the fields
		// Document the request and response fields
		this.mockMvc.perform(MockMvcRequestBuilders.put("/invoice")
				.contentType(MediaType.APPLICATION_JSON)
				.param("invoiceId", "123")
				.content(this.objectMapper.writeValueAsString(invoiceDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$").value("The given Company or Invoice is not exist!"))
				.andDo(document("Put-Invoice-Invoice-NotFound", requestFields(
						fieldWithPath("invoiceNumber").description("Invoice number."),
						fieldWithPath("companyName").description("Name of company on invoice."),
						fieldWithPath("totalCost").description("Total cost of invoice."),
						fieldWithPath("author").description("Author of invoice."),
						fieldWithPath("paid").description("If company paid the invoice."),
						subsectionWithPath("items[]").description("A list of items in the invoice."),
						subsectionWithPath("items[].itemId").description("Invoice line item id."),
						subsectionWithPath("items[].description").description("Invoice line item description."),
						subsectionWithPath("items[].rateHourBilled").description("Invoice line item quantity."),
						subsectionWithPath("items[].ratePrice").description("Invoice line item hourly price."),
						subsectionWithPath("items[].flatPrice").description("Invoice line item flat price.")
				)));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("Updating invoice with new line item")
	public void updateInvoiceWithNewLineItemTest() throws Exception {
		// Create company before insert invoice
		initCompanyEntities(Arrays.asList("Test"));

		// Create Invoice
		// Add items within invoice
		var itemsDto = Arrays.asList(
				new ItemDto("Description", 10, 14.50F, 60F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var actionResult = createInvoice(invoiceDto);
		var invoiceJson = actionResult.andReturn().getResponse().getContentAsString();
		var dbInvoice = objectMapper.readValue(invoiceJson, InvoiceDto.class);
		// Modify invoice with adding new line item
		var dbItems = dbInvoice.getItemsDto();
		dbItems.add(new ItemDto("Second item", 12, 14.50F, 70F));
		dbInvoice.setItemsDto(dbItems);

		// Perform the PUT invoice operation
		// Verify the updated the fields
		// Document the request and response fields
		this.mockMvc.perform(MockMvcRequestBuilders.put("/invoice")
				.contentType(MediaType.APPLICATION_JSON)
				.param("invoiceId", dbInvoice.getInvoiceNumber().toString())
				.content(this.objectMapper.writeValueAsString(dbInvoice)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.companyName").value("Test"))
				.andExpect(jsonPath("$.paid").value(false))
				.andExpect(jsonPath("$.items.length()").value(2))
				.andExpect(jsonPath("$.items[0].description").value("Description"))
				.andExpect(jsonPath("$.items[0].rateHourBilled").value(10))
				.andExpect(jsonPath("$.items[0].ratePrice").value(14.50F))
				.andExpect(jsonPath("$.items[0].flatPrice").value(60F))
				.andExpect(jsonPath("$.items[1].description").value("Second item"))
				.andExpect(jsonPath("$.items[1].rateHourBilled").value(12))
				.andExpect(jsonPath("$.items[1].ratePrice").value(14.50F))
				.andExpect(jsonPath("$.items[1].flatPrice").value(70F))
				.andDo(document("Put-Invoice-New-LineItem", requestFields(
						fieldWithPath("invoiceNumber").description("Invoice number."),
						fieldWithPath("companyName").description("Name of company on invoice."),
						fieldWithPath("totalCost").description("Total cost of invoice."),
						fieldWithPath("author").description("Author of invoice."),
						fieldWithPath("paid").description("If company paid the invoice."),
						subsectionWithPath("items[]").description("A list of items in the invoice.")
				), responseFields(
						fieldWithPath("invoiceNumber").description("Invoice number."),
						fieldWithPath("companyName").description("Name of company on invoice."),
						fieldWithPath("totalCost").description("Total cost of invoice."),
						fieldWithPath("author").description("Author of invoice."),
						fieldWithPath("paid").description("If company paid the invoice."),
						subsectionWithPath("items[]").description("A list of items in the invoice.")
				)));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("Updating invoice with removing existing line item")
	public void updateInvoiceWithRemoveLineItemTest() throws Exception {
		// Create company before insert invoice
		initCompanyEntities(Arrays.asList("Test"));

		// Create Invoice
		// Add items within invoice
		var itemsDto = Arrays.asList(
				new ItemDto("Description1", 10, 14.50F, 60F),
				new ItemDto("Description2", 10, 14.50F, 60F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var actionResult = createInvoice(invoiceDto);
		var invoiceJson = actionResult.andReturn().getResponse().getContentAsString();
		var dbInvoice = objectMapper.readValue(invoiceJson, InvoiceDto.class);
		// Modify invoice with adding new line item
		var dbItems = dbInvoice.getItemsDto();
		dbItems.get(0).setState(DtoState.Deleted);
		dbInvoice.setItemsDto(dbItems);

		// Perform the PUT invoice operation
		// Verify the updated the fields
		// Document the request and response fields
		this.mockMvc.perform(MockMvcRequestBuilders.put("/invoice")
				.contentType(MediaType.APPLICATION_JSON)
				.param("invoiceId", dbInvoice.getInvoiceNumber().toString())
				.content(this.objectMapper.writeValueAsString(dbInvoice)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.companyName").value("Test"))
				.andExpect(jsonPath("$.paid").value(false))
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].description").value("Description2"))
				.andExpect(jsonPath("$.items[0].rateHourBilled").value(10))
				.andExpect(jsonPath("$.items[0].ratePrice").value(14.50F))
				.andExpect(jsonPath("$.items[0].flatPrice").value(60F))
				.andDo(document("Put-Invoice-Delete-LineItem", requestFields(
						fieldWithPath("invoiceNumber").description("Invoice number."),
						fieldWithPath("companyName").description("Name of company on invoice."),
						fieldWithPath("totalCost").description("Total cost of invoice."),
						fieldWithPath("author").description("Author of invoice."),
						fieldWithPath("paid").description("If company paid the invoice."),
						subsectionWithPath("items[]").description("A list of items in the invoice.")
				), responseFields(
						fieldWithPath("invoiceNumber").description("Invoice number."),
						fieldWithPath("companyName").description("Name of company on invoice."),
						fieldWithPath("totalCost").description("Total cost of invoice."),
						fieldWithPath("author").description("Author of invoice."),
						fieldWithPath("paid").description("If company paid the invoice."),
						subsectionWithPath("items[]").description("A list of items in the invoice.")
				)));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void fetchInvoiceByCompanyNameTest() throws Exception {

		CompanyDto companyObject1 = new CompanyDto("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(MockMvcRequestBuilders.post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		var itemsDto = Arrays.asList(
				new ItemDto("itemdescription", 10, 14.50F, 60F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var invoiceDto2 = new InvoiceDto("Test", "rest", false, itemsDto);

		var actionResult = createInvoice(invoiceDto);
		var invoiceJson = actionResult.andReturn().getResponse().getContentAsString();
		var dbInvoice = objectMapper.readValue(invoiceJson, InvoiceDto.class);
		var invoiceId = dbInvoice.getInvoiceNumber();

		var actionResult2 = createInvoice(invoiceDto2);
		var invoiceJson2 = actionResult2.andReturn().getResponse().getContentAsString();
		var dbInvoice2 = objectMapper.readValue(invoiceJson2, InvoiceDto.class);
		var invoiceId2 = dbInvoice2.getInvoiceNumber();

		mockMvc.perform(get("/invoice/Test")
		).andExpect(status().isOk())
				.andExpect(jsonPath("[0].invoiceNumber").value(invoiceId2))
				.andExpect(jsonPath("[1].invoiceNumber").value(invoiceId))
				.andDo(print())
				.andDo(document("Get-InvoiceBy-Company-Name"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void fetchInvoiceByInvalidCompanyNameTest() throws Exception {
		mockMvc.perform(get("/invoice/Rest")
		).andExpect(status().isNotFound())
				.andDo(print())
				.andDo(document("Get-InvoiceBy-Invalid-Company-Name"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void fetchInvoiceByCompanyNameWithNoInvoicesTest() throws Exception {
		CompanyDto companyObject1 = new CompanyDto("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(MockMvcRequestBuilders.post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		mockMvc.perform(get("/invoice/Test")
		).andExpect(status().isNotFound())
				.andDo(print())
				.andDo(document("Get-InvoiceBy-valid-Company-Name-With-No-Invoices"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void fetchInvoiceByCompanyNameTotalCostTest() throws Exception {
		CompanyDto companyObject1 = new CompanyDto("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(MockMvcRequestBuilders.post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		var itemsDto = Arrays.asList(
				new ItemDto("itemdescription", 10, 14.50F, 60F),
				new ItemDto("itemdescription2", 10, 14.50F, 30F),
				new ItemDto("itemdescription3", 10, 14.50F, 0F)
		);

		var itemsDto2 = Arrays.asList(
				new ItemDto("itemdescription4", 10, 50F, 60F),
				new ItemDto("itemdescription5", 10, 50F, 30F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var invoiceDto2 = new InvoiceDto("Test", "rest", false, itemsDto2);
		var actionResult = createInvoice(invoiceDto);
		var invoiceJson = actionResult.andReturn().getResponse().getContentAsString();
		var dbInvoice = objectMapper.readValue(invoiceJson, InvoiceDto.class);

		var actionResult2 = createInvoice(invoiceDto2);
		var invoiceJson2 = actionResult2.andReturn().getResponse().getContentAsString();
		var dbInvoice2 = objectMapper.readValue(invoiceJson2, InvoiceDto.class);

		mockMvc.perform(get("/invoice/Test")
		).andExpect(status().isOk())
				.andExpect(jsonPath("[0].invoiceNumber").value(2L))
				.andExpect(jsonPath("[1].invoiceNumber").value(1L))
				.andExpect(jsonPath("[0].totalCost").value(1090F))
				.andExpect(jsonPath("[1].totalCost").value(525F));
	}

	/**
	 *
	 * @throws Exception
	 */
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
				new ItemDto("itemdescription3", 10, 14.50F, 0F)
		);

		var itemsDto2 = Arrays.asList(
				new ItemDto("itemdescription4", 10, 50F, 60F),
				new ItemDto("itemdescription5", 10, 50F, 30F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var invoiceDto2 = new InvoiceDto("Test", "rest", true, itemsDto2);
		var actionResult = createInvoice(invoiceDto);
		var invoiceJson = actionResult.andReturn().getResponse().getContentAsString();
		var dbInvoice = objectMapper.readValue(invoiceJson, InvoiceDto.class);
		var invoiceId = dbInvoice.getInvoiceNumber();

		var actionResult2 = createInvoice(invoiceDto2);
		var invoiceJson2 = actionResult2.andReturn().getResponse().getContentAsString();
		var dbInvoice2 = objectMapper.readValue(invoiceJson2, InvoiceDto.class);

		mockMvc.perform(get("/invoice/unpaid/Test")
		).andExpect(status().isOk())
				.andExpect(jsonPath("[0].invoiceNumber").value(invoiceId))
				.andExpect(jsonPath("[0].totalCost").value(dbInvoice.getTotalCost()));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void fetchUnPaidInvoiceByInValidCompanyName() throws Exception {
		mockMvc.perform(get("/invoice/unpaid/Test")
		).andExpect(status().isNotFound());

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void fetchUnPaidInvoiceByCompanyNameWithEmptyData() throws Exception {
		CompanyDto companyObject1 = new CompanyDto("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(MockMvcRequestBuilders.post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		var itemsDto = Arrays.asList(
				new ItemDto("itemdescription", 10, 14.50F, 60F),
				new ItemDto("itemdescription2", 10, 14.50F, 30F),
				new ItemDto("itemdescription3", 10, 14.50F, 0F)
		);

		var itemsDto2 = Arrays.asList(
				new ItemDto("itemdescription4", 10, 50F, 60F),
				new ItemDto("itemdescription5", 10, 50F, 30F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", true, itemsDto);
		var invoiceDto2 = new InvoiceDto("Test", "rest", true, itemsDto2);
		createInvoice(invoiceDto);
		createInvoice(invoiceDto2);

		mockMvc.perform(get("/invoice/unpaid/Test")
		).andExpect(status().isNotFound())
				.andDo(document("Get-Company-Unpaid-Invoice-ButWithAllInvoicesPaid"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void fetchInvoiceByIdValidInvoiceNumberTest() throws Exception {
		CompanyDto companyObject1 = new CompanyDto("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(MockMvcRequestBuilders.post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		var itemsDto = Arrays.asList(
				new ItemDto("itemdescription", 10, 14.50F, 60F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var actionResult = createInvoice(invoiceDto);
		var invoiceJson = actionResult.andReturn().getResponse().getContentAsString();
		var dbInvoice = objectMapper.readValue(invoiceJson, InvoiceDto.class);
		var invoiceId = dbInvoice.getInvoiceNumber();

		mockMvc.perform(get("/invoice/id/" + invoiceId)
		).andExpect(status().isOk())
				.andExpect(jsonPath("$.invoiceNumber").value(invoiceId))
				.andExpect(jsonPath("$.items[0].flatPrice").value(60F))
				.andExpect(jsonPath("$.totalCost").value(205F))
				.andExpect(jsonPath("$.companyName").value("Test"))
				.andDo(print())
				.andDo(document("Get-InvoiceBy-valid-InvoiceNumber"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void fetchUnPaidInvoiceByInValidInvoiceNumberTest() throws Exception {
		mockMvc.perform(get("/invoice/id/2")
		).andExpect(status().isNotFound())
				.andDo(print())
				.andDo(document("Get-InvoiceBy-Invalid-InvoiceNumber"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void getPagingForAllInvoicesByCompanyTest() throws Exception {
		CompanyDto companyObject1 = new CompanyDto("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		var itemsDto = Arrays.asList(
				new ItemDto("itemdescription", 10, 14.50F, 60F),
				new ItemDto("itemdescription", 10, 14.50F, 60F)
		);

		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);

		for (int x = 0; x < 25; x++) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				System.out.println(e);
			}
			this.mockMvc.perform(post("/invoice")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(this.objectMapper.writeValueAsString(invoiceDto)))
					.andExpect(status().isCreated());
		}

		this.mockMvc.perform(get("/invoice/Test")
				.param("pageNo", "0"))
				.andExpect(jsonPath("length()").value(10))
				.andDo(print())
				.andDo(document("Get-InvoiceBy-Company-Name-Paging"));

		this.mockMvc.perform(get("/invoice/Test")
				.param("pageNo", "2"))
				.andExpect(jsonPath("length()").value(5))
				.andDo(print())
				.andDo(document("Get-InvoiceBy-Company-Name-Paging-Last"));

		this.mockMvc.perform(get("/invoice/Test")
				.param("pageNo", "3"))
				.andExpect(status().isNotFound())
				.andDo(print())
				.andDo(document("Get-InvoiceBy-Company-Name-Paging-Invalid"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("Updating invoice failed when invoice already paid")
	public void updateInvoiceFailedWhenItWasPaidTest() throws Exception {
		// Create company before insert invoice
		initCompanyEntities(Arrays.asList("Test"));

		// Create Invoice
		// Add items within invoice
		var itemsDto = Arrays.asList(
				new ItemDto("Description1", 10, 14.50F, 60F),
				new ItemDto("Description2", 10, 14.50F, 60F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", true, itemsDto);
		var actionResult = createInvoice(invoiceDto);
		var invoiceJson = actionResult.andReturn().getResponse().getContentAsString();
		var dbInvoice = objectMapper.readValue(invoiceJson, InvoiceDto.class);
		// Modify invoice author and line item
		dbInvoice.setAuthor("test author");
		dbInvoice.getItemsDto().get(0).setFlatPrice(80F);

		// Perform the PUT invoice operation
		// Verify the updated the fields
		// Document the request and response fields
		this.mockMvc.perform(MockMvcRequestBuilders.put("/invoice")
				.contentType(MediaType.APPLICATION_JSON)
				.param("invoiceId", dbInvoice.getInvoiceNumber().toString())
				.content(this.objectMapper.writeValueAsString(dbInvoice)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$").value("The invoice can't update since it was already paid status!"))
				.andDo(document("Put-Invoice-Already-Paid", requestFields(
						fieldWithPath("invoiceNumber").description("Invoice number."),
						fieldWithPath("companyName").description("Name of company on invoice."),
						fieldWithPath("totalCost").description("Total cost of invoice."),
						fieldWithPath("author").description("Author of invoice."),
						fieldWithPath("paid").description("If company paid the invoice."),
						subsectionWithPath("items[]").description("A list of items in the invoice."),
						subsectionWithPath("items[].itemId").description("Invoice line item id."),
						subsectionWithPath("items[].description").description("Invoice line item description."),
						subsectionWithPath("items[].rateHourBilled").description("Invoice line item quantity."),
						subsectionWithPath("items[].ratePrice").description("Invoice line item hourly price."),
						subsectionWithPath("items[].flatPrice").description("Invoice line item flat price.")
				)));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Sql("/insert_invoices.sql")
	public void deletePaidAndOlderInvoicesTest() throws Exception {
		this.mockMvc.perform(delete("/invoice/deletepaidandolderinvoices"))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("length()").value(1))
				.andExpect(jsonPath("[0].invoiceNumber").value(1))
				.andDo(document("Delete-Paid-Older-Invoices", responseFields(
						subsectionWithPath("[]").description("A list of invoices being deleted."),
						subsectionWithPath("[].invoiceNumber").description("Invoice id."),
						subsectionWithPath("[].companyName").description("Invoice company name should be null."),
						subsectionWithPath("[].totalCost").description("Invoice total cost should be null."),
						subsectionWithPath("[].author").description("Invoice author should be null."),
						subsectionWithPath("[].paid").description("Invoice paid should be null."),
						subsectionWithPath("[].items").description("Invoice items should be null.")
				)));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void getPagingForAllUnPaidInvoicesByCompanyTest() throws Exception {
		CompanyDto companyObject1 = new CompanyDto("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		var itemsDto = Arrays.asList(
				new ItemDto("itemdescription", 10, 14.50F, 60F),
				new ItemDto("itemdescription", 10, 14.50F, 60F)
		);

		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		var invoiceDto2 = new InvoiceDto("Test", "test", true, itemsDto);

		for (int x = 0; x < 25; x++) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				System.out.println(e);
			}
			if (x % 2 == 0) {
				this.mockMvc.perform(post("/invoice")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(this.objectMapper.writeValueAsString(invoiceDto)))
						.andExpect(status().isCreated());
			} else {
				this.mockMvc.perform(post("/invoice")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(this.objectMapper.writeValueAsString(invoiceDto2)))
						.andExpect(status().isCreated());
			}
		}

		this.mockMvc.perform(get("/invoice/unpaid/Test")
				.param("pageNo", "0"))
				.andExpect(jsonPath("length()").value(10))
				.andDo(print())
				.andDo(document("Get-Unpaid-InvoiceBy-Company-Name-Paging"));

		this.mockMvc.perform(get("/invoice/unpaid/Test")
				.param("pageNo", "1"))
				.andExpect(jsonPath("length()").value(3))
				.andDo(print())
				.andDo(document("Get-Unpaid-InvoiceBy-Company-Name-Paging-Last"));

		this.mockMvc.perform(get("/invoice/unpaid/Test")
				.param("pageNo", "2"))
				.andExpect(status().isNotFound())
				.andDo(print())
				.andDo(document("Get-Unpaid-InvoiceBy-Company-Name-Paging-Invalid"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void fetchAllInvoicesTest() throws Exception {
		CompanyDto companyObject1 = new CompanyDto("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");
		mockMvc.perform(MockMvcRequestBuilders.post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		var itemsDto = Arrays.asList(
				new ItemDto("itemdescription", 10, 14.50F, 60F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		for (int x = 0; x < 15; x++) {
			this.mockMvc.perform(post("/invoice")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(this.objectMapper.writeValueAsString(invoiceDto)))
					.andExpect(status().isCreated());
		}

		mockMvc.perform(get("/invoice")
				.param("pageNo", "0")
		).andExpect(status().isOk())
				.andExpect(jsonPath("length()").value(10))
				.andDo(document("Get-All-Invoices"));

		mockMvc.perform(get("/invoice")
				.param("pageNo", "1")
		).andExpect(status().isOk())
				.andExpect(jsonPath("length()").value(5));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void fetchAllInvoicesInValidPageTest() throws Exception {
		CompanyDto companyObject1 = new CompanyDto("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(MockMvcRequestBuilders.post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		var itemsDto = Arrays.asList(
				new ItemDto("itemdescription", 10, 14.50F, 60F)
		);
		var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
		this.mockMvc.perform(post("/invoice")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(this.objectMapper.writeValueAsString(invoiceDto)))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/invoice")
				.param("pageNo", "1")
		).andExpect(status().isNotFound())
				.andExpect(content().string("No invoice found for the given page."))
				.andDo(document("Get-All-Invoices-Invalid-Page"));
	}
}
