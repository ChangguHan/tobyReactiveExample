package com.example.note.live;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedulerEx {
    /*
    publishOn
    subscribeOn
    - 중간에 스케줄러를 넣는것, 오퍼레이터처럼 하나의 퍼클리케이터, 섭스크라이버를 끼워 넣는것
    - 하는 역할, 데이터 쏘기 전에, 섭스크라이버가 onsubscribe 넘겨주고 , request 넘기고, 이런 데이터를 쏘고 전달하는 모든 과정을
    - subscribeOn에서 지정한 곳에서 실행해달라고
    - 이 스케줄러르 통해서 윗부분이 동작하게
    - 이게 정리가 안되면 나중에 엄청 헷갈림
    
     */
    public static void main(String[] args) {

        Publisher<Integer> pub = sub -> {
            sub.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    log.debug("request");
                    sub.onNext(1);
                    sub.onNext(2);
                    sub.onNext(3);
                    sub.onNext(4);
                    sub.onNext(5);
                    sub.onComplete();
                }

                @Override
                public void cancel() {

                }
            });
        };

        // 시간순서에따라 비동기적으로, 여기가 생각해야할게 많음
        //
        Publisher<Integer> subOnPub = sub -> {
            // subscribe 이후 작읍을 새로운 스레드에서
            ExecutorService es = Executors.newSingleThreadExecutor(); // 코어스레드 1개, 맥시멈 1개, 그 이상요청하면 큐에서 대기
            // 한번에 하나의 스레드만 동작하도록 스레드풀

            /*
            main thread -> pool-1-thread-1 으로 변경됨

             */
            es.execute(() -> {
                pub.subscribe(sub);
            });
        };

        // 여기 최종 로그찍는 섭스크라이버
        //
        subOnPub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.debug("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.debug("onNext: {}", integer);
            }

            @Override
            public void onError(Throwable t) {
                log.debug("onError");
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        });

     // 여기에 exit  이라고 출력할경우 예상
     // exit main 스레드 먼저 ㄴ아ㅗ고 그 이후, thread pool 에서 출력
        log.debug("exit");

        /*
        사용ㅎ는 이유, publisher 가 느린경우
        publisher 에서 onNext 호출해서 데이터 전달해주는데, 이걸 모두 다른 스레드에서 해줌
        근데 이걸 처리해주는 건 빠르다
        이건 또 무슨의미, onNext()를 실행은 빠른데, 같은 스레드에서 실행되는거 아닌가?
        >  dlrj ckwdkqhrl

         */


        /*
        request는 언제, 어디에서 돌아갈까
        thread1 - onSubscribe
        thread1 - request
        thread1 - onNext
         */

        /*
        반대의 경우를 생각해보자
        publishOn은 publish까지는 빠른데, sub에서 느린경우, 별도의
         */
    }
}
