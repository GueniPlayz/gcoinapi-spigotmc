package de.gueni.coins;

import de.gueni.coins.database.CoinHandler;
import de.gueni.coins.database.DBConnection;
import de.gueni.coins.hook.VaultHook;
import de.gueni.coins.listener.AsyncPlayerPreLoginListener;
import de.gueni.coins.listener.PlayerQuitListener;
import de.gueni.coins.user.CoinUser;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

@Getter
public class CoinPlugin extends JavaPlugin {

    private DBConnection dbConnection;
    private CoinHandler coinHandler;

    @Override
    public void onEnable() {
        setup();
    }

    @Override
    public void onDisable() {
        // Disconnecting from database
        this.dbConnection.disconnect();
    }

    // TODO: hook placeholder api into the plugin
    // TODO: comment CoinHandler.java

    private void setup() {
        this.saveDefaultConfig();

        // Creating new instance of DBConnection with mysql login credentials
        this.dbConnection = new DBConnection(
                getConfig().getString( "mysql.host" ),
                getConfig().getInt( "mysql.port" ),
                getConfig().getString( "mysql.database" ),
                getConfig().getString( "mysql.username" ),
                getConfig().getString( "mysql.password" )
        );

        // Connection to database with given credentials
        this.dbConnection.connect();

        // Checking if the plugin is not connected to mysql
        // If yes we disable the plugin
        if ( !this.dbConnection.isConnected() ) {
            this.getLogger().log( Level.WARNING, "CoinAPI needs mysql to work!" );
            Bukkit.getPluginManager().disablePlugin( this );
            return;
        }

        // Hooking Vault into the plugin
        if ( getConfig().getBoolean( "settings.vault" ) ) {
            Bukkit.getServicesManager().register( Economy.class, new VaultHook( this ), this, ServicePriority.Normal );
            this.getLogger().log( Level.FINE, "Hooked vault into the plugin!" );
        }

        // Creating new instance of CoinHandler and creating the table
        this.coinHandler = new CoinHandler( this );
        coinHandler.createTable();


        // Registering listeners
        new AsyncPlayerPreLoginListener( this );
        new PlayerQuitListener( this );

        this.getLogger().log( Level.FINE, "Plugin started correctly!" );

        // Starting task that updates every x-ticks players coins
        startTask();
    }

    /*
    Task that writes all players coins into the database
    This prevents if the server crashes a lot of data is lost
     */
    private void startTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously( this, () -> {
            for ( CoinUser coinUser : CoinUser.getCoinUserMap().values() ) {
                coinHandler.setCoins( coinUser.getUUID(), coinUser.getCoins() );
            }
        }, this.getConfig().getLong( "settings.update_interval.delay" ), this.getConfig().getLong( "settings.update_interval.period" ) );
    }

}

