package com.money.maker.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CurrentDateTimeCatcher {

    public LocalDateTime getCurrentDateTime(){
        return LocalDateTime.now();
    }

}
