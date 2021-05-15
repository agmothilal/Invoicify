package com.groot.invoicify.unitTests;

import com.groot.invoicify.dto.CompanyDto;
import com.groot.invoicify.entity.Company;
import com.groot.invoicify.repository.CompanyRepository;
import com.groot.invoicify.service.CompanyService;
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
        Company companyEntity1 = new Company("CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

        when(mockRepository.save(companyEntity1)).thenReturn(companyEntity1);

        subject.create(companyObject1);
        verify(mockRepository).save(
                new Company("CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800")
        );


    }

    @Test
    void findSingleCompanyTest() {

        CompanyDto companyObject1 = new CompanyDto("CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");
        Company companyObject2 = new Company("CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

        when(mockRepository.findByName("CTS")).thenReturn(companyObject2);

        CompanyDto actual = subject.findSingleCompany("CTS");

        assertThat(actual).isEqualTo(companyObject1);
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

    @Test
    void updateCompanyDetailsByPatchTest() {

        Company company1 = new Company(1L,"CTS", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");
        when(mockRepository.findById(1L)).thenReturn(java.util.Optional.of(company1));
        when(mockRepository.save(company1)).thenReturn(company1);
        subject.patchCompany(1L,new CompanyDto("DTS"));
        verify(mockRepository)
                .save(new Company(1L,"DTS","Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800"));


    }

}
