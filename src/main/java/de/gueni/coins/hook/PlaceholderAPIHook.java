package de.gueni.coins.hook;

import de.gueni.coins.CoinPlugin;
import de.gueni.coins.user.CoinUser;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final CoinPlugin plugin;

    public PlaceholderAPIHook( CoinPlugin plugin ) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "gcoinapi";
    }

    @Override
    public @Nullable String getRequiredPlugin() {
        return null;
    }

    @Override
    public @NotNull String getAuthor() {
        return "GueniPlayz";
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest( Player player, @NotNull String params ) {
        CoinUser user = CoinUser.getUser( player );

        // %gcoinapi_coins
        if( params.equals( "coins" )){
            return String.valueOf( user.getCoins() );
        }

        return null;
    }
}
