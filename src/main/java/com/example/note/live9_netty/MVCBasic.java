package com.example.note.live9_netty;

import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

/*
콜백지옥 해결 > Completion


 */

@Slf4j
@SpringBootApplication
@EnableAsync
public class MVCBasic {

    @RestController
    public static class MyController {
        AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1))); //
        @GetMapping("/rest")
        public DeferredResult<String> rest(int idx){
            String url = "http://localhost:8013/service?req={req}";

            // ResponseEntity : Header, 응답코드, 바디 세가지를 다가지고 있음
            // ListenableFuture: 비동기 작업 수행 결과를 가지고있는데, 콜백을 등록할 수 있음
            // 이렇게 Listenable 리턴하면, 바로 리턴하는데, Spring MVC에서 알아서 2초 기다려서 응답해줌
            ListenableFuture<ResponseEntity<String>> forEntity = rt.getForEntity(url, String.class, "hello " + idx);// getForEntity : Header까지 같이 리턴

            // Listenable을 수정하려고 할때
            // Listenable을 get()으로 꺼내오면 의미가 없고 callback 을 넣어서 만들어주기
            // 스프링에 결과값 주는 방법 : DeferredResult(Object 만들어서 리턴하면, 언젠가 값을 써주면 응답으로 처리하겠다) > 그리고 이걸 응답해주면됨
            DeferredResult<String> dr = new DeferredResult<>();
            forEntity.addCallback(
                    s -> {
                        dr.setResult(s.getBody() + "/work");
                    }, e -> {
                        // 비동기 Callback일때는, 예외 전파해봤자 정확히 받을곳을 찾기가 어려움
                        dr.setErrorResult(e.getMessage());
                    });

            // 첫번쨰 결과값이 다른곳에 들어가야하는경우
            // 동시에 다 날리면 안되나? 그럼 어떻게 조합해서 클라이언트에게 최종적으로 리턴하는가
            return dr;
        }

        @GetMapping("/rest2")
        public DeferredResult<String> rest2(int idx){

            DeferredResult<String> dr = new DeferredResult<>();

            //
            Completion
                    .from(rt.getForEntity("http://localhost:8013/service?req={req}", String.class, "hello " + idx)) // 비동기로 만들어진 Completion,
                    .andAccept(s -> dr.setResult(s.getBody())); // consumer Interface, 실행만 하는것, 만들어진 결과를 받아서 람다식 안에 넣어줌
            return dr;
        }


    }

    /*
    Listenable Future 콜백 가져오는 작업을 재정의해주기위함
    - 첫번쨰 이후부터는, 의존성을 가지고 있음 > 이부분 신경쓰기
    -
     */
    public static class Completion {

        Consumer<ResponseEntity<String>> con;
        Completion next;

        public Completion() {}
        public Completion(Consumer<ResponseEntity<String>> con) {
            Completion c = new Completion(con);
        }

        public void andAccept(Consumer<ResponseEntity<String>> con) {
            Completion c = new Completion(con);
            this.next = c;
        }
        public static Completion from(ListenableFuture<ResponseEntity<String>> lf) {
            Completion c = new Completion();
            lf.addCallback(s->{
                c.complete(s);
            }, e->{
                c.error(e);
            });
            return c;
        }

        private void error(Throwable e) {
        }

        private void complete(ResponseEntity<String> s) {
            if(next != null) next.run(s);



        }

        void run(ResponseEntity<String> s) {
            if(con != null) con.accept(s);
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(MVCBasic.class, args);
    }
}

/*
Thread Pool 동작
- Queue > Max Pool Size
-
 */
