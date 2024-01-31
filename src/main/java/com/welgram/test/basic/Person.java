package com.welgram.test.basic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person { // 롬복 테스트
    
    private int age;

    public static void main(String[] args) {
        Person p = new Person();
        p.setAge(11);

        System.out.println("p.getAge() = " + p.getAge());
    }
}
