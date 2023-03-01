package com.example.note.live3;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import lombok.extern.slf4j.Slf4j;

/*
리액티브 스트림스 표준 : 표준 API 구현하는 이유, 안에서 어떻게 하는지 헷갈릴수 있음
복잡해질수박에 없는 비동기적인 것, 복잡한것을 간결하게 해결못할수 있음
기본적ㅇ로 어떻겍 굴러가는지 큰 그림을 가져야함

최적화하기 위해 굉장히 복잡함
가장 기초적이고 핵심적인 것

Subscriber는 가져야할것
onSubscribe, onNext, onError, onComplete


하나의 스레드에서 왔다갔다하는데
pub.subscribe() 메소드가 끝나기전에 아래 작업이 모두 끝ㅁ남

메인에서, 한개 실행되다가, subscribe 실행후, pub 람다 안에 subscribe 메소드 실행
넘어온 onÍubscribe 을 호출하는데 subscription 호출
그리고 request, onNext, 쭉쭉, 리턴하면 onSubScribe 끝나고, pub.subscribe 끝나고 빠져나감

논리가있게 역할을 구분한건 좋은데
실전에서는 이것만 가지고는 유용하지 않음

장점
이벤트발생 : 외부에서 엑션, 백그라운드 작업
기다리고 있는 블로킹 구조를, 계속 블록되어있음
나머지 코드는 전혀 실행이 안되니까 화면이 멈춰짐
서버로 보자면, 스레드 나누긴 하지만 하나의 작업을 기다리는 동안 블러킹하면
스레드 풀이 꽉차면, 큐마저 꽉차서 에러가 발생

실제 프로그램에서는 pub, sub을 같은 스레드에 넣지 않음
subscription의 request가, 외부 호출 작업이 들어가 있을때
이만든작업이 멈춰있으면, 호출하는쪽도 블러킹되어있음
그래서 pub에서 처리해주는곳을 별도이ㅡ thread 에서 동작


 */
@Slf4j
public class SchedulerEx {
    public static void main(String[] args) {
        Publisher<Integer> pub = sub -> {
            sub.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    sub.onNext(1);
                    sub.onNext(2);
                    sub.onNext(3);
                    sub.onNext(4);
                    sub.onNext(5);
                    sub.onComplete();
                }

                @Override
                public void cancel() {

                }
            });
        };
        pub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.debug("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.debug("onNext:{}", integer);
            }

            @Override
            public void onError(Throwable t) {
                log.debug("onError:{}", t);

            }

            @Override
            public void onComplete() {
                log.debug("onComplete");

            }
        });
    }
}
