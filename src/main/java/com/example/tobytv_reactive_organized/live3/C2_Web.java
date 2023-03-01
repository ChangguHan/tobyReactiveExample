package com.example.tobytv_reactive_organized.live3;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/*
Callable로 리턴할경우, 스프링이 이것을 별도의 스레드 안에서 비동기적으로 실행시켜줌

로드테스트 100개로 확인해보면
- 전체 2초밖에 안걸림
- 스레드 보면, 콤캣 스레드는 1개만 생성되나,MvcAsync 스레드가 100개로 만들어짐
- 즉 서블릿 스레드는 1개로 돌려쓰지만, 뒤에 작업스레드가 생성된것 > 어플리케이션 전체적으로 큰 의미 없음
 */
@Slf4j
@SpringBootApplication
@EnableAsync
public class C2_Web {
    @RestController
    public static class MyController {
        @GetMapping("/callable")
        public Callable<String> callable() throws InterruptedException {
            log.info("callable"); // 톰켓의 서블릿 스레드, 실행하고 바로 리턴함
            return () -> {
                log.info("async"); // 다른 스레드에서 실행됨
                Thread.sleep(2000);
                return "hello";
            };
        }

        public static void main(String[] args) throws ExecutionException, InterruptedException {
            System.setProperty("server.port", "8013");
            System.setProperty("server.tomcat.max_threads", "1"); // 톰켓 스레드 1개로 제한
            SpringApplication.run(C2_Web.class, args);
        }
    }
}