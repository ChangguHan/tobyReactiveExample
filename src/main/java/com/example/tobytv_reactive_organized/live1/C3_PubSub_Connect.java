package com.example.tobytv_reactive_organized.live1;

import java.util.List;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

/*
Steram 중간에 다른 스트림 끼워서 데이터를 전달
한번 중간에서 모두 더해주는 publisher 만들어보자

 */
@Slf4j
public class C3_PubSub_Connect {
    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a->a+1).limit(10).collect(Collectors.toList()));
        sumPub(pub).subscribe(logSub());
    }

    public static Subscriber<Integer> logSub() {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer item) {
                log.info("{}, onNext: {}", Thread.currentThread().getName(), item);

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };
    }
    /*
    여기서 잘 생각해줘야함
    pub <-> sumPub <-> sub
    순서
    1. sumPub.subscribe() -> pub.subscribe() -> sumPub's sub.onSubscribe -> sub.onSubscribe
     */
    public static Publisher<Integer> sumPub(Publisher<Integer> pub) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                pub.subscribe(new Subscriber<Integer>() {
                    int sum = 0;
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscriber.onSubscribe(subscription);
                    }

                    @Override
                    public void onNext(Integer item) {
                        sum += item;
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {
                        subscriber.onNext(sum);
                        subscriber.onComplete();
                    }
                });
            }
        };
    }

    public static Publisher<Integer> iterPub(List<Integer> list) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        for (Integer each : list) {
                            subscriber.onNext(each);
                        }
                        subscriber.onComplete();
                    }

                    @Override
                    public void cancel() {
                    }
                });
            }
        };

    }

}
