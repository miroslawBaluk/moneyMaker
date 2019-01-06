package com.money.maker.loan.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.money.maker.loan.dto.LoanView;
import com.money.maker.loan.repository.LoanRepository;
import com.money.maker.loan.service.LoanService;
import com.money.maker.loan.validator.LoanValidator;
import com.money.maker.utils.CurrentDateTimeCatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {LoanValidator.class, LoanRepository.class, CurrentDateTimeCatcher.class,
        LoanRepository.class, LoanController.class, LoanService.class})
@DataJpaTest
@EnableConfigurationProperties
@EnableJpaRepositories("com.money.maker")
@EntityScan({"com.money.maker"})
public class LoanRestTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Autowired
    private LoanController loanController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void shouldApplyForLoan() throws Exception {

        LoanView result = toLoanView(mockMvc.perform(
                MockMvcRequestBuilders.post("/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("term", "100")
                        .param("amount", BigDecimal.valueOf(2000).toString())
        ).andExpect(MockMvcResultMatchers.status()
                .isOk())
                .andReturn().getResponse().getContentAsString());


        assertThat(result.getPrincipalAmount()).isEqualTo(BigDecimal.valueOf(2200.00).setScale(2, RoundingMode.HALF_EVEN));
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStartDate().toLocalDate()).isEqualByComparingTo(LocalDate.now());
        assertThat(result.getEndDate()).isEqualByComparingTo(LocalDate.now().plusDays(100));
        assertThat(result.getInstallment()).isEqualTo(BigDecimal.valueOf(733.33).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    public void shouldReturn400OnValidationError() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.post("/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("term", "10")
                        .param("amount", BigDecimal.valueOf(2000).toString())
        ).andExpect(MockMvcResultMatchers.status()
                .isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }

    private LoanView toLoanView(String x) throws IOException {
        return objectMapper.readValue(x, LoanView.class);

    }


}
