package com.example.note.live;

import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PubSub5 {
    /*
    General 사용해보자 */
    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
        Publisher<String> mapPub = mapPub(pub, s -> "a" + s);
        mapPub.subscribe(logSub());
    }

    private static <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.debug("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T item) {
                log.debug("onNext:{}", item);
            }

            @Override
            public void onError(Throwable throwable) {
                log.debug("onError:{}", throwable);
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");

            }
        };
    }
    private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {
//                결국 onNext 에 n을 f 해서 넘겨줘야한단말야
//                그런데 onNext는 sub에서 정의되잔항
//                그러니까 sub를 다시 정의할수밖에 없지않나
                pub.subscribe(new DelegateSub<T,R>(sub) {
                    @Override
                    public void onNext(T item) {

                        sub.onNext(f.apply(item));
                    }
                });

                // subscrib는 publisher 것
                // 거기서 onNext를 실행하는건데,

            }
        };

    }

    private static <T> Publisher<T> iterPub(Iterable<T> iter) {
        return new Publisher<T>() {
            @Override
            public void subscribe(Subscriber<? super T> sub) {
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
    }
}
