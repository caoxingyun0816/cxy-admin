package com.wondertek.mam.util.others;

import java.util.*;

public class Student{
    private int id;

    private String name;

    private Integer age;

    private Date birthday;

    public Student(int id, String name, Integer age, Date birthday) {
        super();
        this.id = id;
        this.name = name;
        this.age = age;
        this.birthday = birthday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Student [id=" + id + ", name=" + name + ", age=" + age
                + ", birthday=" + birthday + "]";
    }


}