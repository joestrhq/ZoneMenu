package xyz.joestr.zonemenu.command.subcommand;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.WeatherType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.EnumFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.md_5.bungee.api.ChatColor;
import xyz.joestr.zonemenu.ZoneMenu;

public class SubCommandZoneFlag {

	ZoneMenu plugin = null;

	public SubCommandZoneFlag(ZoneMenu plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("unchecked")
	public void process(Player player, String[] args) {

		if(args.length != 4) {
			
			// Wrong usage of the /zone command
			player.sendMessage(this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
			player.sendMessage(
					this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("usage_message"))
							.replace("{0}", "/zone flag <Zone> <Flag> <Flagvalue>"));

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

			if (protectedregion == null) {

				player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
				player.sendMessage(this.plugin.colorCode('&',
						((String) this.plugin.configDelegate.getMap().get("not_exisiting_zone")).replace("{0}",
								args[1])));
				
				return;
			}

			String all = "";

			for (int i = 3; i < args.length; i++) {

				if (i == args.length - 1) {

					all = all.concat(args[i]);
				} else {

					all = all.concat(args[i].concat(" "));
				}
			}

			Flag<?> flagMatched = DefaultFlag.fuzzyMatchFlag(plugin.worldGuardPlugin.getFlagRegistry(), args[2]);

			if (!DefaultFlag.getDefaultFlags().contains(flagMatched)) {
				player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
				player.sendMessage(
						plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_not_found"))
								.replace("{0}", args[2]));
			}

			if (flagMatched instanceof StateFlag) {
				StateFlag sf = (StateFlag) flagMatched;
				try {
					if (all.equalsIgnoreCase("null")) {
						protectedregion.setFlag(sf, null);
					} else {
						protectedregion.setFlag(sf, sf.parseInput(FlagContext.create().setSender(player).setInput(all)
								.setObject("region", protectedregion).build()));
					}
					player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
					player.sendMessage(
							plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
									.replace("{0}", flagMatched.getName()).replace("{1}", all));
				} catch (InvalidFlagFormat e) {
					player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
					player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " " + args[2] + " <allow|deny|none>");
					return;
					// e.printStackTrace();
				}
				return;
			}

			if (flagMatched instanceof SetFlag<?>) {
				if (DefaultFlag.DENY_SPAWN.getName().equalsIgnoreCase(((SetFlag<?>) flagMatched).getName())) {
					SetFlag<EntityType> sfet = (SetFlag<EntityType>) flagMatched;
					try {
						if (all.equalsIgnoreCase("null")) {
							protectedregion.setFlag(sfet, null);
						} else {
							protectedregion.setFlag(sfet, sfet.parseInput(FlagContext.create().setSender(player)
									.setInput(all).setObject("region", protectedregion).build()));
						}
						player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
						player.sendMessage(
								plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
										.replace("{0}", flagMatched.getName()).replace("{1}", all));
					} catch (InvalidFlagFormat e) {
						player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " " + args[2]
								+ " <EntityType[,EntityType ...]>");
						// e.printStackTrace();
					}
					return;
				}

				Type type = ((ParameterizedType) flagMatched.getClass().getGenericSuperclass())
						.getActualTypeArguments()[0];
				SetFlag<Type> sfet = (SetFlag<Type>) flagMatched;

				try {
					if (all.equalsIgnoreCase("null")) {
						protectedregion.setFlag(sfet, null);
					} else {
						protectedregion.setFlag(sfet, sfet.parseInput(FlagContext.create().setSender(player)
								.setInput(all).setObject("region", protectedregion).build()));
					}
					player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
					player.sendMessage(
							plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
									.replace("{0}", flagMatched.getName()).replace("{1}", all));
				} catch (InvalidFlagFormat e) {
					player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
					player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " " + args[2] + " <"
							+ type.getTypeName() + "[," + type.getTypeName() + " ...]>");
					// e.printStackTrace();
				}

				return;
			}

			if (flagMatched instanceof StringFlag) {
				StringFlag sf = (StringFlag) flagMatched;

				try {
					if (all.equalsIgnoreCase("null")) {
						protectedregion.setFlag(sf, null);
					} else {
						protectedregion.setFlag(sf, sf.parseInput(FlagContext.create().setSender(player).setInput(all)
								.setObject("region", protectedregion).build()));
					}
					player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
					player.sendMessage(
							plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
									.replace("{0}", flagMatched.getName()).replace("{1}", all));
				} catch (InvalidFlagFormat e) {
					player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
					player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " " + args[2] + " <String ...>");
					// e.printStackTrace();
				}

