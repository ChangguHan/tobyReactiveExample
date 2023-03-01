package com.example.tobytv_reactive_organized.live5_WebFlux;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.note.live11.WebFluxEx;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/*
Mono: 결과를 한번에 리턴

리액티브: 좀더 빠르게 응답, 이벤트 드라이븐
 */
@SpringBootApplication
@RestController
@Slf4j
@EnableAsync
public class C1_WebFlux {
    static final String URL1 = "http://localhost:8013/service?req={req}";
    static final String URL2 = "http://localhost:8013/service2?req={req}";

    @Autowired
    MyService myservice;

    /*
    WebClient: 논블록킹 비동기 가능
     */
    WebClient client = WebClient.create();

    @GetMapping("/rest")
    public Mono<String> rest(int idx) throws InterruptedException {
        log.info(String.valueOf(idx));
        // 정의하는것만으로는 호출되지 않음, publisher이므로 subscribe 해야함,
        // subscribe 할필요없이 리턴해주면 스프링이 요청받으면 알아서 subscribe 호출
        Mono<ClientResponse> res = client.get().uri(URL1, idx).exchange();

        // ClientResponse 에서 body를 변경해주기, stream의 map처럼
        return res.flatMap(c -> c.bodyToMono(String.class));
    }

    // URL1으로부터 값 받은후, 2로 다시한번 요청
    @GetMapping("/rest2")
    public Mono<String> rest2(int idx) throws InterruptedException {
        log.info(String.valueOf(idx));
        return client.get()
                     .uri(URL1, idx)
                     .exchange()
                     .flatMap(c -> c.bodyToMono(String.class))
                     .flatMap(c -> client.get().uri(URL2, c).exchange())
                     .flatMap(c2 -> c2.bodyToMono(String.class))
                     .flatMap(res2 -> Mono.fromCompletionStage(myservice.work(res2)));
    }

    public static void main(String[] args) {
        System.setProperty("server.port", "8015");
        System.setProperty("server.tomcat.max_threads", "1000");
        SpringApplication.run(WebFluxEx.class, args);
    }

    /*
    비동기로 작업하려면 @Async 와 COmpletableFuture 사용, 다른 워커에서 실행됨
     */
    @Service
    public static class MyService {
        @Async
        public CompletableFuture<String> work(String req) {return CompletableFuture.completedFuture(req + "/asyncwork");}
    }
}
