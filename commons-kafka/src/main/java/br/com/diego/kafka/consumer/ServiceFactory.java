package br.com.diego.kafka.consumer;

public interface ServiceFactory<T> {
    ConsumerService<T> create() throws Exception;
}
