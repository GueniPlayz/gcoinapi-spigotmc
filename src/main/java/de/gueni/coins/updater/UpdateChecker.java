package de.gueni.coins.updater;

import de.gueni.coins.CoinPlugin;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

public class UpdateChecker {

    private final CoinPlugin plugin;
    private final int resourceID;

    public UpdateChecker( CoinPlugin plugin, int resourceID ) {
        this.plugin = plugin;
        this.resourceID = resourceID;
    }

    public void getVersion( Consumer<String> consumer ) {
        Bukkit.getScheduler().runTaskAsynchronously( this.plugin, () -> {
            try( InputStream inputStream = new URL( "https.//api.spigotmc.org/legacy/update.php?resource=" + this.resourceID ).openStream(); Scanner scanner = new Scanner( inputStream ) ) {
                if( scanner.hasNext() ) {
                    consumer.accept( scanner.next() );
                }
            } catch ( IOException exception ) {
                this.plugin.getLogger().log( Level.INFO, "Can't look for updates: " + exception.getMessage() );
            }
        } );
    }
}
