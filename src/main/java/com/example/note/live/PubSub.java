package com.example.note.live;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.Future;

public class PubSub {
    public static void main(String[] args) {
        Iterable<Integer> itr = Arrays.asList(1, 2, 3, 4, 5);
        ExecutorService es = Executors.newSingleThreadExecutor();

        Publisher p = new Publisher() {
            @Override
            public void subscribe(Subscriber subscriber) {
                // 데이터 주는쪽은, 누구에게 주는지 알아야하는데, 구독 방ㄱ식
                // 즉 여기에 어떤 subscriber 넣으면 되는지 확인
                subscriber.onSubscribe(new Subscription() {
                    /*
                    subscribe 메소드 호출시, publisher는 subscription 만든다음에 onsubscribe 호출
                    subscription으로 요청이 가능
                    backpressure라고 하는데, publisher - subscriber 차이 속도차가 발생할때 조절하도록 subscription으로 조절
                    이 요청할때 하는게 개수임
                     */
                    Iterator it = itr.iterator();
                    @Override
                    public void request(long n) {
                        // Future : 비동기 시작된 작업이, 결과가 무엇인지를 나타내는 오브젝트, 중간에 cancel 가능
                        // 결과값이없어서 f 로
                        Future<?> f = es.submit( () -> {
                            int i = 0;
                            // 요청만함, 응답값이 없음
                            // 데이터 보내는건 직접 보냄
                            // 람다식 밖에  정의된 n을 고칠수가 없음
                            // 왜? :
                            while(i++ < n) {
                                System.out.println("n = " + n);
                                if(it.hasNext()) {
                                    subscriber.onNext(it.next());
                                }
                                else {
                                    subscriber.onComplete();
                                    break;
                                }
                            }
                        });



                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber<Integer> s = new Subscriber<Integer>() {
            Subscription sub;
            @Override
            public void onSubscribe(Subscription subscription) {
                sub = subscription;
                System.out.println("subscription = " + subscription);
                subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                //  publisher가 데이터 준거가지고 처리해
                System.out.println("onNext, item = " + item);
                sub.request(1);

            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("throwable = " + throwable);
            }

            @Override
            public void onComplete() {
                // 더이상 데이터 없으니까 호출
                System.out.println("onComplete");

            }
        };

        p.subscribe(s);


        /*
        리액티브적용 - 리액티브하게 하고싶다
        pub, sub 각각 배압이 다를수있음, 그래서 sub에서 요청하는것도 필요


        스케줄러
        - 비동기적으로 동시에 병렬적으로 작업을 수행
        - 결국 동시성을 가지고 복잡한 코드를 간결하ㅔㄱ 만들기 위해서 사용
        - 지금 엔진을 만드는 거라서 복잡해서, 이미 수백가지가 넘음
        - 굉장히 세련된 방식으로 가능

        비동기적
        pub -> sub
        pub는 한 subscription 에 대해서 여러개 스레드 사용하지 않음
        sub는 sequential 하게 데이터 날아옴
        어느 한순간에는 하나의 스레드만
        동시성 이슈적음, 1.0 전에는 병렬적으로 해보자, 장점이 다 상쇄돼고 너무 지저분해짐

         */
        es.shutdown();
    }
}
