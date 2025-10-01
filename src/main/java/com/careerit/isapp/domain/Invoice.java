package com.careerit.isapp.domain;

import lombok.Data;

import java.util.List;

@Data
public class Invoice {
    private List<InvoiceItems> invoiceItems;
    private double totalAmount;
    private double taxAmount;
    private double netAmount;
}
