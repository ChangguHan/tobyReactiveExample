package com.example.note.live3;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;

/*
웹 애플리케이션을 만들어보자

비동기 서블릿
서블릿 3.0
현재 3.1이 최신

서블릿 3.0나오면서 비동기 서블릿
비동기적으로 서블릿 요청을 추가
서블릿은 모두 블로킹 구조
IO 작업이 일어나느것 따라서 스레드마다 하나씩 할당
커넥션 100개라면, 스레드 100개
HTTP 커넥션이 Inputstream, outputstream이 기본적으로 블로킹
스레드가 블로킹하는 상황은 Context Switch 되어, 자원을 많이 소모
블로킹 걸면, 블록 되는순간에 웨이팅 상태로 빠지고
다시 정상적인 러닝 스레드 되면서 2번씩 이렁남
블로킹 많이 사용하면 불필요한 소모가 많아짐

자바 인풋, 아웃풋 스트림ㅇㄴ 블로킹 방식
HTTP 서블릿 리스폰스는 인풋 스트림 베이스
서블릿은 블로킹 IO방식

서블릿 3.0이전에는 논블러킹 IO로 커넥션 처리
스레드가 각각커넥션마다 생성되는게 아니라, 톰캣에서 NIO가 다 잡고,
커넥션 실행되는 시간에 스레드를 다 만들어줘야함

일반ㄴ적으로
req -> logic -> res에서
이게 빈번하게 발생하면 문제될게 없는데
logic에서 blocking IO가 발생하는게 문제, 이떄 CS가 발생하는거고
스레드 개수가 다섯개라면, 풀 더만들지 못하니까 큐에 꽉차고
큐도 꽉차면 서비스 더이상 수행 불가
0.1초안에 응답할수있는건데 무한히 대기 할수있는거지
레이턴시가 꽉차는 순간, 큐에서 대기
클라이언트는 계속 응답을 대기

이게 CPU를 많이 써서 해야하는 작업이면 괜찮은데
블로킹 호출하는 작업이라던가, 쓸데없이 대기하고 있다는것

별도의 워커 스레드를 해서 지금 문제
서블릿 스레드 -> req -> WorkThread -> res
톰캣 기본 스레드 풀 사이즈 200개
200개 이상이라면 대기상태에 빠지게됨
어느순간 피크되어 팍 터지면 응답속도 현저하게 떨어짐

하고싶은것
서블릿스레드 입장에서 작업안하면, 풀에다가 반환 > 비동기 서블릿
이게 서블릿 3.0에서 가능하지만, 약간 부족
서블릿 스레드를 어떻게 효과적으로 쓰느냐는 제공
IO작업이 블로킹이어서, 작업 끝나고 서블릿을 반납함
논블록킹IO : 콜백방식으로 처리하게된것
스프링을 쓰면서 이것을 활용해야함

비동기 서블릿(3.1) 작업 수행
NIO Connector가 요청을 계속 받음
풀에서 이 서블릿 스레드를 가져옴
비동기로 작업 스레드를 실행시키고, 이 서블릿 스레드는 서블릿 스레드 풀에 반납
HTTP는 응답이 와야되는데, 응답을 처리하는 곳은 어디? > 비동기 서블릿 엔진 자체가 작업 스레드가 요청을 넘기는 시점에서
스레드풀에서 다시 스레드를 할당해서, 물고있는 커넥터에 응답처리해줌




 */

@Slf4j
@SpringBootApplication
@EnableAsync
public class FutureEx6 {

    @RestController
    public static class MyController {
        /*
        지금 스프링 MVC에서 서블릿 3.1을 사용하는 방법을 보여주고있음

         */
        @GetMapping("/callable")
        public Callable<String> callable() throws InterruptedException {
            /*
            비동기, 즉 작업 스레들에서 별도로 작업하고, 서블릿 스레드는 반환하고 싶으면 Callable 붙이면 됨
드
             */
            log.info("callable"); // 톰켓의 서블릿 스레드
            return () -> {
                log.info("async"); // 다른 스레드에서 실행됨
                Thread.sleep(2000);
                return "hello";
            };
        }

        @GetMapping("/string")
        public String string() throws InterruptedException {
            log.info("async"); // 다른 스레드에서 실행됨
            Thread.sleep(2000);
            return "hello";
        }

        Queue<DeferredResult<String>> q = new ConcurrentLinkedDeque<>();

        @GetMapping("/dr")
        public DeferredResult<String> dr() throws InterruptedException {
            log.info("dr"); // 다른 스레드에서 실행됨
            DeferredResult<String> dr = new DeferredResult<>();
            q.add(dr);
            return dr;
        }

        @GetMapping("/dr/count")
        public String drCount() throws InterruptedException {
            return String.valueOf(q.size());
        }

        @GetMapping("/dr/event")
        public String drEvent(String msg) throws InterruptedException {
            for(DeferredResult<String> dr: q) {
                dr.setResult("Hello {}" + msg);
                q.remove(dr);
            }
            return "OK";
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(FutureEx6.class, args);
    }
}
