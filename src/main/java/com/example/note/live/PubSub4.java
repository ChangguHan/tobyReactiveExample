//package com.example.tobytvreactive.live;
//
//import java.util.concurrent.Flow.Publisher;
//import java.util.concurrent.Flow.Subscriber;
//import java.util.concurrent.Flow.Subscription;
//import java.util.function.BiFunction;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class PubSub4 {
//    /*
//    -> : 다운 스트림
//    <- : 업스트림
//     */
//    public static void main(String[] args) {
///*
//        sumpub을 좀더 general 하게
//        더하기 뿐만 아니라 , 가공하다가 uncomplete 했을때 데이터 넘기긴 하는데, 어떻게 축적 할것인가
//         */
//        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
//        resucePub(pub, 0, (a, b) -> a + b).subscribe(logSub());
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
//    private static Publisher<Integer> resucePub(Publisher<Integer> pub,Integer init, BiFunction<Integer, Integer, Integer> f) { // a : 기존, b : 신규, reuslt : 결과
//        return new Publisher<Integer>() {
//            @Override
//            public void subscribe(Subscriber<? super Integer> sub) {
////                결국 onNext 에 n을 f 해서 넘겨줘야한단말야
////                그런데 onNext는 sub에서 정의되잔항
////                그러니까 sub를 다시 정의할수밖에 없지않나
//                pub.subscribe(new DelegateSub(sub) {
//                    private Integer result = init;
//                    @Override
//                    public void onNext(Integer item) {
//                        result = f.apply(result, item);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        sub.onNext(result);
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
