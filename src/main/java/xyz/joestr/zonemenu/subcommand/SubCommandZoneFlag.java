package xyz.joestr.zonemenu.subcommand;

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

		plugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {
			
			// Initialise new region
			ProtectedRegion protectedregion = null;

			if (!t.isEmpty()) {
				protectedregion = t.get(0);
			}
			
			if (protectedregion == null) {

				player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
				player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("no_zone")));
				return;
			}
			
			String all = "";

			for (int i = 2; i < args.length; i++) {
				all.concat(args[i]);
			}

			all.trim();
			all.replace(" ", "");

			for (Flag<?> f : DefaultFlag.getDefaultFlags()) {
				if (f.getName().equalsIgnoreCase(args[1])) {
					if (f instanceof StateFlag) {
						StateFlag sf = (StateFlag) f;
						try {
							protectedregion.setFlag(sf, sf.parseInput(FlagContext.create().setSender(player)
									.setInput(all).setObject("region", protectedregion).build()));
							player.sendMessage(
									plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
							player.sendMessage(plugin
									.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
									.replace("{0}", f.getName()).replace("{1}", args[2]));
						} catch (InvalidFlagFormat e) {
							player.sendMessage(
									plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
							player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <allow|deny|none>");
							return;
							// e.printStackTrace();
						}
					} else if (f instanceof SetFlag<?>) {
						if (DefaultFlag.DENY_SPAWN.getName().equalsIgnoreCase(((SetFlag<?>) f).getName())) {
							SetFlag<EntityType> sfet = (SetFlag<EntityType>) f;
							try {
								protectedregion.setFlag(sfet, sfet.parseInput(FlagContext.create().setSender(player)
										.setInput(all).setObject("region", protectedregion).build()));
							} catch (InvalidFlagFormat e) {
								player.sendMessage(
										ChatColor.RED + "/zone flag " + args[1] + " <EntityType [EntityType ...]>");
								// e.printStackTrace();
							}
						}

						Type type = ((ParameterizedType) f.getClass().getGenericSuperclass())
								.getActualTypeArguments()[0];

						SetFlag<Type> sfet = (SetFlag<Type>) f;
						try {
							protectedregion.setFlag(sfet, sfet.parseInput(FlagContext.create().setSender(player)
									.setInput(args[2]).setObject("region", protectedregion).build()));
							player.sendMessage(
									plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
							player.sendMessage(plugin
									.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
									.replace("{0}", f.getName()).replace("{1}", args[2]));
						} catch (InvalidFlagFormat e) {
							player.sendMessage(
									plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
							player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <" + type.getTypeName()
									+ "[," + type.getTypeName() + " ...]>");
							// e.printStackTrace();
						}
					} else if (f instanceof StringFlag) {
						StringFlag sf = (StringFlag) f;
						try {
							protectedregion.setFlag(sf, sf.parseInput(FlagContext.create().setSender(player)
									.setInput(args[2]).setObject("region", protectedregion).build()));
							player.sendMessage(
									plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
							player.sendMessage(plugin
									.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
									.replace("{0}", f.getName()).replace("{1}", args[2]));
						} catch (InvalidFlagFormat e) {
							player.sendMessage(
									plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
							player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <String...>");
							// e.printStackTrace();
						}
					} else if (f instanceof BooleanFlag) {
						BooleanFlag bf = (BooleanFlag) f;
						try {
							protectedregion.setFlag(bf, bf.parseInput(FlagContext.create().setSender(player)
									.setInput(args[2]).setObject("region", protectedregion).build()));
							player.sendMessage(
									plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
							player.sendMessage(plugin
									.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
									.replace("{0}", f.getName()).replace("{1}", args[2]));
						} catch (InvalidFlagFormat e) {
							player.sendMessage(
									plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
							player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <true|false>");
							// e.printStackTrace();
						}
					} else if (f instanceof IntegerFlag) {
						IntegerFlag inf = (IntegerFlag) f;
						try {
							protectedregion.setFlag(inf, inf.parseInput(FlagContext.create().setSender(player)
									.setInput(args[2]).setObject("region", protectedregion).build()));
							player.sendMessage(
									plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
							player.sendMessage(plugin
									.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
									.replace("{0}", f.getName()).replace("{1}", args[2]));
						} catch (InvalidFlagFormat e) {
							player.sendMessage(
									plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
							player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <Integer>");
							// e.printStackTrace();
						}
					} else if (f instanceof DoubleFlag) {
						DoubleFlag df = (DoubleFlag) f;
						try {
							protectedregion.setFlag(df, df.parseInput(FlagContext.create().setSender(player)
									.setInput(args[2]).setObject("region", protectedregion).build()));
							player.sendMessage(
									plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
							player.sendMessage(plugin
									.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
									.replace("{0}", f.getName()).replace("{1}", args[2]));
						} catch (InvalidFlagFormat e) {
							player.sendMessage(
									plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
							player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <Double>");
							// e.printStackTrace();
						}
					} else if (f instanceof LocationFlag) {
						LocationFlag lf = (LocationFlag) f;
						try {
							protectedregion.setFlag(lf, lf.parseInput(FlagContext.create().setSender(player)
									.setInput(args[2]).setObject("region", protectedregion).build()));
							player.sendMessage(
									plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
							player.sendMessage(plugin
									.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_changed"))
									.replace("{0}", f.getName()).replace("{1}", args[2]));
						} catch (InvalidFlagFormat e) {
							player.sendMessage(
									plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
							player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <here|X,Y,Z>");
							// e.printStackTrace();
						}
					} else if (f instanceof EnumFlag<?>) {
						if (DefaultFlag.GAME_MODE.getName().equalsIgnoreCase(((EnumFlag<?>) f).getName())) {
							EnumFlag<GameMode> efgm = (EnumFlag<GameMode>) f;
							try {
								protectedregion.setFlag(efgm, efgm.parseInput(FlagContext.create().setSender(player)
										.setInput(args[2]).setObject("region", protectedregion).build()));
								player.sendMessage(
										plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
								player.sendMessage(plugin
										.colorCode('&',
												(String) plugin.configDelegate.getMap().get("zone_flag_changed"))
										.replace("{0}", f.getName()).replace("{1}", args[2]));
							} catch (InvalidFlagFormat e) {
								player.sendMessage(
										plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
								player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <Gamemode>");
								// e.printStackTrace();
							}
						} else if (DefaultFlag.WEATHER_LOCK.getName().equalsIgnoreCase(((EnumFlag<?>) f).getName())) {
							EnumFlag<WeatherType> efwt = (EnumFlag<WeatherType>) f;
							try {
								protectedregion.setFlag(efwt, efwt.parseInput(FlagContext.create().setSender(player)
										.setInput(args[2]).setObject("region", protectedregion).build()));
								player.sendMessage(
										plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
								player.sendMessage(plugin
										.colorCode('&',
												(String) plugin.configDelegate.getMap().get("zone_flag_changed"))
										.replace("{0}", f.getName()).replace("{1}", args[2]));
							} catch (InvalidFlagFormat e) {
								player.sendMessage(
										plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
								player.sendMessage(ChatColor.RED + "/zone flag " + args[1] + " <Weathertype>");
								// e.printStackTrace();
							}
						}
					} else {
						player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
						player.sendMessage(
								plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_flag_not_found"))
										.replace("{0}", args[1]));
					}
				}
			}
		});
	}
}
