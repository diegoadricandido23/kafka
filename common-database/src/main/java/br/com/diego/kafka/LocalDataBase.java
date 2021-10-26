package br.com.diego.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class LocalDataBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDataBase.class);
    private final Connection connection;

    public LocalDataBase(final String nome) throws SQLException {
        String url = "jdbc:sqlite:service-users/target/"+ nome +".db";
        this.connection = DriverManager.getConnection(url);

    }

    public void createIfNotExistis(final String sql) {
        try {
            connection.createStatement().execute(sql);

        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
    public static void main(String[] args) {
    }

    public void update(String sql, String... params) throws SQLException {
        prepare(sql, params).execute();
    }

    public ResultSet query(String sql, String...params) throws SQLException {
        return prepare(sql, params).executeQuery();
    }

    private PreparedStatement prepare(String sql, String[] params) throws SQLException {
        var prepareStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            prepareStatement.setString(i + 1, params[i]);
        }
        return prepareStatement;
    }
}
