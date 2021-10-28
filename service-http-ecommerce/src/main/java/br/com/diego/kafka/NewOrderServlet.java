package br.com.diego.kafka;

import br.com.diego.kafka.dispatcher.KafkaDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewOrderServlet.class);

    private final KafkaDispatcher orderDispatcher = new KafkaDispatcher<Order>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            var orderId = req.getParameter("uuid");
            var amount = new BigDecimal(req.getParameter("amount"));
            var email = req.getParameter("email");
            var order = new Order(orderId, email, amount);

            try(var dataBase = new OrderDataBase()) {
                if (dataBase.saveNewOrder(order)) {
                    LOGGER.info("GERANDO NOVA VENDA: {}", order);

                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, new Correlationid(NewOrderServlet.class.getSimpleName()), order);
                    LOGGER.info("NEW ORDEM SENT SUCCESSFULLY");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("NEW ORDEM SENT SUCCESSFULLY");
                } else {
                    LOGGER.info("OLD ORDER RECEIVED");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("OLD ORDER RECEIVED");
                }
            }
        } catch (ExecutionException | InterruptedException | IOException | SQLException e) {
            LOGGER.error("ERRO WHEN SEND ORDER: {}", e.getMessage());
            throw new ServletException(e);
        }
    }
}
