package xyz.joestr.zonemenu.command.subcommand;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.joestr.zonemenu.ZoneMenu;
import xyz.joestr.zonemenu.util.ZoneMenuPlayer;
import xyz.joestr.zonemenu.util.ZoneMenuToolType;

public class SubCommandZoneFind {

	ZoneMenu plugin = null;

	public SubCommandZoneFind(ZoneMenu plugin) {
		this.plugin = plugin;
	}

	public void process(Player player, String[] args) {
		
		// .length does not equals 1 -> Wrong usage
		if(args.length != 1) {
			
			player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
			player.sendMessage(
					this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("usage_message"))
							.replace("{0}", "/zone find"));

			return;
		}
		
		// Check if players inventory contains a stick
		if (!player.getInventory().contains(Material.STICK)) {

			// Add a stick to players inventory
			player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.STICK, 1) });
		}

		// If the player is in the map ...
		if(this.plugin.zoneMenuPlayers.containsKey(player)) {
			
			// ... set the proper ToolType.
			this.plugin.zoneMenuPlayers.get(player).setToolType(ZoneMenuToolType.FIND);
		} else {
			
			// If not create it here, set the ToolType and put it into the map.
			ZoneMenuPlayer zoneMenuPlayer = new ZoneMenuPlayer(player);
			
			zoneMenuPlayer.setToolType(ZoneMenuToolType.FIND);
			
			this.plugin.zoneMenuPlayers.put(player, zoneMenuPlayer);
		}

		// Send the player a message
		player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
		player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("zone_find")));
	}
}
