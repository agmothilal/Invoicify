package com.groot.invoicify.company;

import com.groot.invoicify.entity.Company;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private final CompanyRepository companyRepos;

    public CompanyService(CompanyRepository companyRepos) {
        this.companyRepos = companyRepos;
    }

    public void create(CompanyDto companyDtoObject){
        companyRepos.save(
                new Company(companyDtoObject.getName(),companyDtoObject.getAddress(),companyDtoObject.getCity(),
                        companyDtoObject.getState(),companyDtoObject.getZip(),companyDtoObject.getContactName(),
                        companyDtoObject.getContactTitle(),companyDtoObject.getContactPhoneNumber()
                )
        );

    }
}
