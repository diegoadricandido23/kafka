package br.com.diego.kafka;

public interface ServiceFactory<T> {
    ConsumerService<T> create();
}
