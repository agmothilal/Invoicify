package com.groot.invoicify.service;

import com.groot.invoicify.dto.DtoState;
import com.groot.invoicify.dto.InvoiceDto;
import com.groot.invoicify.dto.ItemDto;
import com.groot.invoicify.entity.Company;
import com.groot.invoicify.entity.Invoice;
import com.groot.invoicify.entity.Item;
import com.groot.invoicify.repository.CompanyRepository;
import com.groot.invoicify.repository.InvoiceRepository;
import com.groot.invoicify.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
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
        var itemsEntity = this.itemRepository.saveAll(items);

        return new InvoiceDto(invoice.getInvoiceId(),
                invoice.getCompany().getName(),
                invoice.getAuthor(),
                invoice.getPaid(),
                itemsEntity.stream().map(item ->
                        ItemService.MapToDto(item)
                ).collect(Collectors.toList()));
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

	public List<InvoiceDto> fetchAllInvoicesByCompany(String companyName) {
		deletePaidAndOlderInvoices();
		Company companyEntity = companyRepository.findByName(companyName);

		Long compId = companyEntity.getCompanyId();


		if (invoiceRepository.findByCompanyCompanyId(compId).isEmpty()) {

			return null;
		} else {
			return invoiceRepository.findByCompanyCompanyId(compId)
					.stream()
					.map(invoiceEntity -> {

						List<Item> itemEntList = itemRepository.findByInvoiceInvoiceId(invoiceEntity.getInvoiceId());
						float totalInvoiceSumLocal = (float) itemEntList.stream().
								mapToDouble(itemEntObject -> (itemEntObject.getFlatPrice() + itemEntObject.getRatePrice() * itemEntObject.getRateHourBilled())
								).sum();


						return new InvoiceDto(
								invoiceEntity.getInvoiceId(),
								companyName,
								invoiceEntity.getAuthor(),
								invoiceEntity.getPaid(),
								itemEntList
										.stream().map(itemEnt ->
								{

									return new ItemDto(itemEnt.getDescription(),
											itemEnt.getRateHourBilled(),
											itemEnt.getRatePrice(),
											itemEnt.getFlatPrice());
								}).collect(Collectors.toList()),
								totalInvoiceSumLocal


						);
					})
					.collect(Collectors.toList());
		}
	}

	public List<InvoiceDto> fetchAllUnPaidInvoicesByCompany(String companyName) {
		deletePaidAndOlderInvoices();
		Company companyEntity = companyRepository.findByName(companyName);

		Long compId = companyEntity.getCompanyId();

		if (invoiceRepository.findByCompanyCompanyIdAndPaid(compId, false).isEmpty()) {

			return null;
		} else {
			return invoiceRepository.findByCompanyCompanyIdAndPaid(compId, false)
					.stream()
					.map(invoiceEntity -> {

						List<Item> itemEntList = itemRepository.findByInvoiceInvoiceId(invoiceEntity.getInvoiceId());
						float totalInvoiceSumLocal = (float) itemEntList.stream().
								mapToDouble(itemEntObject -> (itemEntObject.getFlatPrice() + itemEntObject.getRatePrice() * itemEntObject.getRateHourBilled())
								).sum();


						return new InvoiceDto(
								invoiceEntity.getInvoiceId(),
								companyName,
								invoiceEntity.getAuthor(),
								invoiceEntity.getPaid(),
								itemEntList
										.stream().map(itemEnt ->
								{

									return new ItemDto(itemEnt.getDescription(),
											itemEnt.getRateHourBilled(),
											itemEnt.getRatePrice(),
											itemEnt.getFlatPrice());
								}).collect(Collectors.toList()),
								totalInvoiceSumLocal


						);
					})
					.collect(Collectors.toList());
		}
	}

	public InvoiceDto findInvoiceByInvoiceNumber(Long invoiceNum) {
		deletePaidAndOlderInvoices();
		Invoice invoiceEntity = invoiceRepository.findByInvoiceId(invoiceNum);
		if (invoiceEntity == null) {
			return null;
		} else {
			List<Item> itemEntityList = itemRepository.findByInvoiceInvoiceId(invoiceNum);
			float totalInvoiceSumLocal = (float) itemEntityList.stream().
					mapToDouble(itemEntObject -> (itemEntObject.getFlatPrice() + itemEntObject.getRatePrice() * itemEntObject.getRateHourBilled())
					).sum();

			List<ItemDto> itemDtoList = itemEntityList
					.stream().map(itemEnt ->
					{

						return new ItemDto(itemEnt.getDescription(),
								itemEnt.getRateHourBilled(),
								itemEnt.getRatePrice(),
								itemEnt.getFlatPrice());
					}).collect(Collectors.toList());

			return (new InvoiceDto(invoiceEntity.getInvoiceId(),
					invoiceEntity.getCompany().getName(),
					invoiceEntity.getAuthor(),
					invoiceEntity.getPaid(),
					itemDtoList,
					totalInvoiceSumLocal)
			);
		}
	}

	public Invoice findInvoiceEntityByInvoiceNumber(Long invoiceNum) {
		deletePaidAndOlderInvoices();
		return invoiceRepository.findByInvoiceId(invoiceNum);
	}

    public InvoiceDto updatedInvoice(long invoiceId, InvoiceDto invoiceDto) {
        Optional<Invoice> invoiceEntity = invoiceRepository.findById(invoiceId);

        if (invoiceEntity.isPresent()) {
            var invoice = invoiceEntity.get();
            // If company not match then gen company from company repo
            var company = invoice.getCompany();
            if (!company.getName().equalsIgnoreCase(invoiceDto.getCompanyName())) {
                company = companyRepository.findByName(invoiceDto.getCompanyName());
                if (company == null) {
                    return null;
                }
            }
            // Save all line items
            var itemList = new ArrayList<Item>();
            for (ItemDto itemDto:
                    invoiceDto.getItemsDto()) {
                if (itemDto.getState() == DtoState.New) {
                    var item = ItemService.MapToEntity(itemDto);
                    item.setInvoice(invoice);
                    var itemEntity = this.itemRepository.save(item);
                    itemList.add(itemEntity);
                }
                else {
                    var itemEntity = this.itemRepository.findById(itemDto.getItemId());
                    if(itemEntity.isPresent()) {
                        var item = itemEntity.get();
                        if(itemDto.getState() == DtoState.Modified) {
                            item.setDescription(itemDto.getDescription());
                            item.setFlatPrice(itemDto.getFlatPrice());
                            item.setRatePrice(itemDto.getRatePrice());
                            item.setRateHourBilled(itemDto.getRateHourBilled());
                            itemList.add(this.itemRepository.save(item));
                        }
                        if(itemDto.getState() == DtoState.Deleted) {
                            this.itemRepository.delete(item);
                        }
                    }
                }
            }

            // Save updated invoice
            invoice.setCompany(company);
            invoice.setAuthor(invoiceDto.getAuthor());
            invoice.setPaid(invoiceDto.getPaid());
            Invoice invoiceUpdated = invoiceRepository.save(invoice);
            // Return update invoice as DTO
            return new InvoiceDto(
                    invoiceUpdated.getInvoiceId(),
                    invoiceUpdated.getCompany().getName(),
                    invoiceUpdated.getAuthor(),
                    invoiceUpdated.getPaid(),
                    itemList.stream().map(item ->
                            ItemService.MapToDto(item)
                    ).collect(Collectors.toList()));
        }
        return null;
    }

    public boolean isInvoicePaid(Long invoiceId) {
		Optional<Invoice> invoiceEntity = invoiceRepository.findById(invoiceId);
		return invoiceEntity.isPresent() ? invoiceEntity.get().getPaid() : false;
	}
}
