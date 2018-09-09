package xyz.joestr.zonemenu.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.joestr.zonemenu.ZoneMenu;

/**
 * Event listener which handles game quits of players
 * 
 * @author joestr
 * @since build_1
 * @version ${project.version}
 */
public class PlayerQuit implements Listener {

    private ZoneMenu plugin;

    public PlayerQuit(ZoneMenu zonemenu) {

        this.plugin = zonemenu;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        this.plugin.clearUpZoneMenuPlayer(event.getPlayer());
    }
}
