package com.example.tobytv_reactive_organized.live1;

import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
Reactive: 외부에 이벤트 발생하면, 대응하는 방식으로 작동

Reactive의 쌍대성 개념 : Iterator
- Duality 쌍대성, 궁극적인 기능은 같은데, 반대방향으로 구현
    - Observable: 리스너 만들고 이벤트 만드는것
- Iterable: pull, 요청해서 가져오는 방식, next()
- Observable: push, 주면 받는 방식, 소스쪽에서 밀어넣어줌

Reactive Streams 표준: 자바 이용하는 여러 회사들이, 리액티브 중구난방 만들지 말고, 적당한 레벨에서 표준
자바 8부터 JDK 안에 API안으로 들어감

Observer 패턴이 가지는 장점
- 만들어내는 쪽에서 자유롭게 입력 가능
- 멀티 브로드 캐스트 할수 있음
 */
public class C1_Observable {
    public static void main(String[] args) {
        IntObservable obs = new IntObservable();
        obs.addObserver((o, arg) -> System.out.println(arg + ": " +Thread.currentThread().getName()));

        ExecutorService es = Executors.newSingleThreadExecutor();
        System.out.println(Thread.currentThread().getName());
        es.execute(obs);
    }

    static class IntObservable extends Observable implements Runnable {
        @Override
        public void run() {
            for (int i=0; i<10; i++) {
                setChanged();
                notifyObservers(i); // 여기서 보내준 것으로 observer들이 출력
            }
        }
    }
}
