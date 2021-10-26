package br.com.diego.kafka;

import br.com.diego.kafka.consumer.ConsumerService;
import br.com.diego.kafka.consumer.KafkaService;
import br.com.diego.kafka.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReadingReportService implements ConsumerService<User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadingReportService.class);
    private static final Path SOURCE = Path.of("sr/main/resources/report.txt");

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        new ServiceRunner(ReadingReportService::new).start(5);
    }

    public void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
        LOGGER.info("-------------------------");
        LOGGER.info("PROCESSING REPORT FOR {}", record.value());

        var message = record.value();
        var user = message.getPayload();
        var target = new File((user.getReportPath()));
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for "+ user.getUuid());

        LOGGER.info("FILE CREATED: " + target.getAbsolutePath());
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_USER_GENERATE_READING_REPORT";
    }

    @Override
    public String getConsumerGroup() {
        return ReadingReportService.class.getSimpleName();
    }
}
