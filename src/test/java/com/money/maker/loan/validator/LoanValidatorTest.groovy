package com.money.maker.loan.validator

import com.money.maker.loan.LoanException
import com.money.maker.loan.properties.LoanProperties
import spock.lang.Specification

import java.time.LocalDateTime

class LoanValidatorTest extends Specification {

    private LoanProperties loanProperties = buildLoanProperties()
    private LoanValidator loanValidator = new LoanValidator(loanProperties)

    def "should throw validation error"() {
        when:
        loanValidator.validate(term, amount, dateTime)
        then:
        LoanException ex = thrown()
        ex.message == message

        where:
        term   | amount                      | message                                   | dateTime
        100000 | BigDecimal.valueOf(100.10)  | amount + " Amount is lower then minimal"  | LocalDateTime.of(2018, 1, 5, 8, 0, 0)
        100000 | BigDecimal.valueOf(10001)   | amount + " Amount is higher then maximal" | LocalDateTime.of(2018, 1, 5, 8, 0, 0)
        1      | BigDecimal.valueOf(1000.20) | term + " Term is lower then minimal"      | LocalDateTime.of(2018, 1, 5, 8, 0, 0)
        100001 | BigDecimal.valueOf(1000.20) | term + " Term is higher then maximal"     | LocalDateTime.of(2018, 1, 5, 6, 0, 0)
        10000  | BigDecimal.valueOf(1000.20) | "Time is between 00:00 and 6:00"          | LocalDateTime.of(2018, 1, 5, 4, 59, 59)
    }

    def "shouldn't throw validation error"() {
        when:
        loanValidator.validate(term, amount, dateTime)
        then:
        noExceptionThrown()

        where:
        term   | amount                       | dateTime
        1000   | BigDecimal.valueOf(1000.20)  | LocalDateTime.of(2018, 1, 5, 6, 0, 0)
        100000 | BigDecimal.valueOf(10000.20) | LocalDateTime.of(2018, 1, 5, 23, 59, 59)

    }

    private static LoanProperties buildLoanProperties() {
        return LoanProperties.builder()
                .maxAmount(BigDecimal.valueOf(10000.20))
                .minAmount(BigDecimal.valueOf(1000.20))
                .maxDays(100000)
                .minDays(1000)
                .build()
    }
}
