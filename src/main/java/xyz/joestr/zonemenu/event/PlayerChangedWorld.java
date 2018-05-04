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
		this.plugin.findLocations.remove(event.getPlayer());
		this.plugin.worlds.remove(event.getPlayer());
		this.plugin.selectedFirstLocations.remove(event.getPlayer());
		this.plugin.selectedSecondLocations.remove(event.getPlayer());
		if(this.plugin.worldEditPlugin.getSession(event.getPlayer()).getSelectionWorld() != null) {
			this.plugin.worldEditPlugin.getSession(event.getPlayer()).getRegionSelector(this.plugin.worldEditPlugin.getSession(event.getPlayer()).getSelectionWorld()).clear();
		}
		this.plugin.resetBeaconCorner(event.getPlayer(), this.plugin.beaconCorner1);
		this.plugin.resetBeaconCorner(event.getPlayer(), this.plugin.beaconCorner2);
		this.plugin.resetBeaconCorner(event.getPlayer(), this.plugin.beaconCorner3);
		this.plugin.resetBeaconCorner(event.getPlayer(), this.plugin.beaconCorner4);
	}
}