				return;
			}

			if (flagMatched instanceof BooleanFlag) {
				BooleanFlag bf = (BooleanFlag) flagMatched;

				try {
					if (all.equalsIgnoreCase("null")) {
						protectedregion.setFlag(bf, null);
					} else {
						protectedregion.setFlag(bf, bf.parseInput(FlagContext.create().setSender(player).setInput(all)
								.setObject("region", protectedregion).build()));
					}
					player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
					player.sendMessage(
							plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
									.replace("{0}", flagMatched.getName()).replace("{1}", all));
				} catch (InvalidFlagFormat e) {
					player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
					player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " " + args[2] + " <true|false>");
					// e.printStackTrace();
				}

				return;
			}

			if (flagMatched instanceof IntegerFlag) {
				IntegerFlag inf = (IntegerFlag) flagMatched;

				try {
					if (all.equalsIgnoreCase("null")) {
						protectedregion.setFlag(inf, null);
					} else {
						protectedregion.setFlag(inf, inf.parseInput(FlagContext.create().setSender(player).setInput(all)
								.setObject("region", protectedregion).build()));
					}
					player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
					player.sendMessage(
							plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
									.replace("{0}", flagMatched.getName()).replace("{1}", all));
				} catch (InvalidFlagFormat e) {
					player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
					player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " " + args[2] + " <Integer>");
					// e.printStackTrace();
				}

				return;
			}

			if (flagMatched instanceof DoubleFlag) {
				DoubleFlag df = (DoubleFlag) flagMatched;

				try {
					if (all.equalsIgnoreCase("null")) {
						protectedregion.setFlag(df, null);
					} else {
						protectedregion.setFlag(df, df.parseInput(FlagContext.create().setSender(player).setInput(all)
								.setObject("region", protectedregion).build()));
					}
					player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
					player.sendMessage(
							plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
									.replace("{0}", flagMatched.getName()).replace("{1}", all));
				} catch (InvalidFlagFormat e) {
					player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
					player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " " + args[2] + " <Double>");
					// e.printStackTrace();
				}

				return;
			}

			if (flagMatched instanceof LocationFlag) {
				LocationFlag lf = (LocationFlag) flagMatched;

				try {
					if (all.equalsIgnoreCase("null")) {
						protectedregion.setFlag(lf, null);
					} else {
						protectedregion.setFlag(lf, lf.parseInput(FlagContext.create().setSender(player).setInput(all)
								.setObject("region", protectedregion).build()));
					}
					player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
					player.sendMessage(
							plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
									.replace("{0}", flagMatched.getName()).replace("{1}", all));
				} catch (InvalidFlagFormat e) {
					player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
					player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " " + args[2] + " <here|X,Y,Z>");
					// e.printStackTrace();
				}

				return;
			}

			if (flagMatched instanceof EnumFlag<?>) {
				if (DefaultFlag.GAME_MODE.getName().equalsIgnoreCase(((EnumFlag<?>) flagMatched).getName())) {
					EnumFlag<GameMode> efgm = (EnumFlag<GameMode>) flagMatched;

					try {
						if (all.equalsIgnoreCase("null")) {
							protectedregion.setFlag(efgm, null);
						} else {
							protectedregion.setFlag(efgm, efgm.parseInput(FlagContext.create().setSender(player)
									.setInput(all).setObject("region", protectedregion).build()));
						}
						player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
						player.sendMessage(
								plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
										.replace("{0}", flagMatched.getName()).replace("{1}", all));
					} catch (InvalidFlagFormat e) {
						player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
						player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " " + args[2] + " <Gamemode>");
						// e.printStackTrace();
					}

					return;
				}

				if (DefaultFlag.WEATHER_LOCK.getName().equalsIgnoreCase(((EnumFlag<?>) flagMatched).getName())) {
					EnumFlag<WeatherType> efwt = (EnumFlag<WeatherType>) flagMatched;

					try {
						if (all.equalsIgnoreCase("null")) {
							protectedregion.setFlag(efwt, null);
						} else {
							protectedregion.setFlag(efwt, efwt.parseInput(FlagContext.create().setSender(player)
									.setInput(all).setObject("region", protectedregion).build()));
						}
						player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
						player.sendMessage(
								plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
										.replace("{0}", flagMatched.getName()).replace("{1}", all));
					} catch (InvalidFlagFormat e) {
						player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
						player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " " + args[2] + " <Weathertype>");
						// e.printStackTrace();
					}

					return;
				}
			}
		});
	}
}
