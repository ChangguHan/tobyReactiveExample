package com.example.note.live9;

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

        String url = "http://localhost:8012/rest?idx={idx}"; // 스레드 풀 1개로 제한
//        String url = "http://localhost:8012/string";

        CyclicBarrier barrier = new CyclicBarrier(100);// 동시에 요청을 날리고 싶을때
        StopWatch main = new StopWatch();

        main.start();
        for(int i=0; i<100; i++) {
            es.submit(() -> {
                var idx = counter.addAndGet(1);
                barrier.await(); //  여기에 100개까지 오면 딱 풀려버림
                // execute는 runnable은 exception 던질방법이 없어서 callable하는 인터페이스로 바꿈, callable은 리턴값이 있고, 익셉션 선언됨
                log.info("Thread " + idx);
                StopWatch sw = new StopWatch();
                sw.start();
                String res = rt.getForObject(url, String.class, idx);
                sw.stop();
                log.info("Elapsed: {}, {} / {}", idx, sw.getTotalTimeMillis(), res);
                return null;
            });
        }
//        barrier.await();
        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS); // 바로 빠져나가면 안되니까, 정해진타임아웃 걸리기 전이라면 그때까지는 기다림
        main.stop();
        log.info("Total: {}", main.getTotalTimeMillis());
        log.info("Total Error: {}", counter2.get());

    }
}

/*
예상결과
순차적으로 1개의 스레드에서 100번 진행
그래서 100번 다른 서비스로 요청보낼것임

Service 에서, Sleep 할경우, Basic 에서 병목현상 발생

해결방법 : APi호출하는 작업을 비동기적으로 바꾸기
API 응답 받기전에, 해당 요청을 바로 리턴함
그리고 다음 요청을 처리하는데 이용
다음 결과가 오면, 응답에 실어주는건 스레드할당을 받아야하긴함

DeferredResult로는 해결할수없음
- 외부의 별개의 이벤트를 다시 호출해줘야함

Callable도 해결불가
background에 워킹스레드 만들어야함
서블릿 스레드는 반환하지만 워킹스레드는 동시에 백갬가 만들어질수있음

이런경우 위해 AsyncRestTemplate 사용



 */