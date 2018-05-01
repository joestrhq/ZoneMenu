package xyz.joestr.zonemenu.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import xyz.joestr.zonemenu.ZoneMenu;

public class PlayerChangedWorld implements Listener {

	private ZoneMenu plugin;

	public PlayerChangedWorld(ZoneMenu zonemenu) {

		this.plugin = zonemenu;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onChangedWorld(PlayerChangedWorldEvent event) {
		
		// Player changed world, so clean up all except tool-map
		// plugin.Tool.remove(event.getPlayer().getName());
		this.plugin.findlocations.remove(event.getPlayer().getName());
		this.plugin.worlds.remove(event.getPlayer().getName());
		this.plugin.firstlocations.remove(event.getPlayer().getName());
		this.plugin.secondlocations.remove(event.getPlayer().getName());
		if(this.plugin.worldeditplugin.getSession(event.getPlayer()).getSelectionWorld() != null) {
			this.plugin.worldeditplugin.getSession(event.getPlayer()).getRegionSelector(this.plugin.worldeditplugin.getSession(event.getPlayer()).getSelectionWorld()).clear();
		}
		this.plugin.resetCorner(event.getPlayer(), this.plugin.corner1);
		this.plugin.resetCorner(event.getPlayer(), this.plugin.corner2);
		this.plugin.resetCorner(event.getPlayer(), this.plugin.corner3);
		this.plugin.resetCorner(event.getPlayer(), this.plugin.corner4);
	}
}
