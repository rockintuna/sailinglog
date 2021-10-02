package me.rockintuna.sailinglog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void getRegisterPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/account/register"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void register() throws Exception {
        mvc.perform(post("/account/register"))
                .andDo(print())

                .andExpect(status().is3xxRedirection());
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