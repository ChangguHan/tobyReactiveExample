package com.example.note.live3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/*
부하테스트 : 100개의 요청

이때 count 날리면, 100개 로 찍혀있음
/event?msg=OO 날리면 100개 다 풀림
이게 일어나는 동안에
톰켓 ㅇ서블릿 스레드느 ㄴ1개만 만들어지고


 */

@Slf4j
public class FutureEx_DR_Main {

    static AtomicInteger counter = new AtomicInteger(0);
    static AtomicInteger counter2 = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(100);
        RestTemplate rt = new RestTemplate();

        String url = "http://localhost:8012/dr"; // 스레드 풀 1개로 제한
//        String url = "http://localhost:8012/string";
        StopWatch main = new StopWatch();

        main.start();
        for(int i=0; i<100; i++) {
            es.execute(() -> {
                var idx = counter.addAndGet(1);
                log.info("Thread " + idx);
                StopWatch sw = new StopWatch();
                sw.start();
                rt.getForObject(url, String.class);
                sw.stop();
                log.info("Elapsed: {}, {}", idx, sw.getTotalTimeMillis());
            });
        }
        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS); // 바로 빠져나가면 안되니까, 정해진타임아웃 걸리기 전이라면 그때까지는 기다림
        main.stop();
        log.info("Total: {}", main.getTotalTimeMillis());
        log.info("Total Error: {}", counter2.get());

    }
}