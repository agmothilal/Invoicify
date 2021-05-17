package com.groot.invoicify.controller;

import com.groot.invoicify.dto.CompanyDto;
import com.groot.invoicify.dto.InvoiceDto;
import com.groot.invoicify.dto.ItemDto;
import com.groot.invoicify.entity.Invoice;
import com.groot.invoicify.service.CompanyService;
import com.groot.invoicify.service.InvoiceService;
import com.groot.invoicify.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
	@Autowired
	InvoiceService invoiceService;
	@Autowired
	CompanyService companyService;
	@Autowired
	ItemService itemService;

	@PostMapping
	public ResponseEntity<?> createInvoice(@RequestBody InvoiceDto invoiceDto) {
		CompanyDto companyDto = this.companyService.findSingleCompany(invoiceDto.getCompanyName());
		if (companyDto == null) {
			return new ResponseEntity<>("No Company by that name.", HttpStatus.NOT_FOUND);
		}
		else {
			var resultDto =this.invoiceService.createInvoice(invoiceDto);
			//return new ResponseEntity<>("Invoice has been created. The invoice ID is "+ id +".", HttpStatus.CREATED);
			return new ResponseEntity<InvoiceDto>(resultDto, HttpStatus.CREATED);
		}
	}

	@PutMapping
	public ResponseEntity<?> updateInvoice(@RequestParam Long invoiceId,
										   @RequestBody InvoiceDto invoiceDto){
		var invoice = this.invoiceService.updatedInvoice(invoiceId, invoiceDto);
		if (invoice == null) {
			return new ResponseEntity<>("The given Company or Invoice is not exist!", HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<>(invoice, HttpStatus.OK);
		}
	}

	@GetMapping("{companyName}")
	public ResponseEntity<?> getAllInvoicesByCompany(@PathVariable String companyName,@RequestParam(defaultValue = "0") Integer pageNo) {
		CompanyDto companyDto = this.companyService.findSingleCompany(companyName);
		if (companyDto == null) {
			return new ResponseEntity<>("No Company by that name.", HttpStatus.NOT_FOUND);
		}
		else {
			List<InvoiceDto> invoiceDtoList = invoiceService.fetchAllInvoicesByCompany(pageNo,companyName);
			if (invoiceDtoList==null)
			{
				//TODO: Fix for page number over the number of invoices.
				return new ResponseEntity<>("Company Exists, but there is no invoice for that company.", HttpStatus.NOT_FOUND);
			}
			else {
				return new ResponseEntity<>(invoiceDtoList, HttpStatus.OK);
			}
		}
	}

	@GetMapping("unpaid/{companyName}")
	public ResponseEntity<?> getAllUnPaidInvoicesByCompany(@PathVariable String companyName) {

		CompanyDto companyDto = this.companyService.findSingleCompany(companyName);
		if (companyDto == null) {
			return new ResponseEntity<>("No Company by that name.", HttpStatus.NOT_FOUND);
		}
		else {
			List<InvoiceDto> invoiceDtoList = invoiceService.fetchAllUnPaidInvoicesByCompany(companyName);
			if (invoiceDtoList==null)
			{
				return new ResponseEntity<>("Company Exists, but there is no unpaid invoice for that company.", HttpStatus.NOT_FOUND);
			}
			else {
				return new ResponseEntity<>(invoiceDtoList, HttpStatus.OK);
			}
		}
	}

	@GetMapping("id/{invoiceNum}")
	public ResponseEntity<?> getAllInvoicesByInvoiceNumber(@PathVariable Long invoiceNum) {

		InvoiceDto invoiceDto = this.invoiceService.findInvoiceByInvoiceNumber(invoiceNum);
		if (invoiceDto == null) {
			return new ResponseEntity<>("Invoice id  " + invoiceNum + " does not exist.", HttpStatus.NOT_FOUND);
		}
		else {
			return new ResponseEntity<>(invoiceDto, HttpStatus.OK);
		}
	}

	@PostMapping ("additem/{invoiceNum}")
	public ResponseEntity<?> addItemsToExistingInvoice(@PathVariable Long invoiceNum,@RequestBody List<ItemDto> itemsDtoList) {

		Invoice invoiceEntity = this.invoiceService.findInvoiceEntityByInvoiceNumber(invoiceNum);

		if (invoiceEntity == null) {
			return new ResponseEntity<>("Invoice id  " + invoiceNum + " does not exist.", HttpStatus.NOT_FOUND);
		}
		else {
			itemService.addItemsToGivenInvoiceNumber(invoiceEntity,itemsDtoList);
			return new ResponseEntity<>( "Items Added to the given invoice number successfully", HttpStatus.CREATED);
		}
	}

}
