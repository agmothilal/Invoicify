package com.groot.invoicify.company;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanyController {

    @GetMapping("company")
    String getAllCompany()
    {
        return "{}";
    }
}
