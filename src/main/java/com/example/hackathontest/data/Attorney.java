package com.example.hackathontest.data;

public class Attorney {
    private String name;
    private int wonCases;
    private int lostCases;

    public Attorney(String name, int wonCases, int lostCases) {
        this.name = name;
        this.wonCases = wonCases;
        this.lostCases = lostCases;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWonCases() {
        return wonCases;
    }

    public void setWonCases(int wonCases) {
        this.wonCases = wonCases;
    }

    public int getLostCases() {
        return lostCases;
    }

    public void setLostCases(int lostCases) {
        this.lostCases = lostCases;
    }
}
