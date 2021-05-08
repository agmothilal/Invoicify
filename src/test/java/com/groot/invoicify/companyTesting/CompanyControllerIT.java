package com.groot.invoicify.companyTesting;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.groot.invoicify.company.CompanyDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional


public class CompanyControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void initialEmptyGetTest() throws Exception{

        mockMvc.perform(get("/company"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(0));


    }

    @Test
    void initialPostTest() throws Exception{

        CompanyDto companyObject1 = new CompanyDto("CTS","Address1","city1","state1","91367","Mike","CEO","800-800-800");


        mockMvc.perform(post("/company")
                .content(objectMapper.writeValueAsString(companyObject1))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

    }

    @Test
    void dtoPostAndGetTest() throws Exception{

        CompanyDto companyObject1 = new CompanyDto("CTS","Address1","city1","state1","91367","Mike","CEO","800-800-800");

        mockMvc.perform(post("/company")
                .content(objectMapper.writeValueAsString(companyObject1))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

        mockMvc.perform(get("/company")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1))
                .andExpect(jsonPath("[0].name").value("CTS"))
        ;

    }

    @Test
    void testUniqueCompanyName() throws Exception{

        CompanyDto companyObject1 = new CompanyDto("CTS","Address1","city1","state1","91367","Mike","CEO","800-800-800");
        CompanyDto companyObject2 = new CompanyDto("CTS","Address2","city1","state1","91367","Steve","CEO","900-800-800");

        mockMvc.perform(post("/company")
                .content(objectMapper.writeValueAsString(companyObject1))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

        mockMvc.perform(post("/company")
                .content(objectMapper.writeValueAsString(companyObject2))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        mockMvc.perform(get("/company")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1));
    }



}
