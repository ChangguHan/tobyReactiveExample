package com.example.tobytv_reactive_organized.live4_CompletableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class C1_CompletableFuture2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture
                .supplyAsync(() -> { // supplyAsync = suppliable, () -> T
                    log.info("2-1 supplyAsync");
                    return 1;
                })
                .thenApply(s -> { // Apply : T -> T
                    log.info("2-2 thenApply: {}", s);
                    return s+1;
                })
                .thenCompose(s2 -> { // thenCompose는 flatMap 역할, 값을 CompletableFuture 로 가져올떄 한번더 감싸지 않도록
                    log.info("2-3 thenCompose: {}", s2);
                    return CompletableFuture.completedFuture(s2+1);
                })
                .thenAccept(s3 -> log.info("2-4 thenAccept: {}", s3));

        // 에러 발생 케이스, 중간에 exceptionally로 대처
        // exception 발생하면 모두 건너뛰고 내려옴(모두 성공 케이스만 정의했기 때문)
        CompletableFuture
                .supplyAsync(() -> { // supplyAsync = suppliable, () -> T
                    log.info("3-1 supplyAsync");
                    if (1==1) {throw new RuntimeException();}
                    return 1;
                })
                .thenApply(s -> { // Apply : T -> T
                    log.info("3-2 thenApply: {}", s);
                    return s+1;
                })
                .thenCompose(s2 -> { // thenCompose는 flatMap 역할, 값을 CompletableFuture 로 가져올떄 한번더 감싸지 않도록
                    log.info("3-3 thenCompose: {}", s2);
                    return CompletableFuture.completedFuture(s2+1);
                })
                .exceptionally(e -> -10) // 예외시 -10 리턴하도록
                .thenAccept(s3 -> log.info("2-4 thenAccept, {}", s3));

        log.info("EXIT");

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }
}