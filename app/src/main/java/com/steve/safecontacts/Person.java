package com.steve.safecontacts;
/*
    Author: Steve Bao
*/

public class Person {

    public Person(String name, String phone) {
        if (name == null)
            this.name = "na";
        else
            this.name = name;

        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int compareTo(Person b) {
        return this.name.compareTo(b.name);
    }
    String name;
    String phone;

}
