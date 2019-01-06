package com.money.maker.loan.validator;

import com.money.maker.loan.LoanException;
import com.money.maker.loan.properties.LoanProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@EnableConfigurationProperties(LoanProperties.class)
public class LoanValidator {

    private LoanProperties loanProperties;

    @Autowired
    LoanValidator(LoanProperties loanProperties) {
        this.loanProperties = loanProperties;
    }

    public void validate(int term, BigDecimal amount, LocalDateTime currentDateTime) {
        if (isTimeBetweenMidnightAndSix(currentDateTime)) {
            throw new LoanException("Time is between 00:00 and 6:00");
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

        if (isMinDaysLower(term)) {
            throw new LoanException(term + " Term is lower then minimal");
        }

        if (isMaxDaysHigher(term)) {
            throw new LoanException(term + " Term is higher then maximal");
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
}
