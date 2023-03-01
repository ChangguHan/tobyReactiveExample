package com.example.note.live3;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import lombok.extern.slf4j.Slf4j;

/*
          또하나의방식 : 콜백,
          - Future를 Object로 만ㄷ
          - Future Object 와 비동기 콜백이 하나에 다 들어감

 */
@Slf4j
public class FutureEx2 {
    public static void main(String[] args) throws InterruptedException, ExecutionException {


        ExecutorService es = Executors.newCachedThreadPool();

        FutureTask<String> f = new FutureTask<String>(() -> {
            Thread.sleep(2000);            // Interrupt exception: 쉬고있는 도중, 누군가가 너 일어나, 그만해 라는 스레드를 보낼수있는데, 그떄 익셉션
            System.out.println("Async");
            return "Hello";
        }) {
            @Override
            protected void done() {
                // 비동기 작업 완료되면 호출
                try {
                    System.out.println(get());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        /*
        FutureTask도 최근에 나온게 아니라 1.5부터 들어옴
        콜백과 같은 기법 활용하기 위한 기법이 다 들어가 있음
        직접 콜백으로 자바 비동기 처리할수 있는방법
         */

        es.execute(f);
        es.shutdown(); // 이 스레드 풀 자체를 종료해라, 비동기작업 바볼 끝나진 않음, 아닐경우 es 가 하나의 서비스로 떠있어서 종료되지 않음.


        /*

         */



    }
}
