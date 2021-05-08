package com.groot.invoicify.company;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanyController {

    @GetMapping("company")
    String getAllCompany()
    {
        return "{}";
    }

    @PostMapping("company")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCompany(){

    }
}
