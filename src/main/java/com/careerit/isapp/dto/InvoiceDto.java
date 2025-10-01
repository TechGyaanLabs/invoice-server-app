package com.careerit.isapp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class InvoiceDto {
    private AccountDto accountDto;
    private List<InvoiceItemDto> invoiceItemsDtos;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal netAmount;
}
