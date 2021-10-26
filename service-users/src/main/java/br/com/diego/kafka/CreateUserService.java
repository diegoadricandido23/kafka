package br.com.diego.kafka;

import br.com.diego.kafka.consumer.ConsumerService;
import br.com.diego.kafka.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class CreateUserService implements ConsumerService<Order> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserService.class);
    private final Connection connection;

    private CreateUserService() throws SQLException {
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

    public static void main(String[] args) {
        new ServiceRunner<>(CreateUserService::new).start(1);
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        LOGGER.info("-------------------------");
        LOGGER.info("PROCESSING NEW ORDER, CHECKING FOR NEW USER");
        LOGGER.info("RECORD VAL: {}", record.value());

        var order = record.value().getPayload();
        if(isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());
        }

        LOGGER.info("PROCESSADO COM SUCESSO");
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }

    private void insertNewUser(String email) throws SQLException {
        var insert = connection.prepareStatement("INSERT INTO USERS (uuid, email)" + " VALUES (?,?)");
        var uuid = UUID.randomUUID().toString();
        insert.setString(1, uuid);
        insert.setString(2, email);
        LOGGER.info("USUARIO {} E {} ADICIONADOS", uuid, email);
    }

    private boolean isNewUser(String email) throws SQLException {
        var exists = connection.prepareStatement("SELECT uuid FROM USERS "
                + "WHERE email =? LIMIT 1");
        exists.setString(1, email);
        var results = exists.executeQuery();
        return !results.next();
    }
}
