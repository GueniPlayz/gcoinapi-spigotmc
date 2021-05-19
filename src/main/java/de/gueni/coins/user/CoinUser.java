package de.gueni.coins.user;

import de.gueni.coins.CoinPlugin;
import de.gueni.coins.events.CoinsChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class CoinUser {

    private static final Map<UUID, CoinUser> COIN_USER_MAP = new HashMap<>();
    private AtomicBoolean loaded;
    private UUID uuid;
    private double coins;

    public CoinUser( UUID uuid ) {
        this.uuid = uuid;
    }

    /**
     * @param player the object of the player
     * @return the user object of the given player
     */
    public static CoinUser getUser( Player player ) {
        if ( !COIN_USER_MAP.containsKey( player.getUniqueId() ) )
            COIN_USER_MAP.put( player.getUniqueId(), new CoinUser( player.getUniqueId() ) );
        return COIN_USER_MAP.get( player.getUniqueId() );
    }

    /**
     * @param uuid the uuid from the player
     * @return the user object of the given uuid
     */
    public static CoinUser getUserByUUID( UUID uuid ) {
        if ( !COIN_USER_MAP.containsKey( uuid ) )
            COIN_USER_MAP.put( uuid, new CoinUser( uuid ) );
        return COIN_USER_MAP.get( uuid );
    }

    /**
     * The map with all users that are currently online
     *
     * @return Map with all users in it
     */
    public static Map<UUID, CoinUser> getCoinUserMap() {
        return COIN_USER_MAP;
    }

    /**
     * removes the user from the user map
     *
     * @param player the player that should be removed
     */
    public void deleteUser( Player player ) {
        COIN_USER_MAP.remove( player.getUniqueId() );
    }

    public AtomicBoolean getLoaded() {
        return loaded;
    }

    // AtomicBoolean that is used in the login progess of the user
    public void setLoaded( AtomicBoolean loaded ) {
        this.loaded = loaded;
    }


    /**
     * checks if the players account balance is higher than {@code coins}
     *
     * @param coins the amount of coins that should be checked
     * @return true if the given amount is higher otherwise false
     */
    public boolean hasEnoughCoins( double coins ) {
        return getCoins() >= coins;
    }

    /**
     * adds coins to the players account
     *
     * @param coins the amount of coins that should be added
     */
    public void addCoins( double coins ) {
        setCoins( getCoins() + coins );
    }

    /**
     * removes coins from the players account
     *
     * @param coins the amount of coins that should be removed
     */
    public void removeCoins( double coins ) {
        setCoins( getCoins() - coins );
    }

    /**
     * gets the players coins
     *
     * @return double the coins of the player
     */
    public double getCoins() {
        return coins;
    }

    /**
     * set the coins of the player
     *
     * @param coins the amount of coins that should be set
     */
    public void setCoins( double coins ) {
        this.coins = coins;
        // Calling the task synchronously otherwise the AsyncPlayerPreLoginEvent will throw errors
        Bukkit.getScheduler().runTask( CoinPlugin.getPlugin( CoinPlugin.class ), () -> Bukkit.getPluginManager().callEvent( new CoinsChangeEvent( uuid, coins ) ) );
    }

    /**
     * gets the uuid of the user
     *
     * @return the uuid of the current user
     */
    public UUID getUUID() {
        return uuid;
    }
}
