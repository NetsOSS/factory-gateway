package eu.nets.oss.template.webapp.model;

import javax.persistence.Entity;

@Entity
public class Person extends AbstractEntity {
    private String name;

    private int age;

    protected Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
