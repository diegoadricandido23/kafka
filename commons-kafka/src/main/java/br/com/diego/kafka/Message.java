package br.com.diego.kafka;

public class Message<T> {

    private final Correlationid id;
    private final T payload;

    Message(Correlationid id, T payload){
        this.id = id;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message{" + "id=" + id + ", payload=" + payload + '}';
    }

    public Correlationid getId() {
        return id;
    }

    public T getPayload() {
        return payload;
    }
}