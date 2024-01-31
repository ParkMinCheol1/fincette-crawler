package com.welgram.test.basic;

import java.util.ArrayList;
import java.util.List;

public class PolarBear extends Animal {
    private String age;

    private List<Animal> foods = new ArrayList<>();

    public PolarBear(String name, String age) {
        super(name);
        this.age = age;
    }

    public String getAge() { return age; }

}
