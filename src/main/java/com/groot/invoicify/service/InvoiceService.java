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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * InvoiceService
 *
 */
@Service
public class InvoiceService {

	private final InvoiceRepository invoiceRepository;
	private final CompanyRepository companyRepository;
	private final ItemRepository itemRepository;

	/**
	 *
	 * @param invoiceRepository
	 * @param companyRepository
	 * @param itemRepository
	 */
	public InvoiceService(InvoiceRepository invoiceRepository,
			CompanyRepository companyRepository,
			ItemRepository itemRepository) {
		this.invoiceRepository = invoiceRepository;
		this.companyRepository = companyRepository;
		this.itemRepository = itemRepository;
	}

	/**
	 *
	 * @param invoice
	 * @return
	 */
	private static boolean isInvoiceOlderAndPaid(Invoice invoice) {
		var previousYear = LocalDateTime.now().minusYears(1);
		return invoice.getPaid()
				&& invoice.getCreateDt().toLocalDateTime().isBefore(previousYear);
	}

	/**
	 *
	 * @param invoiceDto
	 * @param company
	 * @return
	 */
	public static Invoice MapToEntity(InvoiceDto invoiceDto, Company company) {
		return new Invoice(company,
				invoiceDto.getAuthor(),
				invoiceDto.getPaid());
	}

