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
    private ChangeType changeType;

    public CoinsChangeEvent( UUID uuid, double changedCoins, ChangeType changeType ) {
        this.uuid = uuid;
        this.changedCoins = changedCoins;
        this.changeType = changeType;
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

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType( ChangeType changeType ) {
        this.changeType = changeType;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public enum ChangeType {
        USER,
        DATABASE
    }
}
