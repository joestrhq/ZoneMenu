package xyz.joestr.zonemenu.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.joestr.zonemenu.ZoneMenu;

public class PlayerQuit implements Listener {

	private ZoneMenu plugin;

	public PlayerQuit(ZoneMenu zonemenu) {

		this.plugin = zonemenu;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {

		// Clean up player
		this.plugin.tool.remove(event.getPlayer());
		this.plugin.findLocations.remove(event.getPlayer());
		this.plugin.worlds.remove(event.getPlayer());
		this.plugin.selectedFirstLocations.remove(event.getPlayer());
		this.plugin.selectedSecondLocations.remove(event.getPlayer());
		// Next actions are not necessary to perform
		// this.plugin.worldeditplugin.getSession(event.getPlayer()).getRegionSelector(plugin.worldeditplugin.getSession(event.getPlayer()).getSelectionWorld()).clear();
		// this.plugin.resetCorner(event.getPlayer(), this.plugin.corner1);
		// this.plugin.resetCorner(event.getPlayer(), this.plugin.corner2);
		// this.plugin.resetCorner(event.getPlayer(), this.plugin.corner3);
		// this.plugin.resetCorner(event.getPlayer(), this.plugin.corner4);
	}
}
