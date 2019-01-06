package com.money.maker;

import com.money.maker.loan.properties.LoanProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//@EntityScan({ "com.money.maker" })
//@EnableJpaRepositories({ "com.money.maker" })
//@EnableAutoConfiguration
//@ComponentScan
//@EnableConfigurationProperties
//@Import({LoanProperties.class})
public class SpringApp {

	public static void main(String[] args) {

		SpringApplication.run(SpringApp.class, args);
	}
}
