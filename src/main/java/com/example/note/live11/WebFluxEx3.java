package com.example.note.live11;

import java.time.Duration;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
Flux : 데이터가 여러개 갈수 있는것

 */
@Slf4j
@SpringBootApplication
@RestController
@EnableAsync
public class WebFluxEx3 {
    @GetMapping("/event/{id}")
    public Mono<Event> hello(@PathVariable long id) throws InterruptedException {
        return Mono.just(new Event(id, "event - " + id));
    }

    /*
    로그 : onNext이 여러개 리턴됨
    onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
request(unbounded)
onNext(WebFluxEx3.Event(id=1, value=event - 1))
onNext(WebFluxEx3.Event(id=2, value=event - 2))
onComplete()

Flux<Event> vs Mono<List<Event>>
-
     */
    @GetMapping("/events")
    public Flux<Event> hello2() throws InterruptedException {
        return Flux.just(getEvent(1), getEvent(2)).log();
    }

    /*
    데이터를 10개정



     */
    @GetMapping(value = "/events/1"
//            , produces = MediaType.TEXT_EVENT_STREAM_VALUE) // stream으로 계속 내려감
    )
    public Flux<Event> hello3() throws InterruptedException {
        Stream<Event> generate = Stream.generate(() -> getEvent(System.currentTimeMillis()))
                .limit(10);
        return Flux.fromStream(generate).log();
    }

    @GetMapping(value = "/events/2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Event> testGeneratate() throws InterruptedException {
        Flux<Event> es = Flux.generate(() -> 1, (id, sink) -> {
            sink.next(getEvent(id + 1));
            return id + 1;
        });
        Flux<Long> interval = Flux.interval(Duration.ofMillis(300));
        // zip : 한개씩 양쪽거를 묶어줌,
        return Flux.zip(es, interval).map(tup -> tup.getT1());
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
        SpringApplication.run(WebFluxEx3.class, args);
    }

}
