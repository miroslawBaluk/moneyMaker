package com.money.maker.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class ApplyForLoanMessage {

    private int term;
    private BigDecimal amount;

}
