package com.example.tobytv_reactive_organized.live2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import lombok.extern.slf4j.Slf4j;

/*
자바 FutureTask
- 콜백 작업 수행
 */
@Slf4j
public class C2_FutureEx {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();
        FutureTask<String> f = new FutureTask<>(() -> {
            Thread.sleep(2000);
            log.info("FutureTask");
            return "Hello";
        }) {
            @Override
            protected void done() {
                try {
                    log.info(get());
                    log.info("CALL BACK");
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        es.execute(f);
        es.shutdown();
    }
}
/*
맘에 안드는것
- 비동기 작업에 필요한 기술적코드(ExecutorService, execute, shutdown)와 비즈니스 코드가 섞여있음 > 비즈니스코드만 보고싶음
- 스프링을 통해서 이를 분리

 */