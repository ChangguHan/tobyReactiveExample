package com.example.tobytv_reactive_organized.live3;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import lombok.extern.slf4j.Slf4j;

/*
ResponseBodyEmitter
- HTTP 안에 데이터를 여러번에 나눠서 보냄
- 한번 요청에 여러번 응답을 보냄
- SSE표준으로 데이터를 스트림 방식으로 전송
 */
@Slf4j
@SpringBootApplication
@EnableAsync
public class C4_ResponseBodyEmitter {
    @RestController
    public static class MyController {
        Queue<DeferredResult<String>> q = new ConcurrentLinkedDeque<>();

        @GetMapping("/emitter")
        public ResponseBodyEmitter emitter() {
            ResponseBodyEmitter emitter = new ResponseBodyEmitter();
            // emitter  먼저 리턴하고, 오래걸리는 작업을 아래 다른 워커에서 진행
            Executors.newSingleThreadExecutor().submit(() -> {
                for(int i=1; i<=50; i++) {
                    try {
                        emitter.send("<p>Stream " + i + "</p>");
                        Thread.sleep(500);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return emitter;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.setProperty("server.port", "8013");
        System.setProperty("server.tomcat.max_threads", "1"); // 톰켓 스레드 1개로 제한
        SpringApplication.run(C4_ResponseBodyEmitter.class, args);
    }
}