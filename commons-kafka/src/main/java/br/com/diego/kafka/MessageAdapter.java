package br.com.diego.kafka;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageAdapter.class);

    @Override
    public JsonElement serialize(Message message, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", message.getPayload().getClass().getName());
        jsonObject.add("payload", jsonSerializationContext.serialize(message.getPayload()));
        jsonObject.add("id", jsonSerializationContext.serialize(message.getId()));
        return jsonObject;
    }

    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        var obj = jsonElement.getAsJsonObject();
        var payloadType = obj.get("type").getAsString();
        var correlationid = (Correlationid) jsonDeserializationContext.deserialize(obj.get("correlationid"), Correlationid.class);
        try {
            var payload = jsonDeserializationContext.deserialize(obj.get("payload"), Class.forName(payloadType));
            return new Message(correlationid, payload);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
            throw new JsonParseException(e);
        }
    }
}
