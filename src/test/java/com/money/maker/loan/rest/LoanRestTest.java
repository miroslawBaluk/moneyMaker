package com.money.maker.loan.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.money.maker.loan.converter.LoanToViewConverter;
import com.money.maker.loan.domain.Loan;
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
        LoanRepository.class, LoanController.class, LoanService.class, LoanToViewConverter.class})
@DataJpaTest
@EnableConfigurationProperties
@EnableJpaRepositories("com.money.maker")
@EntityScan({"com.money.maker"})
public class LoanRestTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Autowired
    private LoanController loanController;

    @Autowired
    private LoanRepository loanRepository;

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

        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(2200.00).setScale(2, RoundingMode.HALF_EVEN));
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStartDate()).isEqualByComparingTo(LocalDate.now());
        assertThat(result.getEndDate()).isEqualByComparingTo(LocalDate.now().plusDays(100));
        assertThat(result.getInstallment()).isEqualTo(BigDecimal.valueOf(733.33).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    public void shouldApplyForLoan2() throws Exception {

        LoanView result = toLoanView(mockMvc.perform(
                MockMvcRequestBuilders.post("/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("term", "100")
                        .param("amount", BigDecimal.valueOf(3000).toString())
        ).andExpect(MockMvcResultMatchers.status()
                .isOk())
                .andReturn().getResponse().getContentAsString());

        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(3300.00).setScale(2, RoundingMode.HALF_EVEN));
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStartDate()).isEqualByComparingTo(LocalDate.now());
        assertThat(result.getEndDate()).isEqualByComparingTo(LocalDate.now().plusDays(100));
        assertThat(result.getInstallment()).isEqualTo(BigDecimal.valueOf(1100.00).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    public void shouldReturn400validationErrorOnApplyForLoan() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("term", "10")
                        .param("amount", BigDecimal.valueOf(2000).toString())
        ).andExpect(MockMvcResultMatchers.status()
                .isBadRequest());
    }

    @Test
    public void shouldExtendLoanTerm() throws Exception {
        Long loanId = loanRepository.save(getLoan()).getId();
        LoanView result = toLoanView(mockMvc.perform(
                MockMvcRequestBuilders.put("/loan/" + loanId + "/extend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("extensionTerm", "100")
        ).andExpect(MockMvcResultMatchers.status()
                .isOk())
                .andReturn().getResponse().getContentAsString());

        assertThat(result.getEndDate()).isEqualByComparingTo(getLoan().getEndDate().plusDays(100));
        assertThat(result.getExtendedInstallment()).isEqualTo(BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    public void shouldReturn400validationErrorOnExtendLoanTerm() throws Exception {
        Long loanId = loanRepository.save(getLoan()).getId();
        mockMvc.perform(
                MockMvcRequestBuilders.put("/loan/" + loanId + "/extend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("extensionTerm", "2001")
        ).andExpect(MockMvcResultMatchers.status()
                .isBadRequest());
    }

    @Test
    public void shouldReturn400validationErrorOnTwiceExtendLoanTerm() throws Exception {
        Long loanId = loanRepository.save(getLoan()).getId();
        mockMvc.perform(
                MockMvcRequestBuilders.put("/loan/" + loanId + "/extend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("extensionTerm", "100")
        ).andExpect(MockMvcResultMatchers.status()
                .isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.put("/loan/" + loanId + "/extend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("extensionTerm", "100")
        ).andExpect(MockMvcResultMatchers.status()
                .isBadRequest());
    }

    private LoanView toLoanView(String loanViewString) throws IOException {
        return objectMapper.readValue(loanViewString, LoanView.class);
    }

    private Loan getLoan() {
        return Loan.builder()
                .startDate(LocalDate.now().minusMonths(3))
                .endDate(LocalDate.now().plusMonths(3))
                .amount(BigDecimal.valueOf(60000))
                .installment(BigDecimal.valueOf(10000))
                .build();
    }
}
