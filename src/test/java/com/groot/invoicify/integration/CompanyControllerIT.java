package com.groot.invoicify.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groot.invoicify.dto.CompanyDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CompanyControllerIT
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CompanyControllerIT {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	/**
	 *
	 * @throws Exception
	 */
	@Test
	void initialEmptyGetTest() throws Exception {
		mockMvc.perform(get("/company"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("length()").value(0));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	void initialPostTest() throws Exception {
		CompanyDto companyObject1 = new CompanyDto("CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated())
				.andExpect(content().string("Successfully added new Company.Company id is 1."));

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	void dtoPostAndGetTest() throws Exception {
		CompanyDto companyObject1 = new CompanyDto("CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated())
				.andDo(document("Post-Company", requestFields(
						fieldWithPath("companyId").description("Company Id"),
						fieldWithPath("name").description("Company name"),
						fieldWithPath("address").description("Company address"),
						fieldWithPath("city").description("Company city"),
						fieldWithPath("state").description("Company state"),
						fieldWithPath("zip").description("Company zip"),
						fieldWithPath("contactName").description("Company contact name"),
						fieldWithPath("contactTitle").description("Company contact title"),
						fieldWithPath("contactPhoneNumber").description("Company phone number"))));

		mockMvc.perform(get("/company")
		).andExpect(status().isOk())
				.andExpect(jsonPath("length()").value(1))
				.andExpect(jsonPath("[0].name").value("CTS"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	void testUniqueCompanyName() throws Exception {
		CompanyDto companyObject1 = new CompanyDto("CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");
		CompanyDto companyObject2 = new CompanyDto("CTS", "Address2", "city1", "state1", "91367", "Steve", "CEO", "900-800-800");

		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject2))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("Post-Company-DuplicateRequest"));

		mockMvc.perform(get("/company")
		).andExpect(status().isOk())
				.andExpect(jsonPath("length()").value(1));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	void postManyCompanies() throws Exception {
		CompanyDto companyObject1 = new CompanyDto("CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");
		CompanyDto companyObject2 = new CompanyDto("Google", "Address2", "city1", "state1", "91367", "Steve", "CEO", "900-800-800");
		CompanyDto companyObject3 = new CompanyDto("Microsoft", "Address2", "city1", "state1", "91367", "Steve", "CEO", "900-800-800");

		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject2))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());
		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject3))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		mockMvc.perform(get("/company")
		).andExpect(status().isOk())
				.andExpect(jsonPath("length()").value(3))
				.andExpect(jsonPath("[1].name").value("Google"))
				.andDo(print())
				.andDo(document("Get-Company-All"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	void getSingleCompanyByNameTest() throws Exception {
		CompanyDto companyObject1 = new CompanyDto("CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		mockMvc.perform(get("/company/CTS")
		).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("CTS"))
				.andDo(print())
				.andDo(document("Get-Company-ByName"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	void getSingleCompanyByName_noContentTest() throws Exception {
		mockMvc.perform(get("/company/Google")
		).andExpect(status().isNotFound())
				.andDo(print())
				.andDo(document("Get-Company-ByName-NoContent"))
				.andExpect(content().string("No Company by that name."));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void patchCompanyTest() throws Exception {
		CompanyDto companyObject1 = new CompanyDto("CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

		mockMvc.perform(post("/company")
				.content(objectMapper.writeValueAsString(companyObject1))
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		CompanyDto companyObject2 = new CompanyDto("DTS");

		mockMvc.perform(patch("/company/1")
				.content(objectMapper.writeValueAsString(companyObject2))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted())
				.andDo(print())
				.andDo(document("Patch-Company"));

		mockMvc.perform(get("/company/DTS")
		).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("DTS"));

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void patchCompany_givenNoIdTest() throws Exception {
		CompanyDto companyObject2 = new CompanyDto("DTS");

		mockMvc.perform(patch("/company/1")
				.content(objectMapper.writeValueAsString(companyObject2))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("No Company by given Id."));
	}
}
