package de.gueni.coins.database;

import de.gueni.coins.CoinPlugin;
import de.gueni.coins.events.CoinsChangeEvent;
import de.gueni.coins.user.CoinUser;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CoinHandler {

    private final DBConnection dbConnection;
    private final CoinPlugin plugin;

    public CoinHandler( CoinPlugin plugin ) {
        this.plugin = plugin;
        this.dbConnection = plugin.getDbConnection();
    }

    /**
     * creates the table (coin_table) with uuid CHAR(36) and coins DECIMAL(13, 2)
     */
    public void createTable() {
        try ( Connection connection = this.dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement( "CREATE TABLE IF NOT EXISTS coin_table (uuid CHAR(36), coins DECIMAL(13, 2))" ) ) {

            preparedStatement.executeUpdate();

        } catch ( SQLException exception ) {
            exception.printStackTrace();
        }
    }

    /**
     * registers a player in the database if he does not exist
     *
     * @param uuid the uuid that should be registered
     */
    public void register( UUID uuid ) {
        try {
            if ( !isRegistered( uuid ).get() ) {
                try ( Connection connection = this.dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement( "INSERT INTO coin_table (uuid, coins) VALUES (?, ?)" ) ) {

                    preparedStatement.setString( 1, uuid.toString() );
                    preparedStatement.setDouble( 2, 0.0 );

                    preparedStatement.executeUpdate();

                } catch ( SQLException exception ) {
                    exception.printStackTrace();
                }
            }
        } catch ( ExecutionException | InterruptedException e ) {
            e.printStackTrace();
        }
    }

    /**
     * insert all coins of the users that are currently online to the database
     */
    public void updateCoinsForAllPlayers() {
        for ( CoinUser user : CoinUser.getCoinUserMap().values() ) {
            this.setCoinsWithoutEvent( user.getUUID(), user.getCoins() );
        }
    }

    // this method is created because some users think reloading is a good idea and this method is the quickest and easiest? workaround
    private void setCoinsWithoutEvent( UUID uuid, double coins ) {
        try ( Connection connection = this.dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement( "UPDATE coin_table SET coins= ? WHERE uuid= ?" ) ) {

            preparedStatement.setDouble( 1, coins );
            preparedStatement.setString( 2, uuid.toString() );

            preparedStatement.executeUpdate();

        } catch ( SQLException exception ) {
            exception.printStackTrace();
        }
    }

    /**
     * inserts the given amount of {@code coins} to the database
     *
     * @param uuid  the uuid that should receive the coins
     * @param coins the coins that should be inserted into the database
     */
    public void setCoins( UUID uuid, double coins ) {
        try ( Connection connection = this.dbConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement( "UPDATE coin_table SET coins= ? WHERE uuid= ?" ) ) {

            preparedStatement.setDouble( 1, coins );
            preparedStatement.setString( 2, uuid.toString() );

            preparedStatement.executeUpdate();

        } catch ( SQLException exception ) {
            exception.printStackTrace();
        }
        Bukkit.getScheduler().runTask( this.plugin, () -> Bukkit.getPluginManager().callEvent( new CoinsChangeEvent( uuid, coins, CoinsChangeEvent.ChangeType.DATABASE ) ) );
    }

    /**
     * gets the coins of the given uuid
     *
     * @param uuid the uuid that the coins should be retrieved from
     * @return the coins of the user if there is no error otherwise -1
     */
    public CompletableFuture<Double> getCoinsAsync( UUID uuid ) {
        return CompletableFuture.supplyAsync( () -> getCoinsSync( uuid ) );
    }

    public double getCoinsSync( UUID uuid ) {
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

    /**
     * checks if the user is registered
     *
     * @param uuid the uuid of the player that should be registered
     * @return true if he is registered otherwise false
     */
    public Future<Boolean> isRegistered( UUID uuid ) {
        return CompletableFuture.supplyAsync( () -> isRegisteredSync( uuid ) );
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
