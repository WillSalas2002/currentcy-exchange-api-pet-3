package com.will.currency.exchange.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CurrencyDTO {
    private int id;
    private String code;
    private String fullName;
    private String sign;

    public CurrencyDTO(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }
}
