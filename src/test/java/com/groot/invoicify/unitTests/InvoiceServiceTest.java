package com.groot.invoicify.unitTests;

import com.groot.invoicify.dto.InvoiceDto;
import com.groot.invoicify.dto.ItemDto;
import com.groot.invoicify.entity.Company;
import com.groot.invoicify.entity.Invoice;
import com.groot.invoicify.repository.CompanyRepository;
import com.groot.invoicify.repository.InvoiceRepository;
import com.groot.invoicify.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {

    @Mock
    InvoiceRepository invoiceRepository;
    @Mock
    CompanyRepository companyRepository;

    @InjectMocks
    InvoiceService invoiceService;

    @Test
    public void createInvoice() {
        var itemsDto = List.of(
                new ItemDto("Description", 10, 14.50F, 60F)
        );
        var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
        var company = new Company("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");
        var invoiceResult = new Invoice();
        invoiceResult.setInvoiceId(1L);
        var invoice = InvoiceService.MapToEntity(invoiceDto, company);

        when(companyRepository.findByName(invoiceDto.getCompanyName())).thenReturn(company);
        when(invoiceRepository.save(invoice)).thenReturn(invoiceResult);

        var invoiceId = invoiceService.createInvoice(invoiceDto);

        verify(companyRepository, times(1)).findByName(invoiceDto.getCompanyName());
        verify(invoiceRepository, times(1)).save(invoice);
        assertThat(invoiceId).isEqualTo(invoiceResult.getInvoiceId());
    }

}
