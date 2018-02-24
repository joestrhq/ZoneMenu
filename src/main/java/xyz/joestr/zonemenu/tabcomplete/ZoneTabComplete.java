package xyz.joestr.zonemenu.tabcomplete;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.domains.DefaultDomain;

import xyz.joestr.zonemenu.ZoneMenu;

public class ZoneTabComplete implements TabCompleter {

	private ZoneMenu plugin;

	public ZoneTabComplete(ZoneMenu zonemenu) {
		this.plugin = zonemenu;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

		List<String> list = new ArrayList<String>();
		List<String> l = new ArrayList<String>();

		// Player
		if (sender instanceof Player) {

			Player player = (Player) sender;

			if (!player.hasPermission("zonemenu.*")) {
				return list;
			}
			;

			if (args.length <= 1) {

				list.add("find");
				list.add("sign");
				list.add("create");
				list.add("addmember");
				list.add("removemember");
				list.add("info");
				list.add("delete");
				list.add("cancel");

				if (args.length == 1) {

					for (String key : list) {

						if (key.startsWith(args[0])) {
							l.add(key);
						}
					}

					list = l;
				}

				return list;
			}

			if (args.length <= 2) {

				if (args[0].equalsIgnoreCase("addmember")) {

					for (Player pl : Bukkit.getOnlinePlayers()) {
						list.add(pl.getName());
					}

					if (list.contains(player.getName())) {
						list.remove(player.getName());
					}

					if (args.length == 2) {

						for (String key : list) {

							if (key.startsWith(args[1])) {
								l.add(key);
							}
						}

						list = l;
					}

					return list;
				}

				if (args[0].equalsIgnoreCase("removemember")) {

					DefaultDomain domain = null;

					try {
						domain = this.plugin.getRegion(player, false).getMembers();
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (domain == null) {
						return list;
					}

					String[] str = domain.toPlayersString(this.plugin.worldguardplugin.getProfileCache())
							.replace("*", "").split(", ");

					for (String s : str) {
						list.add(s);
					}

					if (args.length == 2) {

						for (String key : list) {

							if (key.startsWith(args[1])) {
								l.add(key);
							}
						}

						list = l;
					}
				}

				return list;
			}

			return list;
		}

		// Console
		return list;
	}
}
