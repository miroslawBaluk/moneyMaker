package com.money.maker.loan.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
//@Table(name = "LOAN")
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue
    private Long id;
    private BigDecimal principalAmount;
    private LocalDateTime startDate;
    private BigDecimal installment;
    private LocalDate endDate;


}
