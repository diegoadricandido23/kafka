package br.com.diego.kafka;

import br.com.diego.kafka.consumer.ConsumerService;
import br.com.diego.kafka.consumer.ServiceRunner;
import br.com.diego.kafka.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService implements ConsumerService<Order> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FraudDetectorService.class);
    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    private LocalDataBase dataBase;

    FraudDetectorService() throws SQLException {
        this.dataBase = new LocalDataBase("frauds_database");
        this.dataBase.createIfNotExistis("CREATE TABLE IF NOT EXISTS Orders ("
                + "uuid varchar (200) primary key, "
                + "is_fraud boolean)");
    }

    public static void main(String[] args) {
        new ServiceRunner<>(FraudDetectorService::new).start(1);
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
        LOGGER.info("-------------------------");
        LOGGER.info("PROCESSING NEW ORDER, CHECKING FOR FRAUD");
        LOGGER.info("RECORD KEY: {}", record.key());//DEFINE EM QUAL PARTICAO IRA CAIR A MENSAGEM
        LOGGER.info("RECORD VAL: {}", record.value());
        LOGGER.info("PARTITION : {}", record.partition());
        LOGGER.info("OFFSET    : {}", record.offset());

        var message = record.value();
        var order = message.getPayload();

        if(wasProcessed(order)) {
            LOGGER.info("ORDER {} WAS ALREADY PROCESSED", order.getOrderId());
            return;
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(isFraud(order)) {
            dataBase.update("insert into Orders (uuid, is_fraud) values (?, true)", order.getOrderId());
            LOGGER.info("ORDER IS A FRAUD: ", order);
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED",
                    order.getEmail(),
                    message.contiueWith(FraudDetectorService.class.getSimpleName()),
                    order);
        } else {
            LOGGER.info("APPROVED: {}", order);
            dataBase.update("insert into Orders (uuid, is_fraud) values (?, false)", order.getOrderId());
            orderDispatcher.send("ECOMMERCE_ORDER_APPROVED",
                    order.getEmail(),
                    message.contiueWith(FraudDetectorService.class.getSimpleName()),
                    order);
        }

        LOGGER.info("PROCESSADO COM SUCESSO");
    }

    private boolean wasProcessed(Order order) throws SQLException {
        var results = dataBase.query("select uuid from Orders where uuid = ? limit 1", order.getOrderId());
        return results.next();
    }

    @Override
    public String getTopic() {
        return"ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return FraudDetectorService.class.getSimpleName();
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500"))  >= 0;
    }
}
