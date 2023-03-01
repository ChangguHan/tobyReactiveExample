package com.example.note.live;

import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PubSub2 {
    /*
    Reactive Streams - 자바 표준 API, 스펙
    Operator 추가하는 방법
    실제 Reactor 사용법
    - Publisher, Subscriber, Subscription, Processor
    - Publisher: 제일 중요역할, 데이터 스트림을 만들어내는 프로바이더
    - Subscriber: 최종적으로 사용하는것, 옵저버 패턴처럼, 퍼블리셔의 subscribe 메소드를 호출
        - onSubscribe
        - onNext : 하나도 안갈수도 있음
        - onError | onComplete : 호출되고 끝남, 호출 영원히 안될수도 있음
    - Subscription:


    중간에 Operator 가 있을경우
    subscription 이 날라갈경우, op들을 거치게됨
    1. map (d1 -> f -> d2)
    
     */

    public static void main(String[] args) {
        Publisher<Integer> pub = new Publisher<Integer>() {
            Iterable<Integer> iter = Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList());
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(
                        new Subscription() {
                            @Override
                            public void request(long n) {
                                try {
                                    iter.forEach(s -> sub.onNext(s));
                                    sub.onComplete();
                                } catch(Throwable t) {
                                    sub.onError(t);
                                }
                            }

                            @Override
                            public void cancel() {

                            }
                        }
                );
            }
        };

        Subscriber<Integer> sub = new Subscriber<Integer>() {
            Subscription sup;
            @Override
            public void onSubscribe(Subscription subscription) {
                this.sup = subscription;
                subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println(item);
                sup.request(1);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        };

        pub.subscribe(sub);
    }
}
