package xyz.joestr.zonemenu.command.subcommand;

import org.bukkit.entity.Player;

import xyz.joestr.zonemenu.ZoneMenu;

public class SubCommandZoneCancel {

	ZoneMenu plugin = null;

	public SubCommandZoneCancel(ZoneMenu plugin) {
		this.plugin = plugin;
	}

	public void process(Player player, String[] args) {
		
		if(args.length != 1) {
			
			// Wrong usage of the /zone command
			player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
			player.sendMessage(
					this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("usage_message"))
							.replace("{0}", "/zone cancel"));

			return;
		}
		
		// Check if player is not in map -> No zone creation running
		if (!this.plugin.zoneMenuPlayers.containsKey(player)) {

			player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
			player.sendMessage(this.plugin.colorCode('&',
					(String) this.plugin.configDelegate.getMap().get("zone_cancel_not_running")));

			return;
		}
		
		// Check if the ToolType is not set -> No zone creation running
		if (this.plugin.zoneMenuPlayers.get(player).getToolType() == null) {

			player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
			player.sendMessage(this.plugin.colorCode('&',
					(String) this.plugin.configDelegate.getMap().get("zone_cancel_not_running")));

			return;
		}

		// Clear up player.
		this.plugin.clearUpZoneMenuPlayer(player);

		// Send player a message
		player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
		player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("zone_cancel")));
	}
}
