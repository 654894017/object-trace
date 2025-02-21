package com.damon.test;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class EqualsPerformanceTest {
    public static void main(String[] args) {
        Person p1 = new Person("Alice", 30, true);
        Person p2 = new Person("Alice", 30, true);

        int iterations = 1000_000_000; // 测试 100 万次

        // 测试 reflectionEquals
        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            p1.equals(p2);
        }
        long endTime = System.nanoTime();
        System.out.println("reflectionEquals 耗时: " + (endTime - startTime) / 1_000_000.0 + " ms");

        // 测试手写 equals 方法
        startTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            EqualsBuilder.reflectionEquals(p1, p2);
        }
        endTime = System.nanoTime();
        System.out.println("手写 equals 耗时: " + (endTime - startTime) / 1_000_000.0 + " ms");
    }

    static class Person {
        private String name;
        private int age;
        private boolean isActive;

        public Person(String name, int age, boolean isActive) {
            this.name = name;
            this.age = age;
            this.isActive = isActive;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Person other = (Person) obj;
            return age == other.age &&
                    isActive == other.isActive &&
                    name.equals(other.name);
        }
    }
}