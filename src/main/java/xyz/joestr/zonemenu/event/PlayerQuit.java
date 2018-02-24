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

		this.plugin.tool.remove(event.getPlayer().getName());
		this.plugin.findlocations.remove(event.getPlayer().getName());
		this.plugin.worlds.remove(event.getPlayer().getName());
		this.plugin.firstlocations.remove(event.getPlayer().getName());
		this.plugin.secondlocations.remove(event.getPlayer().getName());
		// this.plugin.worldeditplugin.getSession(event.getPlayer()).getRegionSelector(plugin.worldeditplugin.getSession(event.getPlayer()).getSelectionWorld()).clear();
		// this.plugin.resetCorner(event.getPlayer(), this.plugin.corner1);
		// this.plugin.resetCorner(event.getPlayer(), this.plugin.corner2);
		// this.plugin.resetCorner(event.getPlayer(), this.plugin.corner3);
		// this.plugin.resetCorner(event.getPlayer(), this.plugin.corner4);
	}
}
