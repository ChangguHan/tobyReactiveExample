package com.example.note.live;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class FluxScEx {
    public static void main(String[] args) throws InterruptedException {
//        Flux.range(1, 10)
//                .publishOn(Schedulers.newSingle("pub"))
//                .log()
//                .subscribeOn(Schedulers.newSingle("sub"))
//                .subscribe(System.out::println);
//
//        System.out.println("exit");
        /*
        내부적으로 스레드풀 만들어서 할당하고 버리는게 아니라
        섭스크라이브 여러개 붙일수 있으니까, 만들어지고 계쏙 유지
        메인 메소드에서 실행되면 바로 사리지지 않아서, 강제로 죽여야함


        별도의 스레드 떠서 동작하는 대표적인 동작
        interval(),

         */
        Flux.interval(Duration.ofMillis(500))
                .take(10) // 데이터 받다가 몇개만 받고, 중간에 끝내버림
                .subscribe(s -> log.debug("onNext: {}", s));

        log.debug("exit");
        /*
        이거 실행이 안됨
        이유: 메인메소드가 아니고 별개의 스레드에서 interval 실행됨

        메인스레드 죽는다고해서, 죽을 이유는 없음, 그럼 왜죽을까?
        유저스레드: 메인스레드 종료되도 종료되지 않음
        인터벌이 만들어내는, 내부 타이머 스레드는, 유저스레드가 아닌 데몬 스레드
        스레드 종류 2가지 : 유저, 데몬
        데몬: 강제로 종료
        유저는 하나라도 남아있으면 종료하지 않음

        수많은 메인 스레드가 돌아가고 있으니, 데몬 스레드가 폏낳ㅁ
        상당의 기본 내부스레드는 데몬스레드


         */
        TimeUnit.SECONDS.sleep(5);
    }
}
