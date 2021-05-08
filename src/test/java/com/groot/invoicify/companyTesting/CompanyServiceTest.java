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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    CompanyRepository mockRepository;

    @InjectMocks
    CompanyService subject;

    @Test
    void create() {

        CompanyDto companyObject1 = new CompanyDto("CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");
        subject.create(companyObject1);
        verify(mockRepository).save(
                new Company("CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800")
        );
    }

    @Test
    void fetchAll() {

        CompanyDto companyObject1 = new CompanyDto("CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");
        CompanyDto companyObject2 = new CompanyDto("GOOGLE", "Address2", "city1", "state1", "91367", "SUNDAR", "CEO", "800-800-900");
        List<CompanyDto> companyDtoList = new ArrayList<CompanyDto>();
        companyDtoList.add(companyObject1);
        companyDtoList.add(companyObject2);

        Company companyObject3 = new Company("CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");
        Company companyObject4 = new Company("GOOGLE", "Address2", "city1", "state1", "91367", "SUNDAR", "CEO", "800-800-900");
        List<Company> companyList = new ArrayList<Company>();

        companyList.add(companyObject3);
        companyList.add(companyObject4);

        when(mockRepository.findAll()).thenReturn(companyList);

        // E Exercise
        List<CompanyDto> actual = subject.fetchAll();

        // A Assert
        assertThat(actual).isEqualTo(companyDtoList);


    }

}
