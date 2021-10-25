package br.com.diego.kafka.consumer;

import br.com.diego.kafka.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class GsonDeserializer implements Deserializer<Message> {

    private final Gson gson = new GsonBuilder().create();

    @Override
    public Message deserialize(String s, byte[] data) {
        return gson.fromJson(new String(data), Message.class);
    }
}
