package me.rockintuna.sailinglog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.dto.KakaoAccountDto;
import me.rockintuna.sailinglog.model.Account;
import me.rockintuna.sailinglog.model.UserDetailsImpl;
import me.rockintuna.sailinglog.repository.AccountRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class Oauth2Service {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    private String accessToken;

    public void login(String code) throws JsonProcessingException {
        accessToken = requestAccessToken(code);
        KakaoAccountDto kakaoAccountDto = requestKakaoAccountDto();
        Account account = getKakaoAccountBy(kakaoAccountDto);
        forceLogin(account);
    }

    private String requestAccessToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "4afb46d8769cc2922b6999a892fc1646");
        body.add("redirect_uri", "http://localhost:8080/account/oauth/callback");
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                request,
                String.class
        );
        return getAccessTokenFrom(response);
    }

    private String getAccessTokenFrom(ResponseEntity<String> response) throws JsonProcessingException {
        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private KakaoAccountDto requestKakaoAccountDto() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                request,
                String.class
        );
        return getKakaoAccountDtoFrom(response);
    }

    private KakaoAccountDto getKakaoAccountDtoFrom(ResponseEntity<String> response) throws JsonProcessingException {
        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        return KakaoAccountDto.of(id, nickname);
    }

    private Account getKakaoAccountBy(KakaoAccountDto kakaoAccountDto) {
        Account account = getAccountByKakaoId(kakaoAccountDto.getId());
        if ( account == null ) {
            account = registerKakaoAccount(kakaoAccountDto);
        }
        return account;
    }

    private Account getAccountByKakaoId(Long kakaoId) {
        return accountRepository.findByKakaoId(kakaoId).orElse(null);
    }

    private Account registerKakaoAccount(KakaoAccountDto kakaoAccountDto) {
        kakaoAccountDto.setPassword(passwordEncoder.encode(kakaoAccountDto.getPassword()));
        Account account = Account.from(kakaoAccountDto);
        return accountRepository.save(account);
    }

    private void forceLogin(Account account) {
        UserDetails userDetails = UserDetailsImpl.of(account);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
