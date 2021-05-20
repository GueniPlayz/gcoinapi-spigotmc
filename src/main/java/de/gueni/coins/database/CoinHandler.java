package de.gueni.coins.database;

import de.gueni.coins.CoinPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CoinHandler {

    private final DBConnection dbConnection;

    public CoinHandler( CoinPlugin plugin ) {
        this.dbConnection = plugin.getDbConnection();
    }

    public void createTable() {
        try ( Connection connection = this.dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement( "CREATE TABLE IF NOT EXISTS coin_table (uuid CHAR(36), coins DOUBLE)" ) ) {

            preparedStatement.executeUpdate();

        } catch ( SQLException exception ) {
            exception.printStackTrace();
        }
    }

    public void register( UUID uuid ) {
        if ( !isRegistered( uuid ) ) {
            try ( Connection connection = this.dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement( "INSERT INTO coin_table (uuid, coins) VALUES (?, ?)" ) ) {

                preparedStatement.setString( 1, uuid.toString() );
                preparedStatement.setDouble( 2, 0.0 );

                preparedStatement.executeUpdate();

            } catch ( SQLException exception ) {
                exception.printStackTrace();
            }
        }
    }

    public void setCoins( UUID uuid, double coins ) {
        try ( Connection connection = this.dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement( "UPDATE coin_table SET coins= ? WHERE uuid= ?" ) ) {

            preparedStatement.setDouble( 1, coins );
            preparedStatement.setString( 2, uuid.toString() );

            preparedStatement.executeUpdate();

        } catch ( SQLException exception ) {
            exception.printStackTrace();
        }
    }

    public double getCoins( UUID uuid ) {
        try {
            return CompletableFuture.supplyAsync( () -> getCoinsSync( uuid ) ).get();
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        return -1;
    }

    private double getCoinsSync( UUID uuid ) {
        try ( Connection connection = this.dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement( "SELECT coins FROM coin_table WHERE uuid = ?" ) ) {
            preparedStatement.setString( 1, uuid.toString() );

            ResultSet resultSet = preparedStatement.executeQuery();

            if ( resultSet.next() ) {
                return resultSet.getDouble( "coins" );
            }

        } catch ( SQLException exception ) {
            exception.printStackTrace();
        }
        return -1;
    }

    private boolean isRegistered( UUID uuid ) {
        try {
            return CompletableFuture.supplyAsync( () -> isRegisteredSync( uuid ) ).get();
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isRegisteredSync( UUID uuid ) {
        try ( Connection connection = this.dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement( "SELECT * FROM coin_table WHERE uuid= ?" ) ) {
            preparedStatement.setString( 1, uuid.toString() );

            ResultSet resultSet = preparedStatement.executeQuery();

            if ( resultSet.next() ) {
                return resultSet.getString( "uuid" ) != null;
            }
            return false;
        } catch ( SQLException exception ) {
            exception.printStackTrace();
            return false;
        }
    }

}
