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
    public ResponseEntity<?> getAllCompany(@PathVariable String companyName) {
        CompanyDto companyDto = this.companyService.findSingleCompany(companyName);
        if (companyDto == null) {
            return new ResponseEntity<>("No Company by that name.", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(companyDto, HttpStatus.OK);
        }
    }

    @PostMapping("company")
    public ResponseEntity<?> addCompany(@RequestBody CompanyDto companyDtoObject) {

        boolean success = this.companyService.create(companyDtoObject);

        if (!success) {
            return new ResponseEntity<>("Duplicate Company Name.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Successfully added new Company.", HttpStatus.CREATED);
    }

    @PatchMapping("company/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void patchSkirt(@PathVariable Long id, @RequestBody CompanyDto companyDtoObject) {
         companyService.patchCompany(id,companyDtoObject);
    }

}
