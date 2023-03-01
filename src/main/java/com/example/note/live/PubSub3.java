//package com.example.tobytvreactive.live;
//
//import java.util.concurrent.Flow.Publisher;
//import java.util.concurrent.Flow.Subscriber;
//import java.util.concurrent.Flow.Subscription;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class PubSub3 {
//    /*
//    -> : 다운 스트림
//    <- : 업스트림
//     */
//    public static void main(String[] args) {
//        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
////        Publisher<Integer> mapPub = mapPub(pub, s -> s * 10);
////        mapPub.subscribe(logSub());
//        sumPub(pub).subscribe(logSub());
//        // 데이터 날라와도 가지고 있다가, 한번에 던지기
//        // 게속 요청보내서, onComplete 때 던져야될듯
//    }
//
//    private static Subscriber<Integer> logSub() {
//        return new Subscriber<Integer>() {
//            @Override
//            public void onSubscribe(Subscription s) {
//                log.debug("onSubscribe");
//                s.request(Long.MAX_VALUE);
//            }
//
//            @Override
//            public void onNext(Integer item) {
//                log.debug("onNext:{}", item);
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                log.debug("onError:{}", throwable);
//            }
//
//            @Override
//            public void onComplete() {
//                log.debug("onComplete");
//
//            }
//        };
//    }
//    private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> f) {
//        return new Publisher<Integer>() {
//            @Override
//            public void subscribe(Subscriber<? super Integer> sub) {
////                결국 onNext 에 n을 f 해서 넘겨줘야한단말야
////                그런데 onNext는 sub에서 정의되잔항
////                그러니까 sub를 다시 정의할수밖에 없지않나
//                pub.subscribe(new DelegateSub(sub) {
//                    @Override
//                    public void onNext(Integer item) {
//                        sub.onNext(f.apply(item));
//                    }
//                });
//
//
//            }
//        };
//
//    }
//
//    private static Publisher<Integer>  sumPub(Publisher<Integer> pub) {
//        return new Publisher<Integer>() {
//            @Override
//            public void subscribe(Subscriber<? super Integer> sub) {
////                결국 onNext 에 n을 f 해서 넘겨줘야한단말야
////                그런데 onNext는 sub에서 정의되잔항
////                그러니까 sub를 다시 정의할수밖에 없지않나
//                pub.subscribe(new DelegateSub(sub) {
//                    private Integer sum = 0;
//                    @Override
//                    public void onNext(Integer item) {
//                        sum += item;
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        sub.onNext(sum);
//                        sub.onComplete();
//                    }
//                });
//
//
//            }
//        };
//
//    }
//
//
//    private static Publisher<Integer> iterPub(Iterable<Integer> iter) {
//        return new Publisher<Integer>() {
//            @Override
//            public void subscribe(Subscriber<? super Integer> sub) {
//                sub.onSubscribe(
//                        new Subscription() {
//                            @Override
//                            public void request(long n) {
//                                try {
//                                    iter.forEach(s -> sub.onNext(s));
//                                    sub.onComplete();
//                                } catch(Throwable t) {
//                                    sub.onError(t);
//                                }
//                            }
//
//                            @Override
//                            public void cancel() {
//
//                            }
//                        }
//                );
//            }
//        };
//    }
//}
