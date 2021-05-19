package de.gueni.coins.hook;

import de.gueni.coins.CoinPlugin;
import de.gueni.coins.user.CoinUser;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class VaultHook extends AbstractEconomy {

    private final CoinPlugin plugin;

    public VaultHook( CoinPlugin plugin ) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return this.plugin.isEnabled();
    }

    @Override
    public String getName() {
        return this.plugin.getDescription().getName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format( double v ) {
        return NumberFormat.getInstance().format( v );
    }

    @Override
    public String currencyNamePlural() {
        return "";
    }

    @Override
    public String currencyNameSingular() {
        return "";
    }

    @Override
    public boolean hasAccount( String name ) {
        return hasAccount( Bukkit.getOfflinePlayer( name ) );
    }

    @Override
    public boolean hasAccount( String name, String s1 ) {
        return hasAccount( Bukkit.getOfflinePlayer( name ) );
    }

    @Override
    public boolean hasAccount( OfflinePlayer player ) {
        CoinUser user = CoinUser.getUserByUUID( player.getUniqueId() );
        return user != null;
    }

    @Override
    public boolean hasAccount( OfflinePlayer player, String worldName ) {
        return hasAccount( player );
    }

    @Override
    public double getBalance( String s ) {
        return getBalance( Bukkit.getOfflinePlayer( s ) );
    }

    @Override
    public double getBalance( String s, String s1 ) {
        return getBalance( Bukkit.getOfflinePlayer( s ) );
    }

    @Override
    public double getBalance( OfflinePlayer player, String world ) {
        return getBalance( player );
    }

    @Override
    public double getBalance( OfflinePlayer player ) {
        return CoinUser.getUserByUUID( player.getUniqueId() ).getCoins();
    }

    @Override
    public boolean has( String s, double v ) {
        return has( Bukkit.getOfflinePlayer( s ), v );
    }

    @Override
    public boolean has( String s, String s1, double v ) {
        return has( Bukkit.getOfflinePlayer( s ), v );
    }

    @Override
    public boolean has( OfflinePlayer player, String worldName, double amount ) {
        return has( player, amount );
    }

    @Override
    public boolean has( OfflinePlayer player, double amount ) {
        return CoinUser.getUserByUUID( player.getUniqueId() ).hasEnoughCoins( amount );
    }

    @Override
    public EconomyResponse withdrawPlayer( String s, double v ) {
        return withdrawPlayer( Bukkit.getOfflinePlayer( s ), v );
    }

    @Override
    public EconomyResponse withdrawPlayer( String s, String s1, double v ) {
        return withdrawPlayer( Bukkit.getOfflinePlayer( s ), v );
    }

    @Override
    public EconomyResponse withdrawPlayer( OfflinePlayer player, String worldName, double amount ) {
        return withdrawPlayer( player, amount );
    }

    @Override
    public EconomyResponse withdrawPlayer( OfflinePlayer player, double amount ) {
        CoinUser user = CoinUser.getUserByUUID( player.getUniqueId() );

        if ( amount <= 0 ) {
            return new EconomyResponse( 0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative coins!" );
        }

        if ( !user.hasEnoughCoins( amount ) ) {
            return new EconomyResponse( 0, 0, EconomyResponse.ResponseType.FAILURE, "Player has not enough coins!" );
        }

        user.removeCoins( amount );
        return new EconomyResponse( amount, user.getCoins(), EconomyResponse.ResponseType.SUCCESS, "" );
    }

    @Override
    public EconomyResponse depositPlayer( String s, double v ) {
        return depositPlayer( Bukkit.getOfflinePlayer( s ), v );
    }

    @Override
    public EconomyResponse depositPlayer( String s, String s1, double v ) {
        return depositPlayer( Bukkit.getOfflinePlayer( s ), v );
    }

    @Override
    public EconomyResponse depositPlayer( OfflinePlayer player, double amount ) {
        CoinUser user = CoinUser.getUserByUUID( player.getUniqueId() );
        user.addCoins( amount );

        return new EconomyResponse( amount, user.getCoins(), EconomyResponse.ResponseType.SUCCESS, "" );
    }

    @Override
    public EconomyResponse depositPlayer( OfflinePlayer player, String worldName, double amount ) {
        return depositPlayer( player, amount );
    }

    @Override
    public EconomyResponse createBank( String s, String s1 ) {
        return new EconomyResponse( 0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CoinAPI does not support bank accounts!" );
    }

    @Override
    public EconomyResponse deleteBank( String s ) {
        return new EconomyResponse( 0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CoinAPI does not support bank accounts!" );
    }

    @Override
    public EconomyResponse bankBalance( String s ) {
        return new EconomyResponse( 0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CoinAPI does not support bank accounts!" );
    }

    @Override
    public EconomyResponse bankHas( String s, double v ) {
        return new EconomyResponse( 0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CoinAPI does not support bank accounts!" );
    }

    @Override
    public EconomyResponse bankWithdraw( String s, double v ) {
        return new EconomyResponse( 0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CoinAPI does not support bank accounts!" );
    }

    @Override
    public EconomyResponse bankDeposit( String s, double v ) {
        return new EconomyResponse( 0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CoinAPI does not support bank accounts!" );
    }

    @Override
    public EconomyResponse isBankOwner( String s, String s1 ) {
        return new EconomyResponse( 0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CoinAPI does not support bank accounts!" );
    }

    @Override
    public EconomyResponse isBankMember( String s, String s1 ) {
        return new EconomyResponse( 0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "CoinAPI does not support bank accounts!" );
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount( String s ) {
        return true;
    }

    @Override
    public boolean createPlayerAccount( String s, String s1 ) {
        return true;
    }
}
