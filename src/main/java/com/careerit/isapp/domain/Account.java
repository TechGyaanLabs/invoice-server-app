package com.careerit.isapp.domain;

import lombok.Data;

@Data
public class Account {

    private String name;
    private String city;
    private String country;
    private String currencySymbol;
    private Invoice invoice;
}
