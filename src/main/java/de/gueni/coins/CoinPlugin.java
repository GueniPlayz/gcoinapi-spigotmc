package de.gueni.coins;

import de.gueni.coins.database.CoinHandler;
import de.gueni.coins.database.DBConnection;
import de.gueni.coins.hook.PlaceholderAPIHook;
import de.gueni.coins.hook.VaultHook;
import de.gueni.coins.listener.AsyncPlayerPreLoginListener;
import de.gueni.coins.listener.PlayerQuitListener;
import de.gueni.coins.updater.UpdateChecker;
import de.gueni.coins.user.CoinUser;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

@Getter
public class CoinPlugin extends JavaPlugin {

    private DBConnection dbConnection;
    private CoinHandler coinHandler;

    @Override
    public void onEnable() {
        this.setup();
    }

    @Override
    public void onDisable() {
        // in case of reload we get all current CoinUsers and insert their coins into the database
        this.coinHandler.updateCoinsForAllPlayers();

        // Disconnecting from database
        this.dbConnection.disconnect();
    }

    // TODO: overview auf spigotmc machen
    // TODO: System mit CoinUser erklären (für Leute die die API nutzen)
    // TODO: CoinSystem programmieren
    // TODO: Metrics einprogrammieren

    private void setup() {
        this.saveDefaultConfig();

        // Creating new instance of DBConnection with mysql login credentials
        this.dbConnection = new DBConnection(
                this.getConfig().getString( "mysql.host" ),
                this.getConfig().getInt( "mysql.port" ),
                this.getConfig().getString( "mysql.database" ),
                this.getConfig().getString( "mysql.username" ),
                this.getConfig().getString( "mysql.password" )
        );

        // Connection to database with given credentials
        this.dbConnection.connect();

        // Checking if the plugin is not connected to mysql
        // If yes we disable the plugin
        if ( !this.dbConnection.isConnected() ) {
            this.getLogger().log( Level.WARNING, "§cCoinAPI needs mysql to work!" );
            Bukkit.getPluginManager().disablePlugin( this );
            return;
        }

        // Hooking PlaceholderAPI and Vault into our plugin if enabled
        this.hook();

        // Creating new instance of CoinHandler and creating the table
        this.coinHandler = new CoinHandler( this );
        this.coinHandler.createTable();

        // Registering listeners
        new AsyncPlayerPreLoginListener( this );
        new PlayerQuitListener( this );

        this.getLogger().log( Level.INFO, "§aPlugin started correctly!" );

        // Starting task that updates every x-ticks players coins
        this.startTask();
    }

    /*
    Task that writes all players coins into the database
    This prevents if the server crashes a lot of data is lost
     */
    private void startTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously( this, () -> {

            for ( CoinUser coinUser : CoinUser.getCoinUserMap().values() ) {
                this.coinHandler.setCoins( coinUser.getUUID(), coinUser.getCoins() );

                // Generating random int so the console is not spammed with save messages
                if ( ThreadLocalRandom.current().nextInt( 6 ) == 0 ) {
                    this.getLogger().log( Level.INFO, "§aSuccessfully saved coins into the database" );
                }
            }
        }, this.getConfig().getInt( "settings.update_interval.delay" ) * 20L, this.getConfig().getInt( "settings.update_interval.period" ) * 20L );
    }

    /*
    Hooks the wanted extension into the plugin
    Can be disabled in the configuration (config.yml)
     */
    private void hook() {
        // Hooking Vault into the plugin
        if ( this.getConfig().getBoolean( "settings.vault" ) ) {
            if ( !Bukkit.getPluginManager().getPlugin( "Vault" ).isEnabled() ) {
                this.getLogger().log( Level.WARNING, "§cVault not found.. Disabling Vault-Support" );
                return;
            }
            Bukkit.getServicesManager().register( Economy.class, new VaultHook( this ), this, ServicePriority.Normal );
            this.getLogger().log( Level.INFO, "§aHooked Vault into the plugin!" );
        }

        // Hooking PlaceholderAPI into the plugin
        if ( this.getConfig().getBoolean( "settings.placeholder_api" ) ) {
            if ( !this.getServer().getPluginManager().getPlugin( "PlaceholderAPI" ).isEnabled() ) {
                this.getLogger().log( Level.WARNING, "§cPlaceholderAPI not found.. Disabling Placeholder-Support" );
                return;
            }

            new PlaceholderAPIHook( this ).register();
            this.getLogger().log( Level.INFO, "§aHooked PlaceholderAPI into the plugin!" );
        }
    }

    /*
    Checks if an update is availabe
     */
    private void checkForUpdate() {
        // TODO: LINK UND RESOURCEID EINFÜGEN
        new UpdateChecker( this, 12345 ).getVersion( version -> {
            if ( !version.equals( this.getDescription().getVersion() ) ) {
                this.getLogger().log( Level.WARNING, "§cA new update is available: v" + version );
                this.getLogger().log( Level.WARNING, "§cDownload it at: //link" );
            }
        } );
    }

}

