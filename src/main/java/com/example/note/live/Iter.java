package com.example.note.live;

import java.util.Arrays;
import java.util.Iterator;

public class Iter {
    // Reactive: 외부에 이벤트 발생하면, 대응하는 방식으로 작동
    // Duality: 쌍대성,
    // Observer 패턴 :리스너만들고 이벤트 만드는것
    // Reactive Streams : 표준, 자바 언어 이용하는 여러 회사들이, 리액티브 중구난방 만들지 말고, 적당한 레벨에서 표준 정하자
    // 리액티브 기술 표준을 넘어서 자바9에는, JDK안에 API로 들어감
    // 자바. 으로 들어가는것중에 오픈소스가 들어간게 꽤 있는데, 그중에 하나가 리액티브 스트림
    // 리액티브 스트림 주요 API, 인터페이스 스펙 따라서 코드 만들어보기


    public static void main(String[] args) {
        // 여러개 데이터가 있을때
        // 리스트로 받아오거나, 배열로 하거나, Iterable 로 하거나
        Iterable<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        /**
         * iterable은 for-each에서 사용가능
         * Collection이 아니어도 가능
         */
        for(Integer i : list) { // 이게 for-each, :  사용하는것, 이게 iterable이어서 가능
            System.out.println(i);
        }

        // Iterable 구현해보자
        Iterable<Integer> iter =() -> new Iterator<Integer>() {
            int i = 0;
            int MAX = 10;
            @Override
            public boolean hasNext() {
                if(i >= MAX) return false;
                return true;
            }

            @Override
            public Integer next() {
                return ++i;
            }
        };


        for(Integer j: iter) {
            System.out.println("j = " + j);
        }

        for(Iterator<Integer> it = iter.iterator(); it.hasNext();) {
            System.out.println("iter = " + it.next());
        }


    }

    // Duality: 궁극적의 기능은 같은데, 반대방향으로 구현이됨
    // Iterable <--> Observable
    // Iterable : pull, 요청해서 가져오는 방식, next()로
    // Observable: push, 주면 받는 방식, 소스쪽에서 밀어넣어줌




}