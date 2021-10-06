package br.com.diego.kafka;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpEcommerceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpEcommerceService.class);

    public static void main(String[] args) throws Exception {
        LOGGER.info("INICIANDO HTTP-ECOMMERCE-SERVICE");
        var server = new Server(8080);

        var context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new NewOrderServlet()), "/new");

        server.setHandler(context);
        server.start();
        server.join();
    }
}
