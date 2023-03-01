package com.example.note.live;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedulerEx2 {
    /*
        반대의 경우를 생각해보자
        publishOn은 publish까지는 빠른데, sub에서 느린경우, 별도의 스레드 빼서,
        onNext를 다른 스레드에서 진행


이건 명확하단말야, publishOn 이 publish 메인스레드, subscribe 는 별도의 스레드 따서 진행

subscribeOn이 subscribe 메인스레드, publish는 별도의 스레드 따서 진행
근데 publish가 진행되어야 onNext 가 처리되잖아

         */
    public static void main(String[] args) {

        Publisher<Integer> pub = sub -> {
            log.debug("subscribe");
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
        Publisher<Integer> pubOnPub = sub -> { // 하단의 subscriber
            log.debug("subscribe2");

            // 이걸 타고오는곳은 별개의 작업에서 실행하겠다는것
            // 그니까 onSubscribe 이후의 작업부터
            // 그럼 중개해주는 subscribe 을 만들고,
            ExecutorService es = Executors.newSingleThreadExecutor();


            pub.subscribe(new Subscriber<Integer>() {
                @Override
                public void onSubscribe(Subscription s) { // 상위에서 onSubscribe 실행해줄경우
                    log.debug("onSubscribe2");
                    sub.onSubscribe(s);
                }

                @Override
                public void onNext(Integer integer) {
                    log.debug("onNext2: {}", integer);
                    es.execute(() -> {
                        sub.onNext(integer);
                    });
                }

                @Override
                public void onError(Throwable t) {
                    log.debug("onError2");

                    es.execute(() -> {
                        sub.onError(t);
                    });

                }

                @Override
                public void onComplete() {
                    log.debug("onComplete2");

                    es.execute(() -> {
                        sub.onComplete();
                    });
                }
            });
        };

        // 여기 최종 로그찍는 섭스크라이버
        //
        pubOnPub.subscribe(new Subscriber<Integer>() {
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

        log.debug("exit");

    }
    /*
     예상결과
     main - subscribe2
     main - subscribe1
     main - onSubscribe2
     main - onSubscribe
     main - request
     main - onNext2 1
     main - onNext2 2
     main - onNext2 3
     main - onNext2 4
     main - onNext2 5
     main - onComplete2
     main - exit
     thread1 - onNext 1
     thread1 - onNext 2
     thread1 - onNext 3
     thread1 - onNext 4
     thread1 - onNext 5
     thread1 - onComplete


    요청이 동시에 날라와도 큐에 걸려있다가 실행이됨
    중요한것중 하나, 하나의 publisher 가 데이터 생성해서 던져주는것은 멀티스레드로 호출하지 않게 되어있음
    그니까 publisher가 onNext를 멀티스레드에서 하지 않고 단일 스레드에서 진행, 호출은 순서가 보장됨

    onPub : publish 할때(이 때가 중요, 위치가 아님) 별개의 스레드를 사용하게 하는것
    onSub : subscribe 할때ㅑ 별개의 스레드 사용

     */
}
