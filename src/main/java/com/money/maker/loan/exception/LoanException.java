package com.money.maker.loan.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class LoanException extends RuntimeException{
    public LoanException(String message){
        super(message);
    }
}
