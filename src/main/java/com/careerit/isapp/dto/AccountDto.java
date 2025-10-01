package com.careerit.isapp.dto;

import lombok.Data;

@Data
public class AccountDto {
    private String name;
    private String city;
    private String country;
    private Currency currency;
}
