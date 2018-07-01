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

		// Player changed world, so clean up all
		this.plugin.toolType.remove(event.getPlayer());
		this.plugin.signType.remove(event.getPlayer());
		this.plugin.findLocations.remove(event.getPlayer());
		this.plugin.createWorlds.remove(event.getPlayer());
		this.plugin.createFirstLocations.remove(event.getPlayer());
		this.plugin.createSecondLocations.remove(event.getPlayer());
		this.plugin.subcreateWorlds.remove(event.getPlayer());
		this.plugin.subcreateFirstLocations.remove(event.getPlayer());
		this.plugin.subcreateSecondLocations.remove(event.getPlayer());

		if (this.plugin.worldEditPlugin.getSelection(event.getPlayer()) != null) {
			this.plugin.worldEditPlugin.getSelection(event.getPlayer()).getRegionSelector().clear();
		}

		this.plugin.resetBeaconCorner(event.getPlayer(), this.plugin.createCorner1);
		this.plugin.resetBeaconCorner(event.getPlayer(), this.plugin.createCorner2);
		this.plugin.resetBeaconCorner(event.getPlayer(), this.plugin.createCorner3);
		this.plugin.resetBeaconCorner(event.getPlayer(), this.plugin.createCorner4);
		this.plugin.resetSubcreateCorner(event.getPlayer(), this.plugin.subcreateCorner1);
		this.plugin.resetSubcreateCorner(event.getPlayer(), this.plugin.subcreateCorner2);
	}
}
