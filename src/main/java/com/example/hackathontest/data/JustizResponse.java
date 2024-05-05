package com.example.hackathontest.data;

public class JustizResponse {
    private String kopf;
    private String spruch;

    public JustizResponse(String kopf, String spruch) {
        this.kopf = kopf;
        this.spruch = spruch;
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
