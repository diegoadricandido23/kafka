package br.com.diego.kafka;

import java.util.UUID;

public class Correlationid {
    private final String id;

    public Correlationid(String title) {
        this.id = title + "(" + UUID.randomUUID() + ")";
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Correlationid{" + "id='" + id + '\'' + '}';
    }

    public Correlationid continueWith(String title) {
        return new Correlationid(id + "-" + title);
    }
}
