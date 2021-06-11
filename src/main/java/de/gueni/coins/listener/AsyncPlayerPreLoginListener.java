package de.gueni.coins.listener;

import de.gueni.coins.CoinPlugin;
import de.gueni.coins.user.CoinUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class AsyncPlayerPreLoginListener implements Listener {

    private final CoinPlugin plugin;

    public AsyncPlayerPreLoginListener( CoinPlugin plugin ) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents( this, plugin );
    }

    @EventHandler
    public void handleAsyncPlayerPreLogin( AsyncPlayerPreLoginEvent event ) {
        UUID uuid = event.getUniqueId();
        CoinUser user = CoinUser.getUserByUUID( uuid );

        long start = System.currentTimeMillis();

        // we start by loading our user and setting him the associated data with him
        user.setLoaded( new AtomicBoolean( false ) );

        do {

            this.plugin.getCoinHandler().register( uuid );
            user.setCoins( this.plugin.getCoinHandler().getCoinsSync( uuid ) );
            user.setLoaded( new AtomicBoolean( true ) );

        } while ( !user.getLoaded().get() );

        this.plugin.getLogger().log( Level.INFO, String.format( "Loading time of user %s took %sms", user.getUUID(), System.currentTimeMillis() - start ) );

    }

}
