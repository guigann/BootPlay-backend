package br.com.sysmap.bootcamp.web;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.service.WalletService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    //credit tests
    @Test
    @DisplayName("Should credit value in wallet")
    public void shouldCreditValueInWallet() throws Exception {
        BigDecimal value = BigDecimal.valueOf(100);

        Wallet mockedWallet = Wallet.builder().balance(BigDecimal.valueOf(1100)).build();
        Mockito.when(walletService.credit(value)).thenReturn(mockedWallet);

        mockMvc.perform(MockMvcRequestBuilders.post("/wallet/credit/{value}", value)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value("1100"));
    }

    //get my wallet tests
    @Test
    @DisplayName("Should get wallet")
    public void shouldGetWallet() throws Exception {
        BigDecimal balance = BigDecimal.valueOf(1000);
        Wallet wallet = Wallet.builder().balance(balance).build();
        Mockito.when(walletService.getByUser()).thenReturn(wallet);

        mockMvc.perform(MockMvcRequestBuilders.get("/wallet")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value("1000"));
    }

}
