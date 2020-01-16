package com.wangyi.dagger;

public class Person {
    Student student;

    public Person(Student student) {
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
