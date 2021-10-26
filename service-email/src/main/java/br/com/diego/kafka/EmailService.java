package br.com.diego.kafka;

import br.com.diego.kafka.consumer.ConsumerService;
import br.com.diego.kafka.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailService implements ConsumerService<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    public static void main(String[] args) {
        new ServiceRunner(EmailService::new).start(5);
    }

    public String getTopic() {
        return "ECOMMERCE_SEND_EMAIL";
    }

    public String getConsumerGroup() {
        return EmailService.class.getSimpleName();
    }

    public void parse(ConsumerRecord<String, Message<String>> record) {
        LOGGER.info("-------------------------");
        LOGGER.info("SENDING EMAIL, CHECKING FOR FRAUD");
        LOGGER.info("RECORD KEY: {}", record.key());//DEFINI EM QUAL PARTICAO IRA CAIR A MENSAGEM
        LOGGER.info("RECORD VAL: {}", record.value());
        LOGGER.info("PARTITION : {}", record.partition());
        LOGGER.info("OFFSET    : {}", record.offset());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("PROCESSADO COM SUCESSO");

    }
}
