package com.careerit.isapp.api;

import com.careerit.isapp.dto.InvoiceDto;
import com.careerit.isapp.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/all")
    public ResponseEntity<List<InvoiceDto>> getInvoices() {
        return ResponseEntity.ok(invoiceService.getInvoices());
    }


    @GetMapping("/download-invoices")
    public ResponseEntity<Void> downloadInvoices() {
        return ResponseEntity.ok().build();
    }
}
