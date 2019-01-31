package xyz.joestr.zonemenu.listener;

import com.sk89q.worldedit.IncompleteRegionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import xyz.joestr.zonemenu.ZoneMenu;

/**
 * Event listener which handles world changes
 *
 * @author joestr
 * @since build_1
 * @version ${project.version}
 */
public class PlayerChangedWorld implements Listener {

    private ZoneMenu zoneMenuPlugin;

    public PlayerChangedWorld(ZoneMenu zonemenu) {

        this.zoneMenuPlugin = zonemenu;
        this.zoneMenuPlugin.getServer().getPluginManager().registerEvents(this, this.zoneMenuPlugin);
    }

    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent event) {

        this.zoneMenuPlugin.clearUpZoneMenuPlayer(event.getPlayer());
    }
}
