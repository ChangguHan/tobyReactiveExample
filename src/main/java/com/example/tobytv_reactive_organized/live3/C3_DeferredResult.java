package com.example.tobytv_reactive_organized.live3;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;

/*
DeferredResult
- 워커스레드를 많이 만들지 않고 처리할수 있음
- 처음 만들어졌을때 대기상태에 있다가, 결과값이 들어가면, 리턴해줌
- 특징 : 워커스레드가 따로 만들어지지 않음, 메모리에 객체만 만들어져있으면 불러와서 결과를 불러주면됨
- 로드 테스트 결과: 이벤트 1개로 백개에 한번에 응답줄수 있고, 스레드도 하나만 생김
- 원리: 큐에 저장했다가, 해당 결과오면 리턴해줌, 계속 폴링하는게 아님
https://stackoverflow.com/questions/15357990/understanding-the-spring-mvcs-deferredresult-class-in-the-context-of-the-spring
 */
@Slf4j
@SpringBootApplication
@EnableAsync
public class C3_DeferredResult {
    @RestController
    public static class MyController {
        Queue<DeferredResult<String>> q = new ConcurrentLinkedDeque<>();

        @GetMapping("/dr")
        public DeferredResult<String> dr() {
            log.info("dr");
            DeferredResult<String> dr = new DeferredResult<>();
            q.add(dr);
            return dr;
        }

        @GetMapping("/dr/count")
        public String drCount() {
            return String.valueOf(q.size());
        }

        @GetMapping("/dr/event")
        public String drEvent(String msg) {
            for(DeferredResult<String> dr: q) {
                dr.setResult("Hello : " + msg);
                q.remove(dr);
            }
            return "OK";
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.setProperty("server.port", "8013");
        System.setProperty("server.tomcat.max_threads", "1"); // 톰켓 스레드 1개로 제한
        SpringApplication.run(C3_DeferredResult.class, args);
    }
}