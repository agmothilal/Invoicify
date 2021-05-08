package com.groot.invoicify.companyTesting;

import com.groot.invoicify.company.CompanyDto;
import com.groot.invoicify.company.CompanyRepository;
import com.groot.invoicify.company.CompanyService;
import com.groot.invoicify.entity.Company;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    CompanyRepository mockRepository;

    @InjectMocks
    CompanyService subject;

    @Test
    void create(){

        CompanyDto companyObject1 = new CompanyDto("CTS","Address1","city1","state1","91367","Mike","CEO","800-800-800");
        subject.create(companyObject1);
        verify(mockRepository).save(
                new Company("CTS","Address1","city1","state1","91367","Mike","CEO","800-800-800")
        );
    }

}
