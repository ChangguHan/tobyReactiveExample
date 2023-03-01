package com.example.note.live3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/*
부하테스트
- 100개의 요청을 동시에 하는것

 */

@Slf4j
public class FutureEx7 {

    static AtomicInteger counter = new AtomicInteger(0);
    static AtomicInteger counter2 = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(100);
        RestTemplate rt = new RestTemplate();

        /*
        callable > 2170ms
        Callable(Servlet 3.1)
        Servlet thread 가 정말 재사용됨
         */
        String url = "http://localhost:8012/callable";
//        String url = "http://localhost:8012/string";
        /*
        string  즉 일반  MVC 결과
        총 2300 ms
        controller, 모두 다른 thread에서 작업 진행
         */
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

/*
Visual VM으로 테스트해보자

톰캣 기본 스레드 100개 -> 20개로 제한할경우 시간이 5배로 늘어남
스레드가 20개밖에 안만들어졌지만, 나머지 80개는 큐에서 대기하고 있었던것ㅇ

워커스레드가 엄청 생겼다가 줄어듬
근데 이게 스레드 총량적으로 보면 차이가 없음
빠르게 처리해야할작업들은 작업 스레드풀 거치지 않고서야는 큰 의미가 ㅇ벗음

워커 스레드 더이상 만들지 않고 비동기 작업 진행방법 있음
DeferredResult 큐


 */