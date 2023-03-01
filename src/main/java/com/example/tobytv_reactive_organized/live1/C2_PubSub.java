package com.example.tobytv_reactive_organized.live1;

import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import lombok.extern.slf4j.Slf4j;

/*
Observable의 문제
- Complete 가 없음
- Error 핸들링이 없음
>> Publisher Subscriber 사용(Java 9)

Publisher, Subscribe 순서
1. publisher.subscribe(subscriber): subscriber 등록
2. subscriber.onSubscribe(subscription): subscriber의 onSubscribe 호출하며 subscription 전달
3. subscription.request(n):subscription의 request 호출
4. subscriber.onNext(item): publisher가 onNext를 통해 값 전달
 */
@Slf4j
public class C2_PubSub {
    public static void main(String[] args) {
        Publisher p = new Publisher() {
            @Override
            public void subscribe(Subscriber subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        for(long i=0; i<n; i++) {
                            subscriber.onNext(i);
                        }
                        subscriber.onComplete();
                    }
                    @Override
                    public void cancel() {}
                });
            }
        };

        Subscriber<Long> s = new Subscriber<Long>() {
            Subscription sub;
            @Override
            public void onSubscribe(Subscription subscription) {
                sub = subscription;
                log.info("subscription: {}", sub);
                subscription.request(10L);
            }

            @Override
            public void onNext(Long item) {
                log.info("onNext: {}", item);
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("throable: {}", throwable);
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };

        p.subscribe(s);
    }
}
