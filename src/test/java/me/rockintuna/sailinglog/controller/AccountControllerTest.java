package me.rockintuna.sailinglog.controller;

import me.rockintuna.sailinglog.dto.AccountRequestDto;
import me.rockintuna.sailinglog.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    private AccountService accountService;

    @Test
    void getRegisterPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/account/register"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void register() throws Exception {
        mvc.perform(post("/account/register")
                        .param("username","jilee")
                        .param("password", "password"))
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