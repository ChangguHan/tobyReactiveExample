package com.example.note.live3;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import lombok.extern.slf4j.Slf4j;

/*
비동기작업하다가 예외가 발생하면,다른 스레드의 익셉션이라서 메인스레드로 전파가 되지 않음
끌어올 방법이 있어야하는데
get()은 excception 을 던지는데, 메인 스레드쪽에 자연스럽게 넘겨줬으면 좋겠음
익셉션 오브젝트를, 익셉션 콜백에 던져주고 싶음


 */
@Slf4j
public class FutureEx4 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {


        ExecutorService es = Executors.newCachedThreadPool();

        CallbackFutureTask f = new CallbackFutureTask(() -> {
            Thread.sleep(2000);            // Interrupt exception: 쉬고있는 도중, 누군가가 너 일어나, 그만해 라는 스레드를 보낼수있는데, 그떄 익셉션
            System.out.println("Async");
            throw new RuntimeException();
//            return "Hello";
        }, (rs) -> {
            //
            System.out.println("rs = " + rs);
        }, (t) -> {
            log.debug(t.getMessage());
        });

        es.execute(f);
        es.shutdown();
/*
맘에 안드는것
- 비동기작업에 필요한 기술적 코드(ExecutorService, execute, shutdown)와 비즨스 코드가 섞여있음 > 분리하고 추상화해야함
- 이와 같은작업을 스프링으로 얼마나 편하게 할수 있을지
- 스프링의 10년전 기술 보도록 시작
 */

    }

    /*
    예외를 받는게 중요한게 아니야, 인터럽트가 발생했다는 것ㅅ을 알리는게 중요
    ExecutionException : 에러가 진짜 발생한 케이스, e.cause
     */
    interface SuccessCallback {
        void onSuccess(String result);
    }

    interface ExceptionCallback {
        void onError(Throwable t);
    }
    public static class CallbackFutureTask extends FutureTask<String> {
        SuccessCallback sc;
        ExceptionCallback ec;
        public CallbackFutureTask(Callable<String> callable, SuccessCallback sc, ExceptionCallback ec) {
            super(callable);
            this.sc = Objects.requireNonNull(sc);
            this.ec = Objects.requireNonNull(ec);
        }

        @Override
        protected void done() {
            super.done();
            try {
                sc.onSuccess(get());
            } catch (Exception e) {
                ec.onError(e.getCause());
            }
        }
    }
}
/*
비동기 작업 가져오는 두가지바법
1. Future를 가지고와서, get() 호출
2. Call back 사용

 */
