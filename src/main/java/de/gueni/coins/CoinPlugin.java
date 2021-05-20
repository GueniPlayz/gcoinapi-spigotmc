package de.gueni.coins;

import de.gueni.coins.database.CoinHandler;
import de.gueni.coins.database.DBConnection;
import de.gueni.coins.hook.PlaceholderAPIHook;
import de.gueni.coins.hook.VaultHook;
import de.gueni.coins.listener.AsyncPlayerPreLoginListener;
import de.gueni.coins.listener.PlayerQuitListener;
import de.gueni.coins.user.CoinUser;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

@Getter
public class CoinPlugin extends JavaPlugin {

    private DBConnection dbConnection;
    private CoinHandler coinHandler;

    @Override
    public void onEnable() {
        this.setup();

        // in case of reload we get all current online players and "convert" them into CoinUsers then we set them their coins from the database
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            CoinUser user = CoinUser.getUser( player );
            user.setCoins( this.coinHandler.getCoins( player.getUniqueId() ) );
        }
    }

    @Override
    public void onDisable() {

        // in case of reload we get all current CoinUsers and insert their coins into the database
        this.coinHandler.updateCoinsForAllPlayers();

        // Disconnecting from database
        this.dbConnection.disconnect();
    }

    // TODO: overview auf spigotmc machen
    // TODO: CoinSystem programmieren
    // TODO: System mit CoinUser erklären (für Leute die die API nutzen)

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
    Method that hooks the wanted extension into the plugin
    Can be disabled in the configuration
     */
    private void hook() {
        // Hooking Vault into the plugin
        if ( getConfig().getBoolean( "settings.vault" ) ) {
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

}

