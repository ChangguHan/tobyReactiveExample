package com.example.tobytv_reactive_organized.live4_CompletableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/*
콜백지옥을 람다로 해결한, Completion 방식을 직접 만들었는데, 이게 자바의 Completable로 존재
Future + Completable = CompletableFuture

Future: 비동기 작업 결과를 담은 클래스
ListenableFuture: Future + 콜백
 */
@Slf4j
public class C1_CompletableFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 바로 작업 완료된 Completable Future
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(1);
        CompletableFuture<Integer> cf2 = new CompletableFuture<>();
        // cf2.get(); // 이때 cf2내부에 값이 없기 때문에, 작업이 무한히 대기, 그래서  cf2.complete()를 실행해줘야함

        // 다른 스레드에서 작업하도록함
        CompletableFuture
                .runAsync(() -> log.info("1-1 runAsync")) // runAsync = runnable
                .thenRun(() -> log.info("1-2 thenRun"))
                .thenRun(() -> log.info("1-3 thenRun"));

        log.info("EXIT");

        // 데몬풀이기 때문에, 메인 스레드 종료되면 바로 종료
        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }
}