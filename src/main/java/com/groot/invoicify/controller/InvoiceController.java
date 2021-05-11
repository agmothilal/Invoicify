package com.groot.invoicify.controller;

import com.groot.invoicify.dto.InvoiceDto;
import com.groot.invoicify.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
	@Autowired
	InvoiceService invoiceService;

}
