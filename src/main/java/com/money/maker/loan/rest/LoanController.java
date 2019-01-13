package com.money.maker.loan.rest;

import com.money.maker.loan.dto.LoanView;
import com.money.maker.loan.service.LoanService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class LoanController {

    private LoanService loanService;

    LoanController(LoanService loanService){
        this.loanService = loanService;
    }

    @RequestMapping(value = "/loan", method = RequestMethod.POST)
    public LoanView applyForLoan(@RequestParam() int term, @RequestParam() BigDecimal amount) {
        return loanService.applyForLoan(term, amount);
    }

    @RequestMapping(value = "/loan/{loanId}/extend", method = RequestMethod.PUT)
    public LoanView extendLoanTerm(@PathVariable Long loanId, @RequestParam int extensionTerm) {
        return loanService.extendLoan(loanId, extensionTerm);
    }
}
