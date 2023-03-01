package com.example.note.live9_ayncresttemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;

import lombok.extern.slf4j.Slf4j;

/*
AsyncRestTemplate
- BLocking 방식이 아님
- Spring 4.0에서 등장
- RestTemplate의 비동기 버전

원리
- 톰캣 스레드는 여전히 하나인데
- 스프링의 AsyncTemplate : 비동기로 하는건 맞는데, 백그라운드에 스레드를 하나씩 만듬
- 자바의 기본 API를 사용하는것, 일시적이지만 스레드 100개를 별도 만드는건 큰 비용
- 원하는것은 스레드를 만들지 않고 진백그라운드에





 */

@Slf4j
@SpringBootApplication
@EnableAsync
public class MVCBasic {

    @RestController
    public static class MyController {
        AsyncRestTemplate rt = new AsyncRestTemplate(); //
        @GetMapping("/rest")
        public ListenableFuture<ResponseEntity<String>> rest(int idx){
            String url = "http://localhost:8013/service?req={req}";

            // ResponseEntity : Header, 응답코드, 바디 세가지를 다가지고 있음
            // ListenableFuture: 비동기 작업 수행 결과를 가지고있는데, 콜백을 등록할 수 있음
            // 이렇게 Listenable 리턴하면, 바로 리턴하는데, Spring MVC에서 알아서 2초 기다려서 응답해줌
            return rt.getForEntity(url, String.class, "hello " + idx);// getForEntity : Header까지 같이 리턴
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MVCBasic.class, args);
    }
}
