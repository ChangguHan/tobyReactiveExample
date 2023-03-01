package com.example.note.live3;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lombok.extern.slf4j.Slf4j;

/*
10년전에 스프링이 비동기작업을 녹여냄
지속적으로 비동기 개발과 관련기능 추가도미

자바의 비동기 지원기술을 스프링 개발에 녹여냄
리액티브라는것을 도입하는 차이점

옛날방식의 비동기 부터 시작 > 자바의 Future
자바의 Future
- 자바 1.5에서 나옴
- 비동기적인 작업 수행한결과를 나타내는것,
- 새로운 스레드에서 별개의 작업 실행
- 다른 스레드의 결과를 다른 방법으로 가져와야하는데, 그 방법이 Future

 */
@Slf4j
public class FutureEx {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        //         스레드풀 : 내가 원하는 스레드가 있어, 스레드 하나 새로 만들어서 사용하면 되는데
        // 새로만들고 사용하는게 큰 비용
        // 그래서 스레드를 천번 사용해야하는데, 동시에는 10개씩밖에 안쓰겠다고 하면
        // 10개만 만들어서 사용하고 날리지 않고, 풀에 저장하고 다시 사용하고
        //

        ExecutorService es = Executors.newCachedThreadPool();
        // newCachedThreadPool : 맥시멈 제한이 없고, 처음엔 하나도 만들어져있지 안으나
        // 요청할떄마다 없으면 만들게됨,

        // submit : 값을 리턴할 수 있음
        // 이걸 호출한, 메인스레드에서 이값을 가져올때 사용 > Future
        Future<String> f = es.submit(() -> {
            Thread.sleep(2000);            // Interrupt exception: 쉬고있는 도중, 누군가가 너 일어나, 그만해 라는 스레드를 보낼수있는데, 그떄 익셉션
            System.out.println("Async");
            return "Hello";
        });

        log.info(f.get());

        System.out.println("Exit");
        /*
          이렇게 실행하면, Hello -> Exit 으로 출력됨
          future의 get은 결과가 나올때까지 블럭킹한 상태
          f.get(): 블록킹, 바로 리턴이 되면 넌 블럭킹

          비동기작업 대부분에 별개 스레드 만들어서 시랳ㅇ이되고, 리턴은 다른 방식으로 가져옴
          Object를 이용하는것,
          또하나의방식 : 콜백,
          - Future를 Object로 만ㄷ

         */

    }
}
