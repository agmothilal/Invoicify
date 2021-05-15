package com.groot.invoicify.controller;

import com.groot.invoicify.dto.InvoiceDto;
import com.groot.invoicify.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
	@Autowired
	InvoiceService invoiceService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long createInvoice(@RequestBody InvoiceDto invoiceDto) {
		return this.invoiceService.createInvoice(invoiceDto);
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
}
