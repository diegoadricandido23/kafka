package br.com.diego.kafka;

import br.com.diego.kafka.consumer.ConsumerService;
import br.com.diego.kafka.consumer.ServiceRunner;
import br.com.diego.kafka.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BatchSendMessageService implements ConsumerService<User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchSendMessageService.class);
    private final LocalDataBase dataBase;
    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();

    BatchSendMessageService() throws SQLException {
        this.dataBase = new LocalDataBase(SQLConstantes.DATABASE_NAME);
        this.dataBase.createIfNotExistis(SQLConstantes.CREATE_TABLE);
    }

    public static void main(String[] args) {
        new ServiceRunner<>(BatchSendMessageService::new).start(1);
    }

    public void parse(ConsumerRecord<String, Message<User>> record) throws SQLException {
        LOGGER.info("-------------------------");
        LOGGER.info("PROCESSING NEW BATCH");
        var message = record.value();
        LOGGER.info("TOPIC VAL: {}", message.getPayload());

        for(User user : getAllUsers()) {
            userDispatcher.sendAsync(getTopic(),
                    user.getUuid(),
                    message.getId().continueWith(BatchSendMessageService.class.getSimpleName()),
                    user);
            LOGGER.info("SEND TO {}", user);
        }


        LOGGER.info("PROCESSADO COM SUCESSO");
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS";
    }

    @Override
    public String getConsumerGroup() {
        return BatchSendMessageService.class.getSimpleName();
    }

    private List<User> getAllUsers() throws SQLException {
        var results = dataBase.query(SQLConstantes.SELECT_ALL_USERS);
        List<User> users = new ArrayList<>();
        while (results.next()) {
            users.add(new User(results.getString(1)));
        }
        return users;
    }
}
