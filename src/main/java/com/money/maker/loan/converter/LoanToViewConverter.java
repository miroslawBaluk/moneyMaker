package com.money.maker.loan.converter;

import com.money.maker.loan.domain.Loan;
import com.money.maker.loan.dto.LoanView;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LoanToViewConverter implements Converter<Loan, LoanView> {

    @Override
    public LoanView convert(Loan loan) {
        return LoanView.builder()
                .id(loan.getId())
                .amount(loan.getAmount())
                .startDate(loan.getStartDate())
                .endDate(loan.getEndDate())
                .installment(loan.getInstallment())
                .extendedInstallment(loan.getExtendedInstallment())
                .build();
    }
}
