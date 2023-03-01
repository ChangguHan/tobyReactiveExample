package com.example.note.live11;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/*
로그
onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription) // 스프링이 해줌
request(unbounded)
onNext(HELLO WEBFLUX)
onComplete()
 */

@Slf4j
@SpringBootApplication
@RestController
@EnableAsync
public class WebFluxEx2 {
    @GetMapping("/")
    public Mono<String> hello() throws InterruptedException {
        log.info("1");
        Mono m = Mono.just("HELLO WEBFLUX").log(); // 1,2 로그 모두 찍힌 다음 찍힘, 이건 퍼블리셔만 생성해줌
        log.info("2");
        return m;
    }

    @GetMapping("/2")
    public Mono<String> hello2() throws InterruptedException {
        String s = generateHello();
        Mono m = Mono.just(s).log(); // just는 미리 준비된것을 사용하는 것, 여기서 s 먼저 실행하고, Mono 만들어서 리턴해줌
        return m;
    }

    // 이걸 콜백 스타일로 만들어서 나중에 넣어서사용할 수 있음
    @GetMapping("/3")
    public Mono<String> hello3() throws InterruptedException {
        Mono m = Mono.fromSupplier(this::generateHello)
                     .log();
        return m;
    }

    /*
    3번을 미리 subscribe 한뒤 리턴하면?,
    예상: 먼저 데이터 가져온뒤 더이상 동작하지 않을것같은데
    실제: 두번 리턴됨
    모노나 플럭스같은 퍼블리셔는 여러개의 섭스크라이버를 가질수 있음
    퍼블리셔는 두가지 타입이 있는데, 핫, 콜드
    - 콜드 : 섭스크라이브 들어올때마다 항상 다시 처음부터 데이터 공급해줌
    - 핫 : 이미 지나간건 더이상 공급해주지 않음
     */

    @GetMapping("/4")
    public Mono<String> hello4() throws InterruptedException {
        Mono<String> m = Mono.fromSupplier(this::generateHello)
                     .log();
        m.subscribe();
        return m;
    }
    // block(): 데이터 빼내기, 데이터 이름이 block()인 이유는 블럭킹해서 값을 가져오기 때문, 가능한 block()을 사용하지 않는게 좋음

    private String generateHello() {
        log.info("METHOD");
        return "HELLO MONO";
    }

    public static void main(String[] args) {
        System.setProperty("server.port", "8015");
        System.setProperty("server.tomcat.max_threads", "1000");
        SpringApplication.run(WebFluxEx2.class, args);
    }

}
