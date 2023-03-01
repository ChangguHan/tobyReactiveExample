//package com.example.tobytvreactive.live3;
//
//import java.util.Queue;
//import java.util.concurrent.ConcurrentLinkedDeque;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.context.request.async.DeferredResult;
//
//import lombok.extern.slf4j.Slf4j;
//
///*
//
//워커스레드가 엄청 생겼다가 줄어듬
//근데 이게 스레드 총량적으로 보면 차이가 없음
//빠르게 처리해야할작업들은 작업 스레드풀 거치지 않고서야는 큰 의미가 ㅇ벗음
//
//워커 스레드 더이상 만들지 않고 비동기 작업 진행방법 있음
//DeferredResult 큐
//스프링 비동기의 꽃, 다양한 응용이 됨, 스프링 3.2부터 나옴
//
//요청을 보냈을때, 시간이 걸리는 job을 수행하지 않을때 대기하다가, 이벤트 발생하면, 작업을 한번에 쏴주는것
//장점: 외부의 이벤트에 의해 지연되어있는 http 응답을 나중에 쏴줄수있음
//
//
//
//
// */
//@Slf4j
//@SpringBootApplication
//@EnableAsync
//public class FutureEx_DR_WEB {
//
//    @RestController
//    public static class MyController {
//        Queue<DeferredResult<String>> q = new ConcurrentLinkedDeque<>();
//
//        @GetMapping("/dr")
//        public DeferredResult<String> dr() throws InterruptedException {
//            log.info("dr"); // 다른 스레드에서 실행됨
//            DeferredResult<String> dr = new DeferredResult<>(600000L);
//            q.add(dr);
//            return dr;
//        }
//        /*
//        요청 보내면 답이 안옴
//        보낸 후에, event로 다시 요청보내면 답이옴
//
//        무슨일이 발생했냐면
//        Deferred Result에  setResult 호출되기전까지 응답하지 않고 반환
//        다만, result 는 바로 반환해서 count 는 출력됨
//        누군가가 event에 로 결과 써주면, 쓰는 순간에 바로 리턴
//
//        사용용도 : 채팅방
//        채팅방에서 background에서 DeferredResult 로 유지하고있으면
//        누군가 채팅을치면 상대방에게 result를 던져줌
//        그리고 다시 Deffered Result 상태로 들어가고
//
//        혹은 대기상테에서 있다가, 외부에서 호출되었을때도 유용
//
//        특징 : 워커스레드가 따로 만들어지지 않음
//        메모리에만 이씅면 됨
//        서블릿 자원을 최소화하며 동시에 수많은 요청을 처리하는데 편리
//
//
//         */
//
//        @GetMapping("/dr/count")
//        public String drCount() throws InterruptedException {
//            return String.valueOf(q.size());
//        }
//
//        @GetMapping("/dr/event")
//        public String drEvent(String msg) throws InterruptedException {
//            for(DeferredResult<String> dr: q) {
//                dr.setResult("Hello {}" + msg);
//                q.remove(dr);
//            }
//            return "OK";
//        }
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(FutureEx_DR_WEB.class, args);
//    }
//}
