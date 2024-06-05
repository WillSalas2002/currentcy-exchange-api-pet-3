package com.will.currency.exchange.api.model;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Currency {
    private Integer id;
    private String code;
    private String fullName;
    private String sign;
}
