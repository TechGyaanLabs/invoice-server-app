package com.careerit.isapp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceItemDto {
    private String serviceCode;
    private String serviceName;
    private String description;
    private BigDecimal quantity;
    private BigDecimal amount;
}
