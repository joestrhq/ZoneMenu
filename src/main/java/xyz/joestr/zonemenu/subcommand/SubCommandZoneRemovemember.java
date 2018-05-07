package xyz.joestr.zonemenu.subcommand;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import xyz.joestr.zonemenu.ZoneMenu;

public class SubCommandZoneRemovemember {

	ZoneMenu plugin = null;

	public SubCommandZoneRemovemember(ZoneMenu plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	public void process(Player player, String[] args) {

		plugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

			// Initialise new region
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

			// Grab the members
			DefaultDomain domainmembers = protectedregion.getMembers();

			// Check if members does not contain the specified player
			if (!domainmembers.contains(
					plugin.worldGuardPlugin.wrapOfflinePlayer(Bukkit.getServer().getOfflinePlayer(args[1])))) {

				player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
				player.sendMessage(plugin
						.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_removemember_unknownplayer"))
						.replace("{0}", args[1]));

				return;
			}

			// Remove specified player from the members
			domainmembers.removePlayer(
					plugin.worldGuardPlugin.wrapOfflinePlayer(Bukkit.getServer().getOfflinePlayer(args[1])));

			// Set the new members
			protectedregion.setMembers(domainmembers);

			player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
			player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_removemember"))
					.replace("{0}", args[1]));
		});
	}
}
