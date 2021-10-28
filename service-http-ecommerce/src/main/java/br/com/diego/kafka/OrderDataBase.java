package br.com.diego.kafka;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

public class OrderDataBase implements Closeable {

    private final LocalDataBase dataBase;

    OrderDataBase() throws SQLException {
        this.dataBase = new LocalDataBase("orders_database");
        this.dataBase.createIfNotExistis("CREATE TABLE IF NOT EXISTS Orders ("
                + "uuid varchar (200) primary key)");
    }

    public boolean saveNewOrder(Order order) throws SQLException {
        if(wasProcessed(order)) {
            return false;
        }
        dataBase.update("insert into Ordes (uuid) values (?)", order.getOrderId());
        return true;
    }

    private boolean wasProcessed(Order order) throws SQLException {
        var results = dataBase.query("select uuid from Orders where uuid = ? limit 1", order.getOrderId());
        return results.next();
    }

    @Override
    public void close() throws IOException {
        try {
            dataBase.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
