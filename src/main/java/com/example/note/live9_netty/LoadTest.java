package com.example.note.live9_netty;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadTest {

    static AtomicInteger counter = new AtomicInteger(0);
    static AtomicInteger counter2 = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        ExecutorService es = Executors.newFixedThreadPool(100);
        RestTemplate rt = new RestTemplate();

        String url = "http://localhost:8012/rest2?idx={idx}"; // 스레드 풀 1개로 제한
//        String url = "http://localhost:8012/string";

        CyclicBarrier barrier = new CyclicBarrier(100);// 동시에 요청을 날리고 싶을때
        StopWatch main = new StopWatch();

        main.start();
        for(int i=0; i<100; i++) {
            es.submit(() -> {
                var idx = counter.addAndGet(1);
                barrier.await(); //  여기에 100개까지 오면 딱 풀려버림
                log.info("Thread " + idx);
                StopWatch sw = new StopWatch();
                sw.start();
                String res = rt.getForObject(url, String.class, idx);
                sw.stop();
                log.info("Elapsed: {}, {} / {}", idx, sw.getTotalTimeMillis(), res);
                return null;
            });
        }
        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS); // 바로 빠져나가면 안되니까, 정해진타임아웃 걸리기 전이라면 그때까지는 기다림
        main.stop();
        log.info("Total: {}", main.getTotalTimeMillis());
        log.info("Total Error: {}", counter2.get());
    }
}