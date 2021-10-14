package br.com.diego.kafka;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class GenerateAllReportServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateAllReportServlet.class);

    private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        batchDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            batchDispatcher.send("SEND_MESSAGE_TO_ALL_USERS", "USER_GENERATE_READING_REPORT", "USER_GENERATE_READING_REPORT");

            LOGGER.info("SENT GENERATE REPORT TO ALL USERS");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("REPORT REQUESTS GENERATED");
        } catch (ExecutionException | InterruptedException | IOException e) {
            LOGGER.error("ERRO WHEN SEND NEW REPORT: {}", e.getMessage());
            throw new ServletException(e);
        }
    }
}
