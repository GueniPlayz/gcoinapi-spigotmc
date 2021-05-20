package de.gueni.coins.listener;

import de.gueni.coins.CoinPlugin;
import de.gueni.coins.user.CoinUser;
import net.kyori.adventure.text.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final CoinPlugin plugin;

    public PlayerQuitListener( CoinPlugin plugin ) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents( this, plugin );
    }

    @EventHandler
    public void onQuit( PlayerQuitEvent event ) {
        Player player = event.getPlayer();
        CoinUser user = CoinUser.getUser( player );

        // we insert the users coins into the database and removing him finally from the map
        this.plugin.getCoinHandler().setCoins( user.getUUID(), user.getCoins() );
        user.deleteUser( player );
    }
}
