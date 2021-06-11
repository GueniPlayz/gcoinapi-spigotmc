package de.gueni.coins.listener;

import de.gueni.coins.CoinPlugin;
import de.gueni.coins.user.CoinUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

public class PluginEnableListener implements Listener {

    private final CoinPlugin plugin;

    public PluginEnableListener( CoinPlugin plugin ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handlePluginEnable( PluginEnableEvent event ) {

        // in case of reload we get all current online players and "convert" them into CoinUsers then we set them their coins from the database
        if ( event.getPlugin().getName().equals( this.plugin.getDescription().getName() ) ) {
            this.plugin.getServer().getScheduler().runTaskAsynchronously( this.plugin, () -> {
                for ( Player player : Bukkit.getOnlinePlayers() ) {
                    CoinUser user = CoinUser.getUser( player );
                    user.setCoins( this.plugin.getCoinHandler().getCoinsSync( player.getUniqueId() ) );
                }
            } );
        }

    }
}
