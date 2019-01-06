package com.money.maker.loan.service

import com.money.maker.loan.LoanException
import com.money.maker.loan.properties.LoanProperties
import com.money.maker.loan.repository.LoanRepository
import com.money.maker.loan.validator.LoanValidator
import com.money.maker.utils.CurrentDateTimeCatcher
import spock.lang.Specification

class LoanServiceTest extends Specification {


    private LoanValidator loanValidator;
    private LoanRepository loanRepository;
    private CurrentDateTimeCatcher currentDateTimeCatcher;

    private LoanProperties loanProperties = buildLoanProperties()


    private LoanService loanService = new LoanService(loanProperties)

    void setup() {

    }


    def "should create torch or return sad icon"() {
        given:

        when:
        loanService.applyForLoan(term, amount)
        then:
        LoanException ex = thrown()
        ex.message == message

        where:
        term   | amount                      | message
        100000 | BigDecimal.valueOf(100.10)  | amount + " Amount is lower then minimal"
        100000 | BigDecimal.valueOf(10001)   | amount + " Amount is higher then maximal"
        1      | BigDecimal.valueOf(1000.20) | term + " Term is lower then minimal"
        100001 | BigDecimal.valueOf(1000.20) | term + " Term is higher then maximal"

//
//        throw new LoanException(amount + " Amount is too high");
//    }
//    if(isMinAmountLower( amount)){
//        throw new LoanException(amount + " Amount is too low");
//    }
//    if(isTimeBetween( ZonedDateTime.now(ZoneId.systemDefault())) ){
//        throw new LoanException("Time is between 00:00 and 6:00");

//        int term, BigDecimal amount
//        elementsList            | term       |
//        elementsFormingTorch()  | "Torch!"
//        elementsFormingTorch2() | "Torch!"
//        emptyElements()         | ":-("
//        tooManyElements()       | ":-("
//        badCombination()        | ":-("
//        duplicated()            | ":-("
//        tooLongDistance()       | ":-("
//        outsideTheOneXaxis()    | ":-("
    }


    private static LoanProperties buildLoanProperties() {
        return LoanProperties.builder()
                .maxAmount(BigDecimal.valueOf(10000.20))
                .minAmount(BigDecimal.valueOf(1000.20))
                .maxDays(100000)
                .minDays(1000)
                .build()
    }

//
//    @Mock
//    private LoanProperties loanProperties;
//    @InjectMocks
//    private LoanService loanService;
//
//
//    @Test
//    public void calculator_returns_windless_time() {
//        //given
//        when(loanProperties.getMaxAmount()).thenReturn(new BigDecimal(10000.20));
//        when(loanProperties.getMinAmount()).thenReturn(new BigDecimal(1000.20));
//        when(loanProperties.getMaxDays()).thenReturn(100000);
//        when(loanProperties.getMinDays()).thenReturn(30);
//        //when
//        loanService.applyForLoan(30, new BigDecimal(1000));
//        //then
//        assertEquals(Double.valueOf(15.0), downtimeData.getEnd());
//    }
//
//
//
//
//    @NotNull
//    private CurtailmentData getLoanProperties() {
//
//    }

}
