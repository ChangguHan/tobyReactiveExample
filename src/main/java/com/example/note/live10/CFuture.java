package com.example.note.live10;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/*
비동기 웹기술
- 논블럭킹 호출하는 방
- 앞에서 Completion 클래스 만든것을 자바에서 존재하는 CompletableFuture로 사용

CompletableFuture
- Future: 비동기 작업 결과를 담고있는 오브젝트
- ListenableFuture: 콜백구조
- 비동기 작업 수행하는 코드 안에서 수행하는데
- COmpletable: 오브젝트 가지고 직접 완료하게 하는 작업 진행
- 간결한 방식으로 ES사용ㅎ자ㅣ않고 간단하게 멀티스레드 환경 만들수 있음
- CompletionStage를 구현: 하나의 비동기 작업 수행하고, 의존적으로 다른 작업수행 가능
- ListenableFuture

 */
@Slf4j
public class CFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f = CompletableFuture.completedFuture(1); // 이미 작업 완료된 CompletableFuture 객체
        CompletableFuture<Integer> f2 = new CompletableFuture<>();
        f.get(); // 이때는 작업이 무한히 대기하게됨, 그래서 f.complete()을 넣어줘야함

        // 다른 스레드에서 작업하도록, 특징: Completa
        CompletableFuture
                .runAsync(() -> log.info("1-1 runAsync"))
                .thenRun(() -> log.info("1-2 thenRun"))
                .thenRun(() -> log.info("1-3 thenRun2"));

        CompletableFuture
                .supplyAsync(() -> { // 리턴값 있도록
                    log.info("2-1 supplySync");
                    if (1 == 1) {throw new RuntimeException();}
                    return 1;
                })
                .thenApply(s -> { // Apply는 T -> T
                    log.info("2-2 thenApply : {}", s);
                    return s + 1;
                })
                .thenCompose(s -> {
                    log.info("2-3 thenCompose : {}", s);
                    return CompletableFuture.completedFuture(s+1); // 이런식으로 외부에 비동기작업으로 가져온 결과가 CompletableFuture일경우 thenCompose로 flatMap처럼 사용 가능
                })
                .exceptionally(e -> -10) // 예외 받으면 이후에 던지는게 아니라, 중간에 복구, 그럼 콜백을 쌍으로 넣어줄 필요가 없음
                .thenAccept(s -> log.info("2-4 {}", s));

        log.info("exit");

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);

    }
}
