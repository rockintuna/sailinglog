package me.rockintuna.sailinglog.global.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rockintuna.sailinglog.account.AccountController;
import me.rockintuna.sailinglog.config.exception.PasswordNeverContainsUsernameException;
import me.rockintuna.sailinglog.config.exception.PasswordNotEqualsWithCheckException;
import me.rockintuna.sailinglog.config.exception.UsernameExistException;
import me.rockintuna.sailinglog.account.AccountRequestDto;
import me.rockintuna.sailinglog.account.AccountService;
import me.rockintuna.sailinglog.security.Oauth2Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AccountController.class)
@AutoConfigureRestDocs
class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private Oauth2Service oauth2Service;

    @Nested
    @DisplayName("????????? ??????")
    class Page {
        @Test
        @DisplayName("?????? ?????? ????????? ??????")
        void getRegisterPage() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/account/register"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("register"));
        }

        @Test
        @DisplayName("????????? ????????? ??????")
        void getLoginPage() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/account/login"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("login"));
        }
    }
    @Nested
    @DisplayName("?????? ??????")
    class Register {

        @Nested
        @DisplayName("?????? ?????? ??????")
        class RegisterSuccess {

            @Test
            @DisplayName("?????? ????????? ?????? ?????? ??????")
            void register() throws Exception {
                AccountRequestDto requestDto = AccountRequestDto
                        .of("jilee", "password", "password");
                String json = objectMapper.writeValueAsString(requestDto);
                mvc.perform(post("/account/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andDo(print())
                        .andExpect(status().is3xxRedirection())
                        .andExpect(view().name("redirect:/account/login"))
                        .andDo(document("register"));

                verify(accountService).registerAccount(any(AccountRequestDto.class));
            }
        }

        @Nested
        @DisplayName("?????? ?????? ??????")
        class RegisterFail {
            @Test
            @DisplayName("?????? Username")
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
            @DisplayName("???????????? ?????? Username")
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
            @DisplayName("?????? ?????? Username")
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
            @DisplayName("?????? ???????????? Username")
            void registerFailedUsernameExist() throws Exception {
                AccountRequestDto requestDto = AccountRequestDto
                        .of("jilee", "password", "password");
                String json = objectMapper.writeValueAsString(requestDto);

                willThrow(UsernameExistException.class)
                        .given(accountService).registerAccount(any(AccountRequestDto.class));

                mvc.perform(post("/account/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(accountService).registerAccount(any(AccountRequestDto.class));
            }

            @Test
            @DisplayName("?????? ??????")
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

            @Test
            @DisplayName("Username??? ???????????? ??????")
            void registerFailedPasswordContainsUsername() throws Exception {
                AccountRequestDto requestDto = AccountRequestDto
                        .of("jilee", "1jilee123", "1jilee123");
                String json = objectMapper.writeValueAsString(requestDto);

                willThrow(PasswordNeverContainsUsernameException.class)
                        .given(accountService).registerAccount(any(AccountRequestDto.class));

                mvc.perform(post("/account/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(accountService).registerAccount(any(AccountRequestDto.class));
            }

            @Test
            @DisplayName("???????????? ?????? ??????")
            void registerFailedPasswordNotEqualsWithCheck() throws Exception {
                AccountRequestDto requestDto = AccountRequestDto
                        .of("jilee", "1jilee123", "1jilee124");
                String json = objectMapper.writeValueAsString(requestDto);

                willThrow(PasswordNotEqualsWithCheckException.class)
                        .given(accountService).registerAccount(any(AccountRequestDto.class));

                mvc.perform(post("/account/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(accountService).registerAccount(any(AccountRequestDto.class));
            }

        }

    }

}