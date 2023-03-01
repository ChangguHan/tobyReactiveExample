package com.example.tobytv_reactive_organized.live5_WebFlux;

import java.time.Duration;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

// Flux: 데이터 여러개 보냄
@SpringBootApplication
@RestController
@Slf4j
@EnableAsync
public class C2_WebFlux3 {

    // 로그: request() -> onNext(1) -> onNext(2) -> onComplete()
    @GetMapping("/events")
    public Flux<Event> events() {
        return Flux.just(getEvent(1), getEvent(2)).log();
    }

    // Stream to Flux
    @GetMapping("/events/1")
    public Flux<Event> events1() {
        Stream<Event> limit = Stream.generate(() -> getEvent(System.currentTimeMillis())).limit(10);
        return Flux.fromStream(limit).log();
    }

    // Zip : 두개의 플럭스를 1개씩 묶어서 연산해 하나로 합쳐줌
    @GetMapping(value="/events/2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Event> events2() {
        // generate() : 인자1 : 초기값, 인자2 : Flux에 담을것은 sink에, 리턴값은 다음 id로
        Flux<Event> es = Flux.generate(() -> 1, (id, sink) -> {
            sink.next(getEvent(id));
            return id+1;
        });
        Flux<Long> interval = Flux.interval(Duration.ofMillis(300));

        // 이러면 하나씩 onNext 하는 과정에서 인터벌 생겨서 시간 지연할수있음
        return Flux.zip(es, interval).map(tu -> tu.getT1());
    }



    private Event getEvent(long id) {
        return new Event(id, "event - " + id);
    }

    @Data
    @AllArgsConstructor
    public static class Event {
        long id;
        String value;
    }

    public static void main(String[] args) {
        System.setProperty("server.port", "8015");
        System.setProperty("server.tomcat.max_threads", "1000");
        SpringApplication.run(C2_WebFlux3.class, args);
    }
}
