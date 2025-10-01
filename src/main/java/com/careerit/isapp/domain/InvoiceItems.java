package com.careerit.isapp.domain;

import lombok.Data;

@Data
public class InvoiceItems {

    private String serviceCode;
    private String serviceName;
    private String description;
    private double quantity;
    private double amount;
}
