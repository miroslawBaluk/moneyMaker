package com.money.maker.loan.service;

import com.money.maker.loan.domain.Loan;
import com.money.maker.loan.dto.LoanView;
import com.money.maker.loan.repository.LoanRepository;
import com.money.maker.loan.validator.LoanValidator;
import com.money.maker.utils.CurrentDateTimeCatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;

@Service
public class LoanService {

    private LoanValidator loanValidator;
    private LoanRepository loanRepository;
    private CurrentDateTimeCatcher currentDateTimeCatcher;

    @Autowired
    public LoanService(LoanValidator loanValidator, LoanRepository loanRepository, CurrentDateTimeCatcher currentDateTimeCatcher) {
        this.loanValidator = loanValidator;
        this.loanRepository = loanRepository;
        this.currentDateTimeCatcher = currentDateTimeCatcher;
    }

    @Transactional
    public LoanView applyForLoan(int term, BigDecimal amount) {
        LocalDateTime currentDateTime = currentDateTimeCatcher.getCurrentDateTime();
        loanValidator.validate(term, amount, currentDateTime);
        LocalDate currentDate = currentDateTimeCatcher.getCurrentDateTime().toLocalDate();
        BigDecimal loanAmount = calculateLoanAmount(amount);
        Loan loan = loanRepository.save(Loan.builder()
                .principalAmount(loanAmount)
                .startDate(currentDateTime)
                .installment(calculateInstallment(term, loanAmount, currentDate))
                .endDate(calculateLoanEndDate(currentDate, term))
                .build());

        return toView(loan);
    }

    @Transactional
    public LoanView extendLoan() {
        return LoanView.builder().build();
    }

    private BigDecimal calculateLoanAmount(BigDecimal amount) {
        return amount.multiply(new BigDecimal(1.1)).setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calculateInstallment(int term, BigDecimal amount, LocalDate currentDate) {
        Period period = Period.between(currentDate, calculateLoanEndDate(currentDate, term));
        int months = period.getMonths();
        return amount.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_EVEN);
    }

    // setScale(2, RoundingMode.HALF_EVEN)
    private LocalDate calculateLoanEndDate(LocalDate currentDate, int term) {
        return currentDate.plusDays((long) term);
    }

    private LoanView toView(Loan loan) {
        return LoanView.builder()
                .id(loan.getId())
                .principalAmount(loan.getPrincipalAmount())
                .startDate(loan.getStartDate())
                .endDate(loan.getEndDate())
                .installment(loan.getInstallment())
                .build();
    }


//    available operations are:
//    apply_for_loan (term, amount)
//if application is not within amount/term range reject application
//if application is between 00:00 and 06:00 and max amount is asked then reject application
//    cost of issued loan is 10% of principal (not 10% per year)
//    extend loan - (extension term is preconfigured. Upon extension the due date is changed, original due date + term)


}

