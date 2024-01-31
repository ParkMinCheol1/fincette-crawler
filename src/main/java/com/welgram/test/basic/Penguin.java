package com.welgram.test.basic;

import java.util.ArrayList;
import java.util.List;

public class Penguin extends Animal{

    private int age;
    private List<Animal> fishes = new ArrayList<>();

    private IdCard idCard = new IdCard();

    public Penguin(String name, int age) {
        super(name);
        this.age = age;
    }

    public Penguin(String name, int age, IdCard idCard) {
        super(name);
        this.age = age;
        this.idCard = idCard;
    }

    public int getAge() { return age; }

    public void eat(Fish fish) {
        fishes.add(fish);
    }

}
