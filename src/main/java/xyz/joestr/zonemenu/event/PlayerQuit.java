package xyz.joestr.zonemenu.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.joestr.zonemenu.ZoneMenu;

/**
 * Event listener which handles game quits of players
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

		// Clean up player
		this.plugin.toolType.remove(event.getPlayer());
		this.plugin.signType.remove(event.getPlayer());
		this.plugin.findLocations.remove(event.getPlayer());
		this.plugin.createWorlds.remove(event.getPlayer());
		this.plugin.createFirstLocations.remove(event.getPlayer());
		this.plugin.createSecondLocations.remove(event.getPlayer());
		this.plugin.subcreateWorlds.remove(event.getPlayer());
		this.plugin.subcreateFirstLocations.remove(event.getPlayer());
		this.plugin.subcreateSecondLocations.remove(event.getPlayer());

		// Next actions are not necessary to perform
		//if (this.plugin.worldEditPlugin.getSelection(event.getPlayer()) != null) {
		//	this.plugin.worldEditPlugin.getSelection(event.getPlayer()).getRegionSelector().clear();
		//}
		//this.plugin.resetBeaconCorner(event.getPlayer(), this.plugin.createCorner1);
		//this.plugin.resetBeaconCorner(event.getPlayer(), this.plugin.createCorner2);
		//this.plugin.resetBeaconCorner(event.getPlayer(), this.plugin.createCorner3);
		//this.plugin.resetBeaconCorner(event.getPlayer(), this.plugin.createCorner4);
		//this.plugin.resetSubcreateCorner(event.getPlayer(), this.plugin.subcreateCorner1);
		//this.plugin.resetSubcreateCorner(event.getPlayer(), this.plugin.subcreateCorner2);
	}
}
