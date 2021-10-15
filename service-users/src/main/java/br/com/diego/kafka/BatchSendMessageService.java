package br.com.diego.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchSendMessageService.class);
    private final Connection connection;
    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();

    BatchSendMessageService() throws SQLException {
        String url = "jdbc:sqlite:service-users/target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS Users ("
                    + "uuid varchar (200) primary key, "
                    + "email varchar(200))");

        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        var batchService = new BatchSendMessageService();
        try (var service = new KafkaService<>(BatchSendMessageService.class.getSimpleName(),
                "ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS",
                batchService::parse,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<String>> record) throws ExecutionException, InterruptedException, SQLException {
        LOGGER.info("-------------------------");
        LOGGER.info("PROCESSING NEW BATCH");
        var message = record.value();
        LOGGER.info("TOPIC VAL: {}", message.getPayload());

        for(User user : getAllUsers()) {
            userDispatcher.sendAsync(message.getPayload(),
                    user.getUuid(),
                    message.getId().continueWith(BatchSendMessageService.class.getSimpleName()),
                    user);
            LOGGER.info("SEND TO {}", user);
        }


        LOGGER.info("PROCESSADO COM SUCESSO");
    }

    private List<User> getAllUsers() throws SQLException {
        var results = connection.prepareStatement("SELECT UUID FROM USERS").executeQuery();
        List<User> users = new ArrayList<>();
        while (results.next()) {
            users.add(new User(results.getString(1)));
        }
        return users;
    }
}
