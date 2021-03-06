package com.money.maker.loan.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "loan")
@PropertySource("classpath:application.yml")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanProperties {
    private int maxDays;
    private int minDays;
    private BigDecimal maxAmount;
    private BigDecimal minAmount;
    private int minLoanExtensionTerm;
    private int maxLoanExtensionTerm;

}
