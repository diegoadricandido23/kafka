package br.com.diego.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class ReadingReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadingReportService.class);
    private static final Path SOURCE = Path.of("sr/main/resources/report.txt");

    public static void main(String[] args) {
        var fraudService = new ReadingReportService();
        try (var service = new KafkaService<>(ReadingReportService.class.getSimpleName(),
                "USER_GENERATE_READING_REPORT",
                fraudService::parse,
                User.class,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
        LOGGER.info("-------------------------");
        LOGGER.info("PROCESSING REPORT FOR {}", record.value());

        var message = record.value();
        var user = message.getPayload();
        var target = new File((user.getReportPath()));
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for "+ user.getUuid());

        LOGGER.info("FILE CREATED: " + target.getAbsolutePath());
    }
}
