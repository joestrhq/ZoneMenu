package xyz.joestr.zonemenu.subcommand;

import java.util.List;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import xyz.joestr.zonemenu.ZoneMenu;

public class SubCommandZoneDelete {

	ZoneMenu plugin = null;

	public SubCommandZoneDelete(ZoneMenu plugin) {
		this.plugin = plugin;
	}

	public void process(Player player) {

		plugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

			// Initialise region
			ProtectedRegion protectedregion = null;

			if (!t.isEmpty()) {
				protectedregion = t.get(0);
			}

			// Check if region in invalid
			if (protectedregion == null) {

				player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
				player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("no_zone")));

				return;
			}

			// Remove the region from worlds region manager
			plugin.worldGuardPlugin.getRegionManager(player.getWorld()).removeRegion(protectedregion.getId());

			// Send a message to the player
			player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
			player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_delete")));
		});
	}
}
