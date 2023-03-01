package com.example.tobytv_reactive_organized.live5_WebFlux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class C2_WebFlux2 {

    // 결과: 1,2 로그 모두 다 찍힌다음, Mono 로그 발생
    @GetMapping("/")
    public Mono<String> hello() {
        log.info("1");
        Mono m = Mono.just("HELLO WEBFLUX").log();
        log.info("2");
        return m;
    }

    // just는 미리 준비되는것을 사용하는것이라, s먼저 가져옴
    @GetMapping("/2")
    public Mono<String> hello2() {
        String s = generateHello();
        Mono m = Mono.just(s).log();
        return m;
    }

    // 먼저 generateHello()실행하지 않고, 요청이오면 그때 실행
    @GetMapping("/3")
    public Mono<String> hello3() {
        Mono m = Mono.fromSupplier(this::generateHello).log();
        return m;
    }

    // 각각 로그가 찍히게됨 > 두번 subscribeehla
    // Mono, Flux는 여러개의 섭스크라이버를 가질수있음
    @GetMapping("/4") // 다른곳에서 섭스크라입 하고 다시 리턴하면?
    public Mono<String> hello4() {
        Mono m = Mono.fromSupplier(this::generateHello).log();
        m.subscribe();
        return m;
    }

    private String generateHello() {
        log.info("METHOD");
        return "HELLO MONO";
    }


    public static void main(String[] args) {
        System.setProperty("server.port", "8015");
        System.setProperty("server.tomcat.max_threads", "1000");
        SpringApplication.run(WebFluxEx.class, args);
    }
}
