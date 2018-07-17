package xyz.joestr.zonemenu.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import xyz.joestr.zonemenu.ZoneMenu;

/**
 * Event listener which handles world changes
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

		this.plugin.clearUpZoneMenuPlayer(event.getPlayer());
	}
}
