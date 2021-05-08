package com.groot.invoicify.company;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CompanyController {

    CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("company")
    public List<CompanyDto> getAllCompany() {
        return this.companyService.fetchAll();
    }

    @GetMapping("company/{companyName}")
    public CompanyDto getAllCompany(@PathVariable String companyName) {
        return this.companyService.findSingleCompany(companyName);
    }

    @PostMapping("company")
    public ResponseEntity<?> addCompany(@RequestBody CompanyDto companyDtoObject) {

        boolean success = this.companyService.create(companyDtoObject);

        if (!success) {
            return new ResponseEntity<>("Duplicate Company Name.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Successfully added new Company.", HttpStatus.CREATED);
    }
}
