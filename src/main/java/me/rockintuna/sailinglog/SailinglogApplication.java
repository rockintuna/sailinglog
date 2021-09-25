package me.rockintuna.sailinglog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
public class SailinglogApplication {

    @PostConstruct
    void started() {
        System.out.println("Default Timezone :" + TimeZone.getDefault().getID());
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        System.out.println("New Default Timezone :" + TimeZone.getDefault().getID());
    }

    public static void main(String[] args) {
        SpringApplication.run(SailinglogApplication.class, args);
    }

}
