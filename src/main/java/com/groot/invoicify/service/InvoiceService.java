package com.groot.invoicify.service;

import com.groot.invoicify.dto.InvoiceDto;
import com.groot.invoicify.entity.Company;
import com.groot.invoicify.entity.Invoice;
import com.groot.invoicify.entity.Item;
import com.groot.invoicify.repository.CompanyRepository;
import com.groot.invoicify.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          CompanyRepository companyRepository) {
        this.invoiceRepository = invoiceRepository;
        this.companyRepository = companyRepository;
    }

    public static Invoice MapToEntity(InvoiceDto invoiceDto, Company company) {
        var items = invoiceDto.getItemsDto().stream()
                .map(dto -> ItemService.MapToEntity(dto))
                .collect(Collectors.toList());

        return new Invoice(company,
                invoiceDto.getAuthor(),
                invoiceDto.getPaid(),
                items);
    }

    public Long createInvoice(InvoiceDto invoiceDto) {
        var company = this.companyRepository.findByName(invoiceDto.getCompanyName());
        var invoice = this.invoiceRepository.save(MapToEntity(invoiceDto, company));
        return invoice.getInvoiceId();
    }

}
