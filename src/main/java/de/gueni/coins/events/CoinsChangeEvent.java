package de.gueni.coins.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class CoinsChangeEvent extends Event {

    private static HandlerList handlers = new HandlerList();
    private UUID uuid;
    private double changedCoins;

    public CoinsChangeEvent( UUID uuid, double changedCoins ) {
        this.uuid = uuid;
        this.changedCoins = changedCoins;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer( uuid );
    }

    public UUID getUniqueID() {
        return uuid;
    }

    public void setChangedCoins( double changedCoins ) {
        this.changedCoins = changedCoins;
    }

    public double getChangedCoins() {
        return changedCoins;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
