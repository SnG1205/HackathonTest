package com.example.hackathontest.data;

public class JustizResponse {
    private String kopf;
    private String spruch;
    private Attorney attorney; // Neu hinzugefügt

    public JustizResponse(String kopf, String spruch, Attorney attorney) {
        this.kopf = kopf;
        this.spruch = spruch;
        this.attorney = attorney;
    }


    // Getter und Setter für attorney
    public Attorney getAttorney() {
        return attorney;
    }
    public void setAttorney(Attorney attorney) {
        this.attorney = attorney;
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
}
