package com.example.note.live3;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import lombok.extern.slf4j.Slf4j;

/*
          또하나의방식 : 콜백,
          - Future를 Object로 만ㄷ
          - Future Object 와 비동기 콜백이 하나에 다 들어감

 */
@Slf4j
public class FutureEx3 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {


        ExecutorService es = Executors.newCachedThreadPool();

        CallbackFutureTask f = new CallbackFutureTask(() -> {
            Thread.sleep(2000);            // Interrupt exception: 쉬고있는 도중, 누군가가 너 일어나, 그만해 라는 스레드를 보낼수있는데, 그떄 익셉션
            System.out.println("Async");
            return "Hello";
        }, (rs) -> {
            //
            System.out.println("rs = " + rs);
        });

        es.execute(f);
        es.shutdown();


    }

    /*
    비동기작업 정상적으로 종료되면 어떤작업 수행할수 있는지 담을수 있는 콜백 인터페이스
     */
    interface SuccessCallback {
        void onSuccess(String result);
    }
    public static class CallbackFutureTask extends FutureTask<String> {
        // callable이라는 비동기작업을 받음
        SuccessCallback sc;
        public CallbackFutureTask(Callable<String> callable, SuccessCallback sc) {
            super(callable);
            this.sc = Objects.requireNonNull(sc);
        }

        @Override
        protected void done() {
            super.done();
            try {
                sc.onSuccess(get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
