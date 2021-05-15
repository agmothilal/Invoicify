package com.groot.invoicify.service;

import com.groot.invoicify.dto.InvoiceDto;
import com.groot.invoicify.dto.ItemDto;
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
import java.util.Arrays;
import java.util.List;
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
}
