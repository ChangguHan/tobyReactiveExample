package com.example.note.live;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedulerEx3 {
    /*
        이걸 동시에 다걸어보자
        일단 효과부터 봅시다

        subOn: 그럼 subscribe 한 순간부터 다른 곳으로 뺌 > 다른 스레드, publisher가 느려도 main에 이상없음
        pubOn: publish하는 순간, onNext하는 순간부터 다른곳으로뺌 > sub가 느려도 이상없음


         */
    public static void main(String[] args) {

        Publisher<Integer> pub = sub -> {
            log.debug("subscribe: pub");
            sub.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    log.debug("request: pub");
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
        /*
        subOn 은 언제 종료시키지?
        onSubscribe 이후부터,
         */

        Publisher<Integer> subOnPub = sub -> { // 하단의 subscriber
            log.debug("subscribe: subOnPub");

            pub.subscribe(new Subscriber<Integer>() {
                @Override
                public void onSubscribe(Subscription s) { // 상위에서 onSubscribe 실행해줄경우
                    ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory(){
                        @Override
                        public String getThreadNamePrefix() {
                            return "subOn";
                        }
                    });
                    log.debug("onSubscrib: subOnPub");
                    es.execute(() -> sub.onSubscribe(s));
                }

                @Override
                public void onNext(Integer integer) {
                    log.debug("onNext: subOnPub: {}", integer);
                    sub.onNext(integer);
                }

                @Override
                public void onError(Throwable t) {
                    log.debug("onError: subOnPub");
                    sub.onError(t);
                }

                @Override
                public void onComplete() {
                    log.debug("onComplete: subOnPub");
                    sub.onComplete();
                }
            });
        };

        Publisher<Integer> pubOnPub = sub -> { // 하단의 subscriber
            log.debug("subscribe: pubOnPub");
            ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory(){
                @Override
                public String getThreadNamePrefix() {
                    return "pubOn";
                }
            });
            /*
            이걸 언제 종료시키지?
            onNext 이후부터니까, 작업 완료된후, 그게 언제야
            onComplete 이후

             */

            subOnPub.subscribe(new Subscriber<Integer>() {
                @Override
                public void onSubscribe(Subscription s) { // 상위에서 onSubscribe 실행해줄경우
                    log.debug("onSubscribe: pubOnPub");
                    sub.onSubscribe(s);

                }

                @Override
                public void onNext(Integer integer) {
                    log.debug("onNex: pubOnPub: pubOnPub: {}", integer);
                    es.execute(() -> sub.onNext(integer));
                }

                @Override
                public void onError(Throwable t) {
                    log.debug("onError: pubOnPub");

                    es.execute(() -> {
                        sub.onError(t);
                    });
                    es.shutdown();
                }

                @Override
                public void onComplete() {
                    log.debug("onComplete: pubOnPub");
                    es.execute(() -> sub.onComplete());
                    es.shutdown();
                }
            });
        };

        pubOnPub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.debug("onSubscribe: sub");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.debug("onNext: sub: {}", integer);
            }

            @Override
            public void onError(Throwable t) {
                log.debug("onError: sub");
            }

            @Override
            public void onComplete() {
                log.debug("onComplete: sub");
            }
        });

        log.debug("exit");

    }
    /*
     예상결과
     main - sub: PubonPub
     main - sub: subOnPub
     main-1 - sub: pub
     main-1 - onSub: subOnPub
     main - exit
     thread-1 - onSub: pubOnPub
     thread-1 - onSub: sub
     thread-1 - req: pub
     thread-1 - onNext 1: subOnPub
     thread-1 - onNext 1: pubOnPub
     thread-1 - onNext 2: subOnPub
     thread-1 - onNext 2: pubOnPub
     thread-1 - onNext 3: subOnPub
     thread-1 - onNext 3: pubOnPub
     thread-1 - onNext 4: subOnPub
     thread-1 - onNext 4: pubOnPub
     thread-1 - onNext 5: subOnPub
     thread-1 - onNext 5: pubOnPub
     thread-1 - onComplete: subOnPub
     thread-1 - onComplete: pubOnPub
     thread-2 - onNext 1: sub
     thread-2 - onNext 2: sub
     thread-2 - onNext 3: sub
     thread-2 - onNext 4: sub
     thread-2 - onNext 5: sub
     thread-2 - onCOmplete: sub



    요청이 동시에 날라와도 큐에 걸려있다가 실행이됨
    중요한것중 하나, 하나의 publisher 가 데이터 생성해서 던져주는것은 멀티스레드로 호출하지 않게 되어있음
    그니까 publisher가 onNext를 멀티스레드에서 하지 않고 단일 스레드에서 진행, 호출은 순서가 보장됨

    onPub : publish 할때(이 때가 중요, 위치가 아님) 별개의 스레드를 사용하게 하는것
    onSub : subscribe 할때ㅑ 별개의 스레드 사용

    중간에 오펄에티ㅓ를 걸수 있고, 퍼블리셔를 여러개 만들고, 빠른걸 먼저 쓰거나 샘플링하거나 복합적인 방법 쓸수있는데
    중간 퍼블리셔, 스케줄러가 다른 방법을 타도록 하수있음
    최적화된 리액티브 스트림을 만드는것, 이게 어려움
    지금은 짧은 하나에서 동작하지만, 나중에 여러개 붙을것임
    범위 넓어지고 커지면, 전체가 비동기적으로 동작해야하는지 앍수있음

    기초가 되는 작업을 설명해줌
    스프링 지금 사용하는, 리액트를 해서 실제 해보
     */
}
