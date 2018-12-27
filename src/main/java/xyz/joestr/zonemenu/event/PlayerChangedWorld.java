package xyz.joestr.zonemenu.event;

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

    private ZoneMenu plugin;

    public PlayerChangedWorld(ZoneMenu zonemenu) {

        this.plugin = zonemenu;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent event) {

        try {
            this.plugin.clearUpZoneMenuPlayer(event.getPlayer());
        } catch (IncompleteRegionException ex) {
            Logger.getLogger(PlayerChangedWorld.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
