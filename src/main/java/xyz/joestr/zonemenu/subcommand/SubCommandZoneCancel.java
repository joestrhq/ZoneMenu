package xyz.joestr.zonemenu.subcommand;

import org.bukkit.entity.Player;

import xyz.joestr.zonemenu.ZoneMenu;

public class SubCommandZoneCancel {
	
	ZoneMenu plugin = null;
	
	public SubCommandZoneCancel(ZoneMenu plugin) {
		this.plugin = plugin;
	}
	
	public void process(Player player) {
		// Check if player is in map
		if (!this.plugin.tool.containsKey(player)) {

			player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
			player.sendMessage(this.plugin.colorCode('&',
					(String) this.plugin.configDelegate.getMap().get("zone_cancel_not_running")));

			return;
		}

		// Clean up user
		this.plugin.tool.remove(player);
		this.plugin.findLocations.remove(player);
		this.plugin.worlds.remove(player);
		this.plugin.selectedFirstLocations.remove(player);
		this.plugin.selectedSecondLocations.remove(player);
		
		if(this.plugin.worldEditPlugin.getSession(player).getSelectionWorld() != null) {
			this.plugin.worldEditPlugin.getSession(player).getRegionSelector(this.plugin.worldEditPlugin.getSession(player).getSelectionWorld()).clear();
		}

		// Reset beacons
		this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner1);
		this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner2);
		this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner3);
		this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner4);

		player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
		player.sendMessage(
				this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("zone_cancel")));
	}
}
