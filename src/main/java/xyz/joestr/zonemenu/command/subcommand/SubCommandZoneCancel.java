package xyz.joestr.zonemenu.command.subcommand;

import org.bukkit.entity.Player;

import xyz.joestr.zonemenu.ZoneMenu;

public class SubCommandZoneCancel {

	ZoneMenu plugin = null;

	public SubCommandZoneCancel(ZoneMenu plugin) {
		this.plugin = plugin;
	}

	public void process(Player player) {
		// Check if player is in map
		if (!this.plugin.toolType.containsKey(player)) {

			player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
			player.sendMessage(this.plugin.colorCode('&',
					(String) this.plugin.configDelegate.getMap().get("zone_cancel_not_running")));

			return;
		}

		// Clean up user
		this.plugin.toolType.remove(player);
		this.plugin.findLocations.remove(player);
		this.plugin.createWorlds.remove(player);
		this.plugin.createFirstLocations.remove(player);
		this.plugin.createSecondLocations.remove(player);
		this.plugin.subcreateWorlds.remove(player);
		this.plugin.subcreateFirstLocations.remove(player);
		this.plugin.subcreateSecondLocations.remove(player);

		if (this.plugin.worldEditPlugin.getSelection(player) != null) {
			this.plugin.worldEditPlugin.getSelection(player).getRegionSelector().clear();
		}

		// Reset beacons
		this.plugin.resetBeaconCorner(player, this.plugin.createCorner1);
		this.plugin.resetBeaconCorner(player, this.plugin.createCorner2);
		this.plugin.resetBeaconCorner(player, this.plugin.createCorner3);
		this.plugin.resetBeaconCorner(player, this.plugin.createCorner4);
		this.plugin.resetSubcreateCorner(player, this.plugin.subcreateCorner1);
		this.plugin.resetSubcreateCorner(player, this.plugin.subcreateCorner2);

		player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
		player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("zone_cancel")));
	}
}
