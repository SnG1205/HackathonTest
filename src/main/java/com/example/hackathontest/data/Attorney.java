package com.example.hackathontest.data;

public class Attorney {
    private String name;
    private int wonCases;
    private int lostCases;

    // Standardkonstruktor für Fälle, in denen keine Daten beim Erstellen des Objekts verfügbar sind
    public Attorney() {
        this.name = "";
        this.wonCases = 0;
        this.lostCases = 0;
    }

    // Konstruktor für vollständige Initialisierung
    public Attorney(String name, int wonCases, int lostCases) {
        this.name = name;
        this.wonCases = wonCases;
        this.lostCases = lostCases;
    }

    // Getter und Setter
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
