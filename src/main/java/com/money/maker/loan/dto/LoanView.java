package com.money.maker.loan.dto;

import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LoanView {
    private Long id;
    private BigDecimal amount;
    private LocalDate startDate;
    private BigDecimal installment;
    private LocalDate endDate;
    private BigDecimal extendedInstallment;
}