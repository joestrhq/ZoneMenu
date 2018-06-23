package xyz.joestr.zonemenu.command.subcommand;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import xyz.joestr.zonemenu.ZoneMenu;

public class SubCommandZoneInfo {

	ZoneMenu plugin = null;

	public SubCommandZoneInfo(ZoneMenu plugin) {
		this.plugin = plugin;
	}

	public void process(Player player) {

		plugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

			// Initialise new region
			ProtectedRegion protectedregion = null;

			if (!t.isEmpty()) {
				protectedregion = t.get(0);
			}

			// Check if region in invalid
			if (protectedregion == null) {

				player.sendMessage(
						this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
				player.sendMessage(
						this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("no_zone")));

				return;
			}

			// Grab some infos
			DefaultDomain domainowners = protectedregion.getOwners();
			DefaultDomain regionmembers = protectedregion.getMembers();
			int min_x = protectedregion.getMinimumPoint().getBlockX();
			int min_z = protectedregion.getMinimumPoint().getBlockZ();
			int max_x = protectedregion.getMaximumPoint().getBlockX();
			int max_z = protectedregion.getMaximumPoint().getBlockZ();
			int area = (this.plugin.difference(min_x, max_x) + 1) * (this.plugin.difference(min_z, max_z) + 1);

			// Send infos to player
			player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));

			player.sendMessage(
					this.plugin.colorCode(
							'&',
							((String)this.plugin.configDelegate.getMap().get("zone_info_name_id"))
								.replace("{id}", protectedregion.getId())
								.replace(
										"{name}",
										((String)this.plugin.nameDelegate.getMap().get("name_format"))
												.replace(
														"{owner}",
														((String)domainowners.toPlayersString(this.plugin.worldGuardPlugin.getProfileCache()))
																.replace("*", "").split(",")[0]
												)
												.replace(
														"{id_counter}",
														protectedregion.getId().replace((String) this.plugin.idDelegate.getMap().get("zone_id"), "")
												)
								)
					)
			);

			player.sendMessage(
					this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("zone_info_priority"))
							+ protectedregion.getPriority());
			player.sendMessage(this.plugin.colorCode('&',
					(String) this.plugin.configDelegate.getMap().get("zone_info_owners"))
					+ domainowners.toPlayersString(this.plugin.worldGuardPlugin.getProfileCache()).replace("*", ""));
			player.sendMessage(this.plugin.colorCode('&',
					(String) this.plugin.configDelegate.getMap().get("zone_info_members"))
					+ regionmembers.toPlayersString(this.plugin.worldGuardPlugin.getProfileCache()).replace("*", ""));

			Iterator<Entry<Flag<?>, Object>> it = protectedregion.getFlags().entrySet().iterator();
			while (it.hasNext()) {
				Entry<Flag<?>, Object> e = it.next();
				player.sendMessage(
						this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("zone_info_flag"))
								.replace("{0}", e.getKey().getName()).replace("{1}", e.getValue().toString()));
			}

			player.sendMessage(
					this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("zone_info_start"))
							.replace("{0}", Integer.toString(min_x)).replace("{1}", Integer.toString(min_z)));
			player.sendMessage(
					this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("zone_info_end"))
							.replace("{0}", Integer.toString(max_x)).replace("{1}", Integer.toString(max_z)));
			player.sendMessage(
					this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("zone_info_area"))
							.replace("{0}", Integer.toString(area)));

		});
	}
}
