package br.com.diego.kafka;

import java.util.UUID;

public class Correlationid {
    private final String id;

    public Correlationid() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Correlationid{" + "id='" + id + '\'' + '}';
    }
}
