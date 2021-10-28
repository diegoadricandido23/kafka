package br.com.diego.kafka;

public class SQLConstantes {
    public static final String DATABASE_NAME = "users_database";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Users ("
            + "uuid varchar (200) primary key, "
            + "email varchar(200))";
    public static final String SELECT_ALL_USERS = "SELECT UUID FROM USERS";
    public static final String INSERT_USER = "INSERT INTO USERS (uuid, email) VALUES (?,?)";
    public static final String SELECT_USER_ID = "SELECT uuid FROM USERS WHERE email =? LIMIT 1";
}
