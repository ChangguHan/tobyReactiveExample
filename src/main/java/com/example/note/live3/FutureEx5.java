package com.example.note.live3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import lombok.extern.slf4j.Slf4j;

/*
이걸 스프링에서는 어떠헥 할것인가?

 */


@Slf4j
//@SpringBootApplication
//@EnableAsync
public class FutureEx5 {

    @Component
    public static class MyService {
        /*
        Spring 안에서 자동으로 비동기작업이 됨
        다른 스레드에서 비동기 수행되니까, 결과를 바로 작업을 가져올수 없음
        그래서 Future, Callback  으로 가져옴

        Async 사용하면, SimpleAsyncTaskExecutor
        스레드 개수만큼 만들어서 사용, 실습용으로만 사용
        실전에서는 아래 Bean 만들기

        setCorePool
        queue: 당작 돌려줄 스레드없어서 대기하는곳
        setCorePoolSize(10); // 1.10개 스레드가 다 사용되면
        setQueueCapacity(200); // 2.큐에 먼저참, 3. 큐가 다차면
        setMaxPoolSize(100); // 4. Max Pool 까지 늘려



         */

        @Bean
        ThreadPoolTaskExecutor tp() {
            ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
            te.setCorePoolSize(10);
            te.initialize();
            return te;
        }
        @Async
        public ListenableFuture<String> hello() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(1000);
            return new AsyncResult<>("Hello");
        }
    }
    public static void main(String[] args) {

        try (ConfigurableApplicationContext c = SpringApplication.run(FutureEx5.class, args)) {


        }

    }

    @Autowired MyService myService;

    @Bean
    ApplicationRunner run() {
        return args -> {
            log.info("run");
            ListenableFuture<String> res = myService.hello(); // hello라는 오래걸리는 작업을 수행하고
            res.addCallback(s -> System.out.println(s), e -> System.out.println(e.getMessage()));
            log.info("exit{}", res);
            res.get();
        };
    }
    /*
    아까와 비슷한데, 자바에서 젝오하는 es, shutdown 등의 이런 코드들이 사라짐
    비동기 거는 큰 이유는, 오랜시간의 장시간의 작업을 시작하는경우
    클라이언트에서 액션으로 실행될수 있게

    이때,방법으로
     결과를 get()으로 받지 않고 DB에 넣음
     Future라는 핸들러를, future에 저장하고 리터을 해버림
     다음 컨트롤러 세션에서 future 넣은것을 호출해보는거지

    자바5의 concurrent 이전부터 이것을 지원했었음

    콜백 퓨처 테스크처럼, 작업 수행하고 리스너로 수행하고싶다
    그럼 스프링이 만들어놓은 Listenable Future을 사용
    Listenable Future: 리스너가 서브젝트에게 자기 등록하고 받아보는것처럼
    콜백을 넣어서 수행 가능



     */
}
