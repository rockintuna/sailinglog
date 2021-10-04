package me.rockintuna.sailinglog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rockintuna.sailinglog.dto.AccountRequestDto;
import me.rockintuna.sailinglog.service.AccountService;
import me.rockintuna.sailinglog.service.Oauth2Service;
import me.rockintuna.sailinglog.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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


    @Nested
    @DisplayName("회원 가입")
    class Page {
        @Test
        @DisplayName("회원 가입 페이지 로딩")
        void getRegisterPage() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/account/register"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("register"));
        }

        @Test
        @DisplayName("로그인 페이지 로딩")
        void getLoginPage() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/account/login"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("login"));
        }
    }
    @Nested
    @DisplayName("회원 가입")
    class Register {

        @Nested
        @DisplayName("회원 가입 성공")
        class RegisterSuccess {

            @Test
            @DisplayName("정상 데이터 회원 가입 성공")
            void register() throws Exception {
                AccountRequestDto requestDto = AccountRequestDto
                        .of("jilee", "password", "password");
                String json = objectMapper.writeValueAsString(requestDto);
                mvc.perform(post("/account/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andDo(print())
                        .andExpect(status().is3xxRedirection());

                verify(accountService).registerAccount(any(AccountRequestDto.class));
            }
        }

        @Nested
        @DisplayName("회원 가입 실패")
        class RegisterFailed {
            @Test
            @DisplayName("짧은 Username 사용")
            void registerFailedUsernameShort() throws Exception {
                AccountRequestDto requestDto = AccountRequestDto
                        .of("ji", "password", "password");
                String json = objectMapper.writeValueAsString(requestDto);
                mvc.perform(post("/account/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(accountService, never()).registerAccount(any(AccountRequestDto.class));
            }

            @Test
            @DisplayName("특수문자 포함 Username 사용")
            void registerFailedUsernameInvalid() throws Exception {
                AccountRequestDto requestDto = AccountRequestDto
                        .of("jile/e", "password", "password");
                String json = objectMapper.writeValueAsString(requestDto);
                mvc.perform(post("/account/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(accountService, never()).registerAccount(any(AccountRequestDto.class));
            }

            @Test
            @DisplayName("공백 포함 Username 사용")
            void registerFailedUsernameWithEmptySpace() throws Exception {
                AccountRequestDto requestDto = AccountRequestDto
                        .of("jil ee", "password", "password");
                String json = objectMapper.writeValueAsString(requestDto);
                mvc.perform(post("/account/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(accountService, never()).registerAccount(any(AccountRequestDto.class));
            }

            @Test
            @DisplayName("짧은 암호 사용")
            void registerFailedPasswordShort() throws Exception {
                AccountRequestDto requestDto = AccountRequestDto
                        .of("jilee", "123", "123");
                String json = objectMapper.writeValueAsString(requestDto);
                mvc.perform(post("/account/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(accountService, never()).registerAccount(any(AccountRequestDto.class));
            }

        }

    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("로그인 성공")
        void login() throws Exception {
            mvc.perform(post("/account/login"))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection());
        }
    }

}