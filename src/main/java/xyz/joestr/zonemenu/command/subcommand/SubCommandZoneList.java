package xyz.joestr.zonemenu.command.subcommand;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import xyz.joestr.zonemenu.ZoneMenu;

public class SubCommandZoneList {

	ZoneMenu plugin = null;

	public SubCommandZoneList(ZoneMenu plugin) {
		this.plugin = plugin;
	}

	public void process(Player player, String[] args) {

		if(args.length != 1) {
			
			// Wrong usage of the /zone command
			player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
			player.sendMessage(
					this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("usage_message"))
							.replace("{0}", "/zone list"));

			return;
		}
		
		plugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

			if (t.isEmpty()) {

				player.sendMessage(
						this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
				player.sendMessage(
						this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("no_zone")));

				return;
			}

			String s = "";

			Iterator<ProtectedRegion> it = t.iterator();

			while (it.hasNext()) {

				ProtectedRegion pr = it.next();

				if (it.hasNext()) {

					s = s + pr.getId() + ", ";
				} else {

					s = s + pr.getId();
				}
			}

			player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
			player.sendMessage(this.plugin.colorCode('&',
					((String) this.plugin.configDelegate.getMap().get("zone_list")).replace("{list}", s)));

			return;
		});
	}
}
