package de.gueni.coins.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {

    private final String host, database, username, password;
    private final int port;
    private HikariDataSource dataSource;

    public DBConnection( String host, int port, String database, String username, String password ) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName( "com.mysql.cj.jdbc.Driver" );
        hikariConfig.setJdbcUrl( "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true&useSSL=false" );
        hikariConfig.setMaximumPoolSize( 10 );
        hikariConfig.setUsername( this.username );
        hikariConfig.setPassword( this.password );
        this.dataSource = new HikariDataSource( hikariConfig );
    }

    public void disconnect() {
        if ( this.isConnected() ) {
            this.dataSource.close();
        }
    }


    /**
     * gets the connection from the datasource
     * @return the connection if its not null
     */
    public Connection getConnection() {
        if ( !isConnected() )
            connect();

        try {
            return this.getDataSource().getConnection();
        } catch ( SQLException exception ) {
            exception.printStackTrace();
        }
        return null;
    }

    public boolean isConnected() {
        return this.dataSource != null;
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }
}
