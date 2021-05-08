package com.groot.invoicify.company;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CompanyController {

    CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("company")
    public List<CompanyDto>  getAllCompany()
    {
        return this.companyService.fetchAll();
    }

    @PostMapping("company")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCompany(@RequestBody CompanyDto companyDtoObject){
         this.companyService.create(companyDtoObject);

    }
}
