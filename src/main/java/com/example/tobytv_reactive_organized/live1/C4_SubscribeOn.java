package com.example.tobytv_reactive_organized.live1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

/*
SubscribeOn
- 이레벨 상위의 publisher는 Subscribe을 다른 스레드에서 하는것
- 이것을 직접 한번 구현해보자
- 필요성: 데이터 생성이 오래걸릴때(publisher가 느린경우, 즉 onNext()호출이 느릴때), subscribe한 이후부터는 다른 스레드에서 넣자
 */
@Slf4j
public class C4_SubscribeOn {
    public static void main(String[] args) {
        Publisher<Integer> pub = C3_PubSub_Connect.iterPub(Stream.iterate(1, a-> a + 1).limit(10).collect(Collectors.toList()));
        subOnPub(pub).subscribe(C3_PubSub_Connect.logSub());
    }

    public static Publisher<Integer> subOnPub(Publisher<Integer> pub) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                log.info("Thread: {}", Thread.currentThread().getName());
                es.execute(() -> {
                    pub.subscribe(subscriber);
                });
            }
        };
    }
}
