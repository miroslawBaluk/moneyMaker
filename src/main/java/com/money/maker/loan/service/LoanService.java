package com.money.maker.loan.service;

import com.money.maker.loan.converter.LoanToViewConverter;
import com.money.maker.loan.domain.Loan;
import com.money.maker.loan.dto.LoanView;
import com.money.maker.loan.exception.LoanException;
import com.money.maker.loan.repository.LoanRepository;
import com.money.maker.loan.validator.LoanValidator;
import com.money.maker.utils.CurrentDateTimeCatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
public class LoanService {

    private LoanValidator loanValidator;
    private LoanRepository loanRepository;
    private CurrentDateTimeCatcher currentDateTimeCatcher;
    private LoanToViewConverter loanToViewConverter;

    @Autowired
    public LoanService(LoanValidator loanValidator, LoanRepository loanRepository, CurrentDateTimeCatcher currentDateTimeCatcher,
                       LoanToViewConverter loanToViewConverter) {
        this.loanValidator = loanValidator;
        this.loanRepository = loanRepository;
        this.currentDateTimeCatcher = currentDateTimeCatcher;
        this.loanToViewConverter = loanToViewConverter;
    }

    @Transactional
    public LoanView applyForLoan(int term, BigDecimal amount) {
        LocalDateTime currentDateTime = currentDateTimeCatcher.getCurrentDateTime();
        loanValidator.validateLoan(term, amount, currentDateTime);
        LocalDate currentDate = currentDateTimeCatcher.getCurrentDateTime().toLocalDate();
        BigDecimal loanAmount = calculateLoanAmount(amount);
        Loan loan = loanRepository.save(Loan.builder()
                .amount(loanAmount)
                .startDate(currentDateTime.toLocalDate())
                .installment(calculateInstallment(term, loanAmount, currentDate))
                .endDate(calculateLoanEndDate(currentDate, term))
                .build());
        return loanToViewConverter.convert(loan);
    }

    @Transactional
    public LoanView extendLoan(Long loanId, int extensionTerm) {
        loanValidator.validateExtensionTerm(extensionTerm);
        Loan loan = loanRepository.findOne(loanId);
        setExtendedInstallment(loan, extensionTerm);
        loan.setEndDate(loan.getEndDate().plusDays(extensionTerm));
        loanRepository.save(loan);
        return loanToViewConverter.convert(loan);
    }

    private void setExtendedInstallment(Loan loan, int extensionTerm) {
        LocalDate now = currentDateTimeCatcher.getCurrentDateTime().toLocalDate();
        long monthsBetweenEndLoanAndDateAfterExtension = ChronoUnit.MONTHS.between(loan.getEndDate(), loan.getEndDate().plusDays(extensionTerm));
        long monthsBetweenNowAndStartLoan = ChronoUnit.MONTHS.between(loan.getStartDate(), now);
        if (Objects.nonNull(loan.getExtendedInstallment())) {
            throw new LoanException("cannot extend loan twice");
        }
        if (monthsBetweenEndLoanAndDateAfterExtension > 0) {
            BigDecimal moneyLeft = moneyLeft(loan, monthsBetweenNowAndStartLoan);
            long termLeftFromNowAndDateAfterExtension = ChronoUnit.MONTHS.between(now, loan.getEndDate().plusDays(extensionTerm));
            BigDecimal extendedInstalment = moneyLeft
                    .divide(BigDecimal.valueOf(termLeftFromNowAndDateAfterExtension), 2, RoundingMode.HALF_EVEN);
            loan.setExtendedInstallment(extendedInstalment);
        }
        if (monthsBetweenEndLoanAndDateAfterExtension == 0) {
            loan.setExtendedInstallment(loan.getInstallment());
        }
    }

    private BigDecimal moneyLeft(Loan loan, long monthsBetween) {
        BigDecimal amount = loan.getAmount();
        BigDecimal installment = loan.getInstallment();

        return amount.subtract(installment.multiply
                (new BigDecimal(monthsBetween)).setScale(2, RoundingMode.HALF_EVEN));
    }

    private BigDecimal calculateLoanAmount(BigDecimal amount) {
        return amount.multiply(new BigDecimal(1.1)).setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calculateInstallment(int term, BigDecimal amount, LocalDate currentDate) {
        long months = ChronoUnit.MONTHS.between(currentDate, calculateLoanEndDate(currentDate, term));
        return amount.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_EVEN);
    }

    private LocalDate calculateLoanEndDate(LocalDate currentDate, int term) {
        return currentDate.plusDays((long) term);
    }
}

