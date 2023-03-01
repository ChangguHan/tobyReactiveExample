package com.example.note.live9;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/*

지난번, 스프링과 서블릿의 비동기 프로그래밍
원시적이지만 현재까지 유효

Servlet 3.1
Callable 이용해서, 백단의 워커스레드 넣고
서블릿 스레드는 즉시 리턴

DeferrerResult
서블릿 스레드는 리턴하고, 오브젝트에 결과값쓰면
그때 클라이언트에게 넘어감

강제적으로 톰캣 스레드 1개 걸고, 동시에 100개 요청갔을때 어떻게 반응하는가
이것만 가지고는 딱히 뭘 할수가 없음
문제는 뒷단의 작업들, 별개의서비스들을 호출하는게 많이 있는경우, 단순히 비동기 사용하는것만으로 해결할수없음

Thread Pool Hell
- LinkedIn 에서 제공한 슬라이드
- 스레드풀이 순간 요청 급격하게 들어오면 꽉차게 됨
- 추가로요청들어오면 대기상태의 큐로 들어감
- 이런상황에서 Latency가 급격히 떨어짐, 즉 요청뒤 응답되는 시간
- 헬에 걸리는순간 응답시간이 5~6배 늘어나게됨
- 단순히 사용자가 늘어나서 생기는 문제?
- 문제 : 하나의 기업 안에서 MSA 를하니까, 여러개의 요청이들어가다보니, 여러개의 서버의 순차적 응답이 필요한경우, 시간이 오래걸림
- 즉 문제가 뭐냐면, 부하에 대피해 충분하지 못한게 아니라, 백엔드 요청을 호출한뒤 놀고있음, 그래서 요청 몰리는순간에 효율이 떨어짐
- 외부서비스도 마찬가지, 그동안 그 스레드가 대기상태로 빠져있음

Java Mission Control
- JDK 설치하면 번들되서 들어가는 프로그램중 하나인데 유용한 기능이 많음
- 테스트할때 사용하는건 자유롭게 사용, 운영용으로 사용할떄는 유료

외부에 요청을 보내서, API콜해서 최종겨로가 리턴할
 */

@Slf4j
@SpringBootApplication
@EnableAsync
public class MVCBasic {

    @RestController
    public static class MyController {
        RestTemplate rt = new RestTemplate();
        @GetMapping("/rest")
        public String rest(int idx){
//            CompletableFuture
            String url = "http://localhost:8013/service?req={req}"; // 스레드 풀 1개로 제한

            return "result " + rt.getForObject(url, String.class, "hello " + idx);
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(com.example.note.live9_ayncresttemplate.MVCBasic.class, args);
    }
}
