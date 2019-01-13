package com.money.maker.loan.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Loan {

    @Id
    @GeneratedValue
    private Long id;
    private BigDecimal amount;
    private LocalDate startDate;
    private BigDecimal installment;
    private LocalDate endDate;
    private BigDecimal extendedInstallment;
}
