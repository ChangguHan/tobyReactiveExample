//package com.example.tobytvreactive.live3;
//
//import java.util.Queue;
//import java.util.concurrent.ConcurrentLinkedDeque;
//import java.util.concurrent.Executors;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.context.request.async.DeferredResult;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
//
//import lombok.extern.slf4j.Slf4j;
//
///*
//ResponseBodyEmitter
//- HTTP 안에 데이터를 여러번에 나눠서 보냄
//- 한번 요청에 여러번 응답을 보냄
// */
//
//@Slf4j
//@SpringBootApplication
//@EnableAsync
//public class FutureEx_DR2_WEB {
//
//    @RestController
//    public static class MyController {
//        Queue<DeferredResult<String>> q = new ConcurrentLinkedDeque<>();
//
//        @GetMapping("/emitter")
//        public ResponseBodyEmitter emitter() throws InterruptedException {
//            ResponseBodyEmitter emitter = new ResponseBodyEmitter();
//            Executors.newSingleThreadExecutor().submit(() -> {
//                try {
//                    for (int i = 0; i < 50; i++) {
//                        emitter.send("<p>Stream : "+i+" </p>");
//                        Thread.sleep(1000);
//                    }
//                } catch(Exception e) {
//
//                }
//            });
//            return emitter;
//        }
//        /*
//        겨로가를 한번에 쏘는게 아니라
//        SSE 표준을 따라서 데이터를 스트리밍 방식으로 리스펀스
//        복잡하지 않은 스트리밍 응답코드 만들수 있음
//
//         */
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(FutureEx_DR2_WEB.class, args);
//    }
//}
