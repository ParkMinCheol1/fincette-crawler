package com.welgram.test.basic;

public class JavaBasicTest {

    public static void main(String[] args) {
        
        Penguin p10 = new Penguin("펭귄10", 10);
        Penguin p1 = new Penguin("펭귄1", 1);
        PolarBear b3 = new PolarBear("북극곰3", "3");
        PolarBear b8 = new PolarBear("북극곰8", "8");

        p10.eat(new Fish("물고기1"));
        p10.eat(new Fish("물고기2"));
        p10.eat(new Fish("물고기3"));

        Penguin C = new Penguin("C", 3, null);

        // 1. 위 동물들의 배열에 담고 나이 출력하기
        
        // 2. 위 동물들의 리스트에 담고 나이 출력하기

        // 3. 위 동물들의 나이를 모두 더하고 총합을 출력하기 (1에서 만든 배열 사용)

        // 4. 위 동물들의 나이를 모두 더하고 평균을 출력하기 (2에서 만든 리스트 사용, 소수점 나오게 표현하기)

        // 5. 북극곰8이 펭귄1보다 나이가 많은지 확인하기 (많으면 true, 적으면 false) (삼항연산자 사용)

        // 6. 펭귄10이 물고기를 몇마리나 먹었는지 확인하기

        // 7. 펭귄10이 먹은 물고기들의 이름을 모두 출력하기 (향상된 for문 사용)

        // 9. 북극곰8의 나이만큼 펭귄1이 물고기 먹이기 (while 문 사용)

        // 10. 펭귄1이 먹은 물고기가 펭귄 10이 먹은 물고기보다 많은지 확인하기 (많으면 "많다" 출력, 적으면 "적다" 출력) (if 문 사용)

        // 11. 펭귄 class 안에 다른 펭귄 객체의 나이를 비교하는 메소드 만들기 (메소드 이름은 compareAge, 매개변수는 펭귄 객체 하나, 매개변수로 들어온 펭귄"보다" 나이가 많으면 1, 적으면 -1, 같으면 0 리턴하기)

        // 12. 펭귄A씨의 실제나이와 펭귄2씨의 신분증 나이 비교해주세요

        // 13. 펭귄C씨의 신분증이 없는 경우 생길수 있는 예외는?!?!
    }


}
