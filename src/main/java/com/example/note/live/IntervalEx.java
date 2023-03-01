package com.example.note.live;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntervalEx {
    public static void main(String[] args) {
        /*
        Interval, take 구현해보자
        일단 pub 만들고
        take에서 onNext 호출시 내부에 n으로 카운트해서 넘으면 complete
         */

        Publisher<Integer> pub = new Publisher() {
            @Override
            public void subscribe(Subscriber s) {
                s.onSubscribe(new Subscription() {
                    boolean cancelled = false;
                    @Override
                    public void request(long n) {
                        for(int i=0; i<20; i++) {
                            if(!cancelled) {
                                s.onNext(i);
                            }
                        }
                    }

                    @Override
                    public void cancel() {
                        cancelled = true;

                    }
                });
            }
        };

        Publisher<Integer> pubTake = new Publisher<Integer>() {

            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                pub.subscribe(new Subscriber<Integer>() {
                    int i=0;
                    Subscription sup;
                    @Override
                    public void onSubscribe(Subscription s) {
                        sup = s;
                        sub.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        sub.onNext(integer);
                        if(++i > 10) {
                            sup.cancel();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        sub.onError(t);

                    }

                    @Override
                    public void onComplete() {
                        sub.onComplete();
                    }
                });

            }
        };

        pubTake.subscribe(new Subscriber<Integer>() {
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
    }
}
