package com.money.maker.loan.service

import com.money.maker.loan.converter.LoanToViewConverter
import com.money.maker.loan.domain.Loan
import com.money.maker.loan.dto.LoanView
import com.money.maker.loan.properties.LoanProperties
import com.money.maker.loan.repository.LoanRepository
import com.money.maker.loan.validator.LoanValidator
import com.money.maker.utils.CurrentDateTimeCatcher
import spock.lang.Specification

import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime

class LoanServiceTest extends Specification {

    private LoanProperties loanProperties = buildLoanProperties()
    private LoanValidator loanValidator = new LoanValidator(loanProperties)
    private LoanRepository loanRepository = Mock LoanRepository
    private CurrentDateTimeCatcher currentDateTimeCatcher = Mock CurrentDateTimeCatcher
    private LoanToViewConverter loanToViewConverter = new LoanToViewConverter()
    private LoanService loanService = new LoanService(loanValidator, loanRepository,
            currentDateTimeCatcher, loanToViewConverter)

    def "should apply for loan"() {
        given:
        currentDateTimeCatcher.getCurrentDateTime() >> LocalDateTime.now()
        when:
        loanService.applyForLoan(term, amount)
        then:
        1 * loanRepository.save(_ as Loan) >> getLoan()
        where:
        term   | amount
        1000   | BigDecimal.valueOf(1000.20)
        100000 | BigDecimal.valueOf(10000.20)
    }


    def "should extend a loan"() {
        given:
        loanRepository.findOne(_ as Long) >> getLoan()
        currentDateTimeCatcher.getCurrentDateTime() >> LocalDateTime.now()
        when:
        LoanView loanView = loanService.extendLoan(1, extensionTerm)
        then:
        loanView.getId() == 1
        loanView.getEndDate() == getLoan().endDate.plusDays(extensionTerm)
        loanView.getExtendedInstallment() == extendedInstallment
        loanView.getEndDate() == endDate
        where:
        extensionTerm | extendedInstallment        | endDate
        20            | BigDecimal.valueOf(1000)   | getLoan().endDate.plusDays(20)
        32            | BigDecimal.valueOf(909.09) | getLoan().endDate.plusDays(32)
        70            | BigDecimal.valueOf(833.33) | getLoan().endDate.plusDays(70)
        100           | BigDecimal.valueOf(769.23) | getLoan().endDate.plusDays(100)
    }

    private static Loan getLoan() {
        return Loan.builder()
                .id(1)
                .startDate(LocalDate.now().minusMonths(10))
                .endDate(LocalDate.now().plusMonths(10))
                .installment(new BigDecimal(1000).setScale(2, RoundingMode.HALF_EVEN))
                .amount(new BigDecimal(20000).setScale(2, RoundingMode.HALF_EVEN))
                .build()
    }

    private static LoanProperties buildLoanProperties() {
        return LoanProperties.builder()
                .maxAmount(BigDecimal.valueOf(10000.20))
                .minAmount(BigDecimal.valueOf(1000.20))
                .maxDays(100000)
                .minDays(1000)
                .maxLoanExtensionTerm(1000)
                .minLoanExtensionTerm(20)
                .build()
    }
}
