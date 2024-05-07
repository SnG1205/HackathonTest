package com.example.hackathontest.data;

import java.util.List;

public class Attorney {
    private String name;
    private int wonCases;
    private int lostCases;
    private List<String> cases;

    public Attorney(String name, int wonCases, int lostCases, List<String> cases) {
        this.name = name;
        this.wonCases = wonCases;
        this.lostCases = lostCases;
        this.cases = cases;
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

    public List<String> getCases() {
        return cases;
    }

    public void setCases(List<String> cases) {
        this.cases = cases;
    }
}
