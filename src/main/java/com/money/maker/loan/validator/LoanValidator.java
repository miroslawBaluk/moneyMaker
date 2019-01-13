package com.money.maker.loan.validator;

import com.money.maker.loan.exception.LoanException;
import com.money.maker.loan.properties.LoanProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@EnableConfigurationProperties(LoanProperties.class)
public class LoanValidator {

    private LoanProperties loanProperties;

    @Autowired
    public LoanValidator(LoanProperties loanProperties) {
        this.loanProperties = loanProperties;
    }

    public void validateLoan(int term, BigDecimal amount, LocalDateTime currentDateTime) {
        if (isTimeBetweenMidnightAndSix(currentDateTime) && amount.equals(loanProperties.getMaxAmount())) {
            throw new LoanException("Time is between 00:00 and 6:00 and max amount is asked");
        }
        if (isMaxAmountHigher(amount)) {
            throw new LoanException(amount + " Amount is higher then maximal");
        }
        if (isMinAmountLower(amount)) {
            throw new LoanException(amount + " Amount is lower then minimal");
        }

        if (isMinDaysLower(term)) {
            throw new LoanException(term + " Term is lower then minimal");
        }

        if (isMaxDaysHigher(term)) {
            throw new LoanException(term + " Term is higher then maximal");
        }
    }

    public void validateExtensionTerm(int termExtension) {
        if (isMinExtensionTermLower(termExtension)) {
            throw new LoanException(termExtension + " Term extension is lower then minimal term extension");
        }

        if (isMaxExtensionTermHigher(termExtension)) {
            throw new LoanException(termExtension + " Term extension is higher then maximal term extension ");
        }

    }

    private boolean isTimeBetweenMidnightAndSix(LocalDateTime dateTime) {
        return dateTime.getHour() < 6;
    }

    private boolean isMaxAmountHigher(BigDecimal amount) {
        return loanProperties.getMaxAmount().compareTo(amount) < 0;
    }

    private boolean isMinAmountLower(BigDecimal amount) {
        return loanProperties.getMinAmount().compareTo(amount) > 0;
    }

    private boolean isMinDaysLower(int term) {
        return loanProperties.getMinDays() > term;
    }

    private boolean isMaxDaysHigher(int term) {
        return loanProperties.getMaxDays() < term;
    }

    private boolean isMinExtensionTermLower(int termExtension) {
        return loanProperties.getMinLoanExtensionTerm() > termExtension;
    }

    private boolean isMaxExtensionTermHigher(int termExtension) {
        return loanProperties.getMaxLoanExtensionTerm() < termExtension;
    }
}
