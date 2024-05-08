package com.example.hackathontest.data;

public class JustizResponse {
    private String kopf;
    private String spruch;
    private Attorney attorney;
    private int wonCases;
    private int lostCases;

    // Konstruktoren, Getter und Setter

    public JustizResponse(String kopf, String spruch, Attorney attorney) {
        this.kopf = kopf;
        this.spruch = spruch;
        this.attorney = attorney;
        this.wonCases = 0;
        this.lostCases = 0;
    }

    public String getKopf() {
        return kopf;
    }

    public void setKopf(String kopf) {
        this.kopf = kopf;
    }

    public String getSpruch() {
        return spruch;
    }

    public void setSpruch(String spruch) {
        this.spruch = spruch;
    }

    public Attorney getAttorney() {
        return attorney;
    }

    public void setAttorney(Attorney attorney) {
        this.attorney = attorney;
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

    @Override
    public String toString() {
        return "JustizResponse{" +
                "kopf='" + kopf + '\'' +
                ", spruch='" + spruch + '\'' +
                ", attorney=" + attorney +
                ", wonCases=" + wonCases +
                ", lostCases=" + lostCases +
                '}';
    }
}
