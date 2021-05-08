package com.groot.invoicify.company;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CompanyController {

    List<CompanyDto> companyList = new ArrayList<CompanyDto>();

    @GetMapping("company")
    public List<CompanyDto>  getAllCompany()
    {
        return companyList;
    }

    @PostMapping("company")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCompany(@RequestBody CompanyDto companyDtoObject){
        companyList.add(companyDtoObject);

    }
}
