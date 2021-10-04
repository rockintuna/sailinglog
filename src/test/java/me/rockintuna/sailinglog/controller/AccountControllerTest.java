package me.rockintuna.sailinglog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rockintuna.sailinglog.dto.AccountRequestDto;
import me.rockintuna.sailinglog.service.AccountService;
import me.rockintuna.sailinglog.service.Oauth2Service;
import me.rockintuna.sailinglog.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private Oauth2Service oauth2Service;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void getRegisterPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/account/register"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void register() throws Exception {
        AccountRequestDto requestDto = AccountRequestDto.of("jilee", "password");
        String json = objectMapper.writeValueAsString(requestDto);
        mvc.perform(post("/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        verify(accountService).registerAccount(any(AccountRequestDto.class));
    }

    @Test
    void getLoginPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/account/login"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void login() throws Exception {
        mvc.perform(post("/account/login"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

}