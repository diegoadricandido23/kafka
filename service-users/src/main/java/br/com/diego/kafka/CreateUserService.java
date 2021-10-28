package br.com.diego.kafka;

import br.com.diego.kafka.consumer.ConsumerService;
import br.com.diego.kafka.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.UUID;

public class CreateUserService implements ConsumerService<Order> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserService.class);
    private final LocalDataBase dataBase;

    CreateUserService() throws SQLException {
        this.dataBase = new LocalDataBase(SQLConstantes.DATABASE_NAME);
        this.dataBase.createIfNotExistis(SQLConstantes.CREATE_TABLE);
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
        var uuid = UUID.randomUUID().toString();
        this.dataBase.update(SQLConstantes.INSERT_USER, uuid, email);

        LOGGER.info("USUARIO {} E {} ADICIONADOS", uuid, email);
    }

    private boolean isNewUser(String email) throws SQLException {
        var results = this.dataBase.query(SQLConstantes.SELECT_USER_ID, email);
        return !results.next();
    }
}
