package xyz.joestr.zonemenu.command.subcommand;

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

		if(args.length != 1) {
			
			// Wrong usage of the /zone command
			player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
			player.sendMessage(
					this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("usage_message"))
							.replace("{0}", "/zone removemember <Zone> <Player>"));

			return;
		}
		
		plugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

			// Initialise new region
			ProtectedRegion protectedregion = null;

			if (t.isEmpty()) {

				player.sendMessage(
						this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
				player.sendMessage(
						this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("no_zone")));

				return;
			}

			for (ProtectedRegion pr : t) {

				if (pr.getId().equalsIgnoreCase(args[1])) {

					protectedregion = pr;
				}
			}

			// Check if region in invalid
			if (protectedregion == null) {

				player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
				player.sendMessage(this.plugin.colorCode('&',
						((String) this.plugin.configDelegate.getMap().get("not_exisiting_zone")).replace("{0}",
								args[1])));

				return;
			}

			// Grab the members
			DefaultDomain domainmembers = protectedregion.getMembers();

			// Check if members does not contain the specified player
			if (!domainmembers.contains(
					plugin.worldGuardPlugin.wrapOfflinePlayer(Bukkit.getServer().getOfflinePlayer(args[2])))) {

				player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
				player.sendMessage(plugin
						.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_removemember_unknownplayer"))
						.replace("{0}", args[2])
						.replace("{1}", args[1]));

				return;
			}

			// Remove specified player from the members
			domainmembers.removePlayer(
					plugin.worldGuardPlugin.wrapOfflinePlayer(Bukkit.getServer().getOfflinePlayer(args[2])));

			// Set the new members
			protectedregion.setMembers(domainmembers);

			player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
			player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_removemember"))
					.replace("{0}", args[2])
					.replace("{1}", args[1]));
		});
	}
}
