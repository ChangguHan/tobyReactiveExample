package com.example.note.live11;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableAsync
public class MVCBackground {

    @RestController
    public static class MyController {
        @GetMapping("/service")
        public String rest(String req) throws InterruptedException {
            Thread.sleep(2000);
            return req + "/service ";
        }

        @GetMapping("/service2")
        public String rest2(String req) throws InterruptedException {
            Thread.sleep(2000);
            return req + "/service2 ";
        }

    }

    public static void main(String[] args) {
        /*
        property override
         */
        System.setProperty("server.port", "8013");
        System.setProperty("server.tomcat.max_threads", "1000");


        SpringApplication.run(MVCBackground.class, args);
    }
}
