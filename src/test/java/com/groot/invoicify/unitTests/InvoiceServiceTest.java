package com.groot.invoicify.unitTests;

import com.groot.invoicify.dto.InvoiceDto;
import com.groot.invoicify.dto.ItemDto;
import com.groot.invoicify.entity.Company;
import com.groot.invoicify.entity.Invoice;
import com.groot.invoicify.entity.Item;
import com.groot.invoicify.repository.CompanyRepository;
import com.groot.invoicify.repository.InvoiceRepository;
import com.groot.invoicify.repository.ItemRepository;
import com.groot.invoicify.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {

    @Mock
    InvoiceRepository invoiceRepository;
    @Mock
    CompanyRepository companyRepository;
    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    InvoiceService invoiceService;

    @Test
    public void createInvoice() {
        var itemsDto = List.of(
                new ItemDto("Description", 10, 14.50F, null)
        );
        var invoiceDto = new InvoiceDto("Test", "test", false, itemsDto);
        var company = new Company("Test", "Address1", "city1", "state1", "91367", "Mike", "CEO", "800-800-800");

        var invoiceResult = new Invoice();
        invoiceResult.setInvoiceId(1L);
        var items = List.of(
                new Item("Description", 10, 14.50F, null, invoiceResult)
        );

        var invoice = InvoiceService.MapToEntity(invoiceDto, company);

        when(companyRepository.findByName(invoiceDto.getCompanyName())).thenReturn(company);
        when(invoiceRepository.save(invoice)).thenReturn(invoiceResult);

        var invoiceId = invoiceService.createInvoice(invoiceDto);

        verify(companyRepository, times(1)).findByName(invoiceDto.getCompanyName());
        verify(itemRepository, times(1)).saveAll(items);
        verify(invoiceRepository, times(1)).save(invoice);
        assertThat(invoiceId).isEqualTo(invoiceResult.getInvoiceId());
    }

    @Test
    public void deleteOneYearOlderAndPaidInvoices() {
        var invoices = List.of(
                new Invoice("authorName", true,
                        Timestamp.valueOf(LocalDateTime.now().minusYears(2))),
                new Invoice("authorName", true,
                        Timestamp.valueOf(LocalDateTime.now().minusYears(1))),
                new Invoice("authorName", false,
                        Timestamp.valueOf(LocalDateTime.now().minusYears(1))),
                new Invoice("authorName", false,
                        Timestamp.valueOf(LocalDateTime.now()))
        );

        when(invoiceRepository.findAll()).thenReturn(invoices);
        var result = invoiceService.deletePaidAndOlderInvoices();

        verify(invoiceRepository, times(1)).delete(invoices.get(0));
        verify(invoiceRepository, times(1)).delete(invoices.get(1));
        assertThat(result).isEqualTo(2L);
    }
}
