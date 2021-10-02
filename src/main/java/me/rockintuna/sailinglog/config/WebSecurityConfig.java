package me.rockintuna.sailinglog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/articles/**").permitAll()
                    .antMatchers("/").permitAll()
                    .antMatchers("/images/**").permitAll()
                    .antMatchers("/css/**").permitAll()
                    .antMatchers("/account/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .csrf().ignoringAntMatchers("/account/**")
                .and()
                    .formLogin()
                    .loginPage("/account/login")
                    .loginProcessingUrl("/account/login")
                    .defaultSuccessUrl("/")
                    .failureUrl("/account/login?error")
                    .permitAll()
                .and()
                    .logout().logoutUrl("/account/logout")
                .and()
                    .httpBasic();
    }

}