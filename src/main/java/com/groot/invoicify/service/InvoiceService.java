package com.groot.invoicify.service;

import com.groot.invoicify.dto.InvoiceDto;
import com.groot.invoicify.entity.Company;
import com.groot.invoicify.entity.Invoice;
import com.groot.invoicify.entity.Item;
import com.groot.invoicify.repository.CompanyRepository;
import com.groot.invoicify.repository.InvoiceRepository;
import com.groot.invoicify.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;
    private final ItemRepository itemRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          CompanyRepository companyRepository,
                          ItemRepository itemRepository) {
        this.invoiceRepository = invoiceRepository;
        this.companyRepository = companyRepository;
        this.itemRepository = itemRepository;
    }

    private static boolean isInvoiceOlderAndPaid(Invoice invoice) {
        var previousYear = LocalDateTime.now().minusYears(1);
        return invoice.getPaid()
                && invoice.getCreateDt().toLocalDateTime().isBefore(previousYear);
    }

    public static Invoice MapToEntity(InvoiceDto invoiceDto, Company company) {
        return new Invoice(company,
                invoiceDto.getAuthor(),
                invoiceDto.getPaid());
    }

    public Long createInvoice(InvoiceDto invoiceDto) {
        var company = this.companyRepository.findByName(invoiceDto.getCompanyName());
        var invoiceWithItems = MapToEntity(invoiceDto, company);
        var invoice = this.invoiceRepository.save(invoiceWithItems);

        // Save all items
        var items = invoiceDto.getItemsDto().stream()
                .map(dto -> {
                    var item = ItemService.MapToEntity(dto);
                    item.setInvoice(invoice);
                    return item;
                })
                .collect(Collectors.toList());
        this.itemRepository.saveAll(items);

        return invoice.getInvoiceId();
    }

    public Long deletePaidAndOlderInvoices() {
        var invoiceList = new ArrayList<Invoice>();
        var invoices = this.invoiceRepository.findAll();
        invoices.forEach(invoiceList::add);
        var deleteInvoices = invoiceList.stream()
                .filter(invoice -> isInvoiceOlderAndPaid(invoice))
                .collect(Collectors.toList());

        deleteInvoices.forEach(invoice -> {
            this.invoiceRepository.delete(invoice);
        });
        return deleteInvoices.stream().count();
    }

    public InvoiceDto updatedInvoice(long invoiceId, InvoiceDto invoiceDto) {
        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);


        if (invoice.isPresent()) {
            if(invoice.get().getCompany().getName().equalsIgnoreCase(invoiceDto.getCompanyName())) {

                var items = invoiceDto.getItemsDto().stream()
                        .map(dto -> {
                            var item = ItemService.MapToEntity(dto);
                            item.setInvoice(invoice.get());
                            return item;
                        })
                        .collect(Collectors.toList());
                this.itemRepository.saveAll(items);

                invoice.get().setAuthor(invoiceDto.getAuthor());
                invoice.get().setPaid(invoiceDto.getPaid());

                Invoice invoiceUpdated = invoiceRepository.save(invoice.get());

                return new InvoiceDto(invoiceUpdated.getCompany().getName(),
                        invoiceUpdated.getAuthor(),
                        invoiceUpdated.getPaid(),
                        invoiceUpdated.getItem().stream().map(item ->

                                ItemService.MapToDto(item)

                        ).collect(Collectors.toList()));

            } else {
                Company company = companyRepository.findByName(invoice.get().getCompany().getName());
                var invoiceWithItems = MapToEntity(invoiceDto, company);
                var invoiceUpdated = this.invoiceRepository.save(invoiceWithItems);

                return new InvoiceDto(invoiceUpdated.getCompany().getName(),
                        invoiceUpdated.getAuthor(),
                        invoiceUpdated.getPaid(),
                        invoiceUpdated.getItem().stream().map(item ->

                             ItemService.MapToDto(item)

                        ).collect(Collectors.toList()));
            }
        }
        return null;
    }
}
