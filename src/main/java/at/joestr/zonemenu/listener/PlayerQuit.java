package at.joestr.zonemenu.listener;

import com.sk89q.worldedit.IncompleteRegionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import at.joestr.zonemenu.ZoneMenu;

/**
 * Event listener which handles game quits of players
 *
 * @author joestr
 * @since build_1
 * @version ${project.version}
 */
public class PlayerQuit implements Listener {

    private ZoneMenu zoneMenuPlugin;

    public PlayerQuit(ZoneMenu zonemenu) {

        this.zoneMenuPlugin = zonemenu;
        this.zoneMenuPlugin.getServer().getPluginManager().registerEvents(this, this.zoneMenuPlugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        this.zoneMenuPlugin.clearUpZoneMenuPlayer(event.getPlayer());
    }
}
