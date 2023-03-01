package com.example.note.live11;

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

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/*
WebFlux
-

Mono
- 결과를 한번에 리턴
- webflux가 mono를 베이스로 진행함

Netty
- 스프링 2.0부터 webflux 사용하는 경우 네티가 기본

Webclient
- AsyncRestTemplate과 유사

리액티브
- 계속해서 받아오는것
- 이벤트 드라이븐
- 리스트에 담아서 넘긴다

 */




@Slf4j
@SpringBootApplication
@RestController
@EnableAsync
public class WebFluxEx {
    static final String URL1 = "http://localhost:8013/service?req={req}";
    static final String URL2 = "http://localhost:8013/service2?req={req}";

    @Autowired
    MyService myservice;
    WebClient client = WebClient.create();
    @GetMapping("/rest")
    public Mono<String> rest(int idx) throws InterruptedException {
        log.info(String.valueOf(idx));
        Mono<ClientResponse> res = client.get().uri(URL1, idx).exchange(); // 정의하는것만으로는 호출되지 않음, publisher이므로 subscribe 해야함,
        // subscribe 할필요없이 리턴해주면 알아서 subscribe 호출

        // ClientResponse 에서 body를 변경해주기, stream map처럼
        return res.flatMap(c -> c.bodyToMono(String.class));
    }

    // 값을 받은 후, 다시한번 요청
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
    비동기로 작업하려면 @Async 와 COmpletableFuture로 리턴
    서비스에서 로그찍으면, 다른 워커? >  다른 워커에서 실행됨

     */
    @Service
    public static class MyService {
        @Async
        public CompletableFuture<String> work(String req) {return CompletableFuture.completedFuture(req + "/asyncwork");}
    }
}
