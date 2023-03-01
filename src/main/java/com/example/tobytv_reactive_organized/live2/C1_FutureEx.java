package com.example.tobytv_reactive_organized.live2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lombok.extern.slf4j.Slf4j;

/*
자바 Future
- 자바 1.5부터 등장
- 비동기 작업 수행한 결과를 나타내는것
- 다른 스레드의 결과를 가져와야하는데, 그 방법이 Future

ThreadPool
- 스레드를 새로 생성하는 비용이 크기때문에
- 미리 생성해놓고 사용하고 반납하는 풀
- 이를 사용하기 위해 대기하는 큐도 같이 항상 존재
- newCachedThreadPool: 초기 0, 코어 0, 최대 integer.MAX_VALUE, 60초동안 작업하지 않으면 스레드풀에서 제거
- newFixedThreadPool: 초기 0, 코어 n, 최대 n
- 코어 스레드수: 한번 증가한 이후 유지해야하는 최소 스레드수
https://m.blog.naver.com/mals93/220743747346
 */
@Slf4j
public class C1_FutureEx {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();
        Future<String> f = es.submit(() -> {
            Thread.sleep(2000);
            log.info("Async");
            return "Hello";
        });

        log.info(f.get()); // get() 만날경우, 결과 리턴될때까지 블럭킹 상태
        log.info("EXIT");
        es.shutdown();
    }
}