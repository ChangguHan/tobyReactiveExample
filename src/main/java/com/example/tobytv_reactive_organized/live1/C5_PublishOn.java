package com.example.tobytv_reactive_organized.live1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

/*
publishOn
- 이건 request시 다른 스레드에서 진행
- 필요성: onNext()호출시 subscriber에서 처리가 느릴때
 */
@Slf4j
public class C5_PublishOn {
    public static void main(String[] args) {
        Publisher<Integer> pub = C3_PubSub_Connect.iterPub(Stream.iterate(1, a-> a + 1).limit(10).collect(Collectors.toList()));
        pubOnPub(pub).subscribe(C3_PubSub_Connect.logSub());
    }

    public static Publisher<Integer> pubOnPub(Publisher<Integer> pub) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                log.info("pubOnPub subscribe");
                pub.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        log.info("pubOnPub onSubscribe");
                        subscriber.onSubscribe(subscription);
                    }

                    @Override
                    public void onNext(Integer item) {
                        es.execute(() -> subscriber.onNext(item));
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        };
    }
}
