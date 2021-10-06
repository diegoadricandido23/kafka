package br.com.diego.kafka;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewOrderServlet.class);

    private final KafkaDispatcher orderDispatcher = new KafkaDispatcher<Order>();
    private final KafkaDispatcher emailDispatcher = new KafkaDispatcher<String>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            var orderId = UUID.randomUUID().toString();
            var amount = new BigDecimal(req.getParameter("amount"));
            var email = req.getParameter("email");

            var order = new Order(orderId, email, amount);
            LOGGER.info("GERANDO NOVA VENDA: {}", order);

            orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

            var emailCode = "Thank you for order! We are processing your order";
            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);

            LOGGER.info("NEW ORDEM SENT SUCCESSFULLY");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("NEW ORDEM SENT SUCCESSFULLY");
        } catch (ExecutionException | InterruptedException | IOException e) {
            LOGGER.error("ERRO WHEN SEND ORDER: {}", e.getMessage());
            throw new ServletException(e);
        }
    }
}