	/**
	 *
	 * @param invoiceDto
	 * @return
	 */
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
				itemsEntity.stream().map(item
						-> ItemService.MapToDto(item)
				).collect(Collectors.toList()));
	}

	/**
	 *
	 * @return
	 */
	public List<InvoiceDto> deletePaidAndOlderInvoices() {
		var invoiceList = new ArrayList<Invoice>();
		var invoices = this.invoiceRepository.findAll();
		invoices.forEach(invoiceList::add);
		var deleteInvoices = invoiceList.stream()
				.filter(invoice -> isInvoiceOlderAndPaid(invoice))
				.collect(Collectors.toList());

		deleteInvoices.forEach(invoice -> {
			this.invoiceRepository.delete(invoice);
		});
		return deleteInvoices.stream().map(i1 -> new InvoiceDto(i1.getInvoiceId())).collect(Collectors.toList());
	}

	/**
	 *
	 * @param pageNo
	 * @param companyName
	 * @return
	 */
	public String invoicePagingTest(Integer pageNo, String companyName) {
		Company companyEntity = companyRepository.findByName(companyName);

		Long compId = companyEntity.getCompanyId();
		Pageable paging = PageRequest.of(pageNo, 10, Sort.by("createDt"));
		Pageable paging0 = PageRequest.of(0, 10, Sort.by("createDt"));

		List<Invoice> invoices2 = invoiceRepository.findByCompanyCompanyId(compId, paging0);

		List<Invoice> invoices = invoiceRepository.findByCompanyCompanyId(compId, paging);
		if (invoices2.isEmpty() && invoices.isEmpty()) {
			return "Company Does not have Invoice.";
		}

		if (invoices.isEmpty()) {
			return "Company has invoice but page number is invalid.";
		}
		return null;
	}

	/**
	 *
	 * @param pageNo
	 * @param companyName
	 * @return
	 */
	public List<InvoiceDto> fetchAllInvoicesByCompany(Integer pageNo, String companyName) {
		Company companyEntity = companyRepository.findByName(companyName);

		Long compId = companyEntity.getCompanyId();
		Pageable paging = PageRequest.of(pageNo, 10, Sort.by("createDt"));
		List<Invoice> invoices = invoiceRepository.findByCompanyCompanyId(compId, paging);

		return invoices
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
									.stream().map(itemEnt
											-> {
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

	/**
	 *
	 * @param pageNo
	 * @param companyName
	 * @return
	 */
	public List<InvoiceDto> fetchAllUnPaidInvoicesByCompany(Integer pageNo, String companyName) {
		Company companyEntity = companyRepository.findByName(companyName);

		Long compId = companyEntity.getCompanyId();
		Pageable paging = PageRequest.of(pageNo, 10, Sort.by("createDt"));
		List<Invoice> invoices = invoiceRepository.findByCompanyCompanyIdAndPaid(compId, false, paging);

		if (invoices.isEmpty()) {

			return null;
		} else {
			return invoices
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
										.stream().map(itemEnt
												-> {

											return new ItemDto(itemEnt.getItemId(),
													itemEnt.getDescription(),
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

	/**
	 *
	 * @param invoiceNum
	 * @return
	 */
	public InvoiceDto findInvoiceByInvoiceNumber(Long invoiceNum) {
		Invoice invoiceEntity = invoiceRepository.findByInvoiceId(invoiceNum);
		if (invoiceEntity == null) {
			return null;
		} else {
			List<Item> itemEntityList = itemRepository.findByInvoiceInvoiceId(invoiceNum);
			float totalInvoiceSumLocal = (float) itemEntityList.stream().
					mapToDouble(itemEntObject -> (itemEntObject.getFlatPrice() + itemEntObject.getRatePrice() * itemEntObject.getRateHourBilled())
					).sum();

			List<ItemDto> itemDtoList = itemEntityList
					.stream().map(itemEnt
							-> {

						return new ItemDto(itemEnt.getItemId(),
								itemEnt.getDescription(),
								itemEnt.getRateHourBilled(),
								itemEnt.getRatePrice(),
								itemEnt.getFlatPrice());
					}).collect(Collectors.toList());

			return (new InvoiceDto(invoiceEntity.getInvoiceId(),
					invoiceEntity.getCompany().getName(),
					invoiceEntity.getAuthor(),
					invoiceEntity.getPaid(),
					itemDtoList,
					totalInvoiceSumLocal));
		}
	}

	/**
	 *
	 * @param invoiceNum
	 * @return
	 */
	public Invoice findInvoiceEntityByInvoiceNumber(Long invoiceNum) {
		return invoiceRepository.findByInvoiceId(invoiceNum);
	}

	/**
	 *
	 * @param invoiceId
	 * @param invoiceDto
	 * @return
	 */
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
			for (ItemDto itemDto
					: invoiceDto.getItemsDto()) {
				if (itemDto.getState() == DtoState.New) {
					var item = ItemService.MapToEntity(itemDto);
					item.setInvoice(invoice);
					var itemEntity = this.itemRepository.save(item);
					itemList.add(itemEntity);
				} else {
					var itemEntity = this.itemRepository.findById(itemDto.getItemId());
					if (itemEntity.isPresent()) {
						var item = itemEntity.get();
						if (itemDto.getState() == DtoState.Modified) {
							item.setDescription(itemDto.getDescription());
							item.setFlatPrice(itemDto.getFlatPrice());
							item.setRatePrice(itemDto.getRatePrice());
							item.setRateHourBilled(itemDto.getRateHourBilled());
							itemList.add(this.itemRepository.save(item));
						}
						if (itemDto.getState() == DtoState.Deleted) {
							this.itemRepository.delete(item);
						}
					}
				}
			}

			// Save updated invoice
			invoice.setCompany(company);
			invoice.setAuthor(invoiceDto.getAuthor());
			invoice.setPaid(invoiceDto.getPaid());
			invoice.setModifiedDt(Timestamp.valueOf(LocalDateTime.now()));
			Invoice invoiceUpdated = invoiceRepository.save(invoice);
			// Return update invoice as DTO
			return new InvoiceDto(
					invoiceUpdated.getInvoiceId(),
					invoiceUpdated.getCompany().getName(),
					invoiceUpdated.getAuthor(),
					invoiceUpdated.getPaid(),
					itemList.stream().map(item
							-> ItemService.MapToDto(item)
					).collect(Collectors.toList()));
		}
		return null;
	}

	/**
	 *
	 * @param invoiceId
	 * @return
	 */
	public boolean isInvoicePaid(Long invoiceId) {
		Optional<Invoice> invoiceEntity = invoiceRepository.findById(invoiceId);
		return invoiceEntity.isPresent() ? invoiceEntity.get().getPaid() : false;
	}

	/**
	 *
	 * @param invoiceEntity
	 */
	public void updateInvoiceModifiedDate(Invoice invoiceEntity) {
		Timestamp localTimeStamp = Timestamp.valueOf(LocalDateTime.now());
		invoiceEntity.setModifiedDt(localTimeStamp);
		invoiceRepository.save(invoiceEntity);
	}

	/**
	 *
	 * @param pageNo
	 * @param companyName
	 * @return
	 */
	public String invoicePagingUnPaidTest(Integer pageNo, String companyName) {
		Company companyEntity = companyRepository.findByName(companyName);

		Long compId = companyEntity.getCompanyId();
		Pageable paging = PageRequest.of(pageNo, 10, Sort.by("createDt"));
		Pageable paging0 = PageRequest.of(0, 10, Sort.by("createDt"));

		List<Invoice> invoices2 = invoiceRepository.findByCompanyCompanyIdAndPaid(compId, false, paging0);
		List<Invoice> invoices = invoiceRepository.findByCompanyCompanyIdAndPaid(compId, false, paging);

		if (invoices2.isEmpty() && invoices.isEmpty()) {
			return "Company Does not have any Unpaid Invoice.";
		}

		if (invoices.isEmpty()) {
			return "Company has Unpaid invoice but page number is invalid.";
		}
		return null;
	}

	/**
	 *
	 * @param pageNo
	 * @return
	 */
	public List<InvoiceDto> fetchAllInvoices(Integer pageNo) {

		Pageable paging = PageRequest.of(pageNo, 10, Sort.by("createDt"));
		var invoicePaging = invoiceRepository.findAll(paging);

		return invoicePaging
				.stream()
				.map(invoiceEntity -> {
					List<Item> itemEntList = itemRepository.findByInvoiceInvoiceId(invoiceEntity.getInvoiceId());

					return new InvoiceDto(
							invoiceEntity.getInvoiceId(),
							invoiceEntity.getCompany().getName(),
							invoiceEntity.getAuthor(),
							invoiceEntity.getPaid(),
							itemEntList.stream().map(itemEnt
									-> new ItemDto(itemEnt.getItemId(),
											itemEnt.getDescription(),
											itemEnt.getRateHourBilled(),
											itemEnt.getRatePrice(),
											itemEnt.getFlatPrice()))
									.collect(Collectors.toList())
					);
				}).collect(Collectors.toList());
	}
}
