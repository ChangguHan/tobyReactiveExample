ListenableFuture
- 콜백 메소드 넣어놀수있음

CompletableFuture
- 콜백 가능, 단 이때 원하는 스레드 지정 가능
- 비동기 task간의 연결
- 콜백 지옥 나지 않는 형태의 이벤트 설정 가능
```java
CompletableFuture completableFuture = new CompletableFuture();
    completableFuture.whenComplete(new BiConsumer() {
        @Override
        public void accept(Object o, Object o2) {
            //handle complete
        }
    }); // complete the task
    completableFuture.complete(new Object())
```
  https://stackoverflow.com/questions/38744943/listenablefuture-vs-completablefuture
  

```java
CompletableFuture
    .runAsync(() -> log.info("runAsync"))
    .thenRun(() -> log.info("thenRun"));
```
```java
CompletableFuture
        .supplyAsync(() -> 1)  // 비동기적으로 값을 제공 (외부 API 호출 등)
        .thenApply(i -> i + "hi")  // 값을 가공
        .exceptionally(e -> e.getMessage()) // 작업 중 오류가 발생했을 경우 사용될 값
        .thenAccept(i -> System.out.println(i)); // 값을 사용

```

https://do-study.tistory.com/123