package com.groot.invoicify.unitTests;

import com.groot.invoicify.dto.DtoState;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
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

        var invoiceEntity = new Invoice(1L, company, invoiceDto.getAuthor(), invoiceDto.getPaid(),List.of(),
                Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        var expectedInvoiceDto = new InvoiceDto(1L,"Test", "test", false,
                List.of());
        var items = List.of(
                new Item("Description", 10, 14.50F, null, invoiceEntity)
        );
        var invoice = InvoiceService.MapToEntity(invoiceDto, company);

        when(companyRepository.findByName(invoiceDto.getCompanyName())).thenReturn(company);
        when(invoiceRepository.save(invoice)).thenReturn(invoiceEntity);

        var result = invoiceService.createInvoice(invoiceDto);

        verify(companyRepository, times(1)).findByName(invoiceDto.getCompanyName());
        verify(itemRepository, times(1)).saveAll(items);
        verify(invoiceRepository, times(1)).save(invoice);
        assertThat(result).isEqualTo(expectedInvoiceDto);
    }

    @Test
    public void deleteOneYearOlderAndPaidInvoices() {
        var invoices = List.of(
                new Invoice("authorName", true,
                        Timestamp.valueOf(LocalDateTime.now().minusYears(2))),
                new Invoice("authorName", true,
                        Timestamp.valueOf(LocalDateTime.now().minusYears(3))),
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

    @Test
    public void updateInvoiceTest() {
        var invoiceId = 1L;
        var invoiceDto = new InvoiceDto("Test1", "Author", true, List.of());

        var company = new Company("Test");
        var invoice = new Invoice(company, "Author", false);
        when(invoiceRepository.findById(invoiceId)).thenReturn(java.util.Optional.of(invoice));

        var newCompany = new Company(invoiceDto.getCompanyName());
        when(companyRepository.findByName(invoiceDto.getCompanyName())).thenReturn(newCompany);

        var updatedInvoice = new Invoice(newCompany, invoiceDto.getAuthor(), invoiceDto.getPaid());
        var mockInvoice = new Invoice(newCompany, invoiceDto.getAuthor(), invoiceDto.getPaid());
        mockInvoice.setInvoiceId(invoiceId);
        when(invoiceRepository.save(updatedInvoice)).thenReturn(mockInvoice);

        var result = invoiceService.updatedInvoice(invoiceId, invoiceDto);

        verify(invoiceRepository, times(1)).findById(invoiceId);
        verify(companyRepository, times(1)).findByName(invoiceDto.getCompanyName());
        verify(invoiceRepository, times(1)).save(updatedInvoice);
        var expectedInvoiceDto = new InvoiceDto(mockInvoice.getInvoiceId(),
                mockInvoice.getCompany().getName(),
                mockInvoice.getAuthor(),
                mockInvoice.getPaid(),
                List.of());
        assertEquals(expectedInvoiceDto, result);
    }

    @Test
    public void updateInvoice_noChangeToCompanyName() {
        var invoiceId = 1L;
        var invoiceDto = new InvoiceDto("Test", "Author", true, List.of());

        var company = new Company("Test");
        var invoice = new Invoice(company, "Author", false);
        when(invoiceRepository.findById(invoiceId)).thenReturn(java.util.Optional.of(invoice));

        var updatedInvoice = new Invoice(company, invoiceDto.getAuthor(), invoiceDto.getPaid());
        var mockInvoice = new Invoice(company, invoiceDto.getAuthor(), invoiceDto.getPaid());
        mockInvoice.setInvoiceId(invoiceId);
        when(invoiceRepository.save(updatedInvoice)).thenReturn(mockInvoice);

        var result = invoiceService.updatedInvoice(invoiceId, invoiceDto);

        verify(invoiceRepository, times(1)).findById(invoiceId);
        verify(companyRepository, times(0)).findByName(invoiceDto.getCompanyName());
        verify(invoiceRepository, times(1)).save(updatedInvoice);
        var expectedInvoiceDto = new InvoiceDto(mockInvoice.getInvoiceId(),
                mockInvoice.getCompany().getName(),
                mockInvoice.getAuthor(),
                mockInvoice.getPaid(),
                List.of());
        assertEquals(expectedInvoiceDto, result);
    }

    @Test
    public void updateInvoice_modifiedItem() {
        ItemDto itemDto = new ItemDto("Description", 10, 14.50F, null);
        Item itemEntity = new Item("Description", 10, 14.50F, null);

        var invoiceId = 1L;
        var invoiceDto = new InvoiceDto("Test", "Author", true, List.of(itemDto));

        var company = new Company("Test");
        var invoice = new Invoice(company, "Author", false);
        when(invoiceRepository.findById(invoiceId)).thenReturn(java.util.Optional.of(invoice));

        var updatedInvoice = new Invoice(company, invoiceDto.getAuthor(), invoiceDto.getPaid());
        var mockInvoice = new Invoice(company, invoiceDto.getAuthor(), invoiceDto.getPaid());
        mockInvoice.setInvoiceId(invoiceId);
        itemEntity.setInvoice(updatedInvoice);
        when(invoiceRepository.save(updatedInvoice)).thenReturn(mockInvoice);

        when(itemRepository.save(new Item("Description", 10, 14.50F, null, invoice))).thenReturn(itemEntity);

        var result = invoiceService.updatedInvoice(invoiceId, invoiceDto);

        verify(invoiceRepository, times(1)).findById(invoiceId);
        verify(companyRepository, times(0)).findByName(invoiceDto.getCompanyName());
        verify(invoiceRepository, times(1)).save(updatedInvoice);
        var expectedInvoiceDto = new InvoiceDto(mockInvoice.getInvoiceId(),
                mockInvoice.getCompany().getName(),
                mockInvoice.getAuthor(),
                mockInvoice.getPaid(),
                List.of(new ItemDto(null, "Description", 10, 14.50F, null)));
        assertEquals(expectedInvoiceDto, result);
    }


    @Test
    public void updateInvoice_deleteItem() {
        ItemDto itemDto = new ItemDto(1L, "Description", 10, 14.50F, null);
        itemDto.setState(DtoState.Deleted);
        Item itemEntity = new Item(1L, "Description", 10, 14.50F, null);

        var invoiceId = 1L;
        var invoiceDto = new InvoiceDto("Test", "Author", true, List.of(itemDto));

        var company = new Company("Test");
        var invoice = new Invoice(company, "Author", false);
        when(invoiceRepository.findById(invoiceId)).thenReturn(java.util.Optional.of(invoice));

        var updatedInvoice = new Invoice(company, invoiceDto.getAuthor(), invoiceDto.getPaid());
        var mockInvoice = new Invoice(company, invoiceDto.getAuthor(), invoiceDto.getPaid());
        mockInvoice.setInvoiceId(invoiceId);
        itemEntity.setInvoice(updatedInvoice);
        when(invoiceRepository.save(updatedInvoice)).thenReturn(mockInvoice);

        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(itemEntity));


        var result = invoiceService.updatedInvoice(invoiceId, invoiceDto);

        verify(invoiceRepository, times(1)).findById(invoiceId);
        verify(companyRepository, times(0)).findByName(invoiceDto.getCompanyName());
        verify(invoiceRepository, times(1)).save(updatedInvoice);
        verify(itemRepository, times(1)).delete(itemEntity);
        var expectedInvoiceDto = new InvoiceDto(mockInvoice.getInvoiceId(),
                mockInvoice.getCompany().getName(),
                mockInvoice.getAuthor(),
                mockInvoice.getPaid(),
                List.of());
        assertEquals(expectedInvoiceDto, result);
    }

    @Test
    public void isInvoicePaidReturnTrueTest() {
        var invoiceId = 1L;
        var company = new Company("Test");
        var invoice = new Invoice(company, "Author", true);
        when(invoiceRepository.findById(invoiceId)).thenReturn(java.util.Optional.of(invoice));

        var result = invoiceService.isInvoicePaid(invoiceId);

        verify(invoiceRepository, times(1)).findById(invoiceId);
        assertThat(result).isTrue();
    }

    @Test
    public void isInvoicePaidReturnFalse() {
        var invoiceId = 1L;
        var company = new Company("Test");
        var invoice = new Invoice(company, "Author", false);
        when(invoiceRepository.findById(invoiceId)).thenReturn(java.util.Optional.of(invoice));

        var result = invoiceService.isInvoicePaid(invoiceId);

        verify(invoiceRepository, times(1)).findById(invoiceId);
        assertThat(result).isFalse();
    }

    @Test
    public void isInvoicePaidReturnFalseWhenInvoiceNotFound() {
        var invoiceId = 1L;
        var result = invoiceService.isInvoicePaid(invoiceId);
        verify(invoiceRepository, times(1)).findById(invoiceId);
        assertThat(result).isFalse();
    }
}
