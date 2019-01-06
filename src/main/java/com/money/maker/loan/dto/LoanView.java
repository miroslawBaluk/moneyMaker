package com.money.maker.loan.dto;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class LoanView {

    private Long id;
    private BigDecimal principalAmount;
    private LocalDateTime startDate;
    private BigDecimal installment;
    private LocalDate endDate;
}
