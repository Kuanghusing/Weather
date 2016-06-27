package com.kuahusg.weather.model;

/**
 * Created by kuahusg on 16-6-27.
 * com.kuahusg.weather.model
 */
public class Citys {
    private String name;
    private String parent1;
    private String parent2;
    private String parent3;


    public Citys(String name, String parent1, String parent2, String parent3) {
        this.name = name;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.parent3 = parent3;
    }

    public String getName() {
        return name;
    }

    public String getParent1() {
        return parent1;
    }

    public String getParent2() {
        return parent2;
    }

    public String getParent3() {
        return parent3;
    }
}
