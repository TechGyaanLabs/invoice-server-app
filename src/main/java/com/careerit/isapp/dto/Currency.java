package com.careerit.isapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Currency {

    private String symbol;
    private String name;
    @JsonProperty("symbol_native")
    private String symbolNative;
    @JsonProperty("decimal_digits")
    private int decimalDigits;
    private int rounding;
    private String code;
    @JsonProperty("name_plural")
    private String namePlural;

}