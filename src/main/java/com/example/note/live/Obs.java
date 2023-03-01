package com.example.note.live;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("deprecation")
public class Obs {
    // Duality: 궁극적의 기능은 같은데, 반대방향으로 구현이됨
    // Iterable <--> Observable
    // Iterable : pull, 요청해서 가져오는 방식, next()로
    // Observable: push, 주면 받는 방식, 소스쪽에서 밀어넣어줌
    /*
    작성된 코드에 나타나는 방향이 완전히 반대

    Observer 패턴이 가지는 장점
    - 만들어내는쪽이 자유롭게 입력할수있고
    - 멀티 브로드캐스트핡수 있음

     */

    static class IntObservable extends Observable implements Runnable{

        @Override
        public void run() {
            for (int i=0; i<10; i++) {
                setChanged();
                notifyObservers(i);
            }
        }


    }
    public static void main(String[] args) {
        // 자바에 Observable이 들어가있고, 8까지 있었음
        IntObservable obs = new IntObservable(); // Source, Event(데이터) 를 던짐, Observer에게, Observer가 여러개 될수있음
        obs.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() + ":, " + arg);
            }
        });

        // 같은 메인스레드 안에서 동작하는건데
        // 별도의 스레드에서 비동기적으로 동작하도록
        ExecutorService es = Executors.newSingleThreadExecutor();
        System.out.println(Thread.currentThread().getName());
        es.execute(obs);

        /*
        Observable 문제
        - Complete이 없음
        - Error가 발생할경우, 예외적인상황(네트워크)에 복구 가능해야하는데, 예외 처리 재시도 등 옵션이 패턴에는 아이디어가 없음, 각자 구현할수박에

        Publisher.subscribe(Subscribe)
        SUbscribe

        Subscriber
        onSubscribe
        onNext



         
         */
    }



}