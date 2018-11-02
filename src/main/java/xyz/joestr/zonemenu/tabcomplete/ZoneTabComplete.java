package xyz.joestr.zonemenu.tabcomplete;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.EnumFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import xyz.joestr.zonemenu.ZoneMenu;

/**
 * Handles tab completions of the /zone command
 * 
 * @author joestr
 * @since build_1
 * @version ${project.version}
 */
public class ZoneTabComplete implements TabCompleter {

    private ZoneMenu plugin;

    public ZoneTabComplete(ZoneMenu zonemenu) {
        this.plugin = zonemenu;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        // TODO: Comments

        List<String> list = new ArrayList<String>();
        List<String> l = new ArrayList<String>();

        // Player
        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (!player.hasPermission("zonemenu.*")) {

                return list;
            }

            // /zone []
            if (args.length <= 1) {

                list.add("find");
                list.add("create");
                list.add("subcreate");
                list.add("addmember");
                list.add("removemember");
                list.add("flag");
                list.add("info");
                list.add("delete");
                list.add("cancel");
                list.add("list");
                list.add("update");

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

            // /zone <> []
            if (args.length <= 2) {

                // /zone info []
                if (args[0].equalsIgnoreCase("info")) {

                    for (ProtectedRegion pr : this.plugin.getRegions(player, false)) {
                        list.add(pr.getId());
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

                // /zone delete []
                if (args[0].equalsIgnoreCase("delete")) {

                    for (ProtectedRegion pr : this.plugin.getRegions(player, false)) {
                        list.add(pr.getId());
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

                // /zone addmember []
                if (args[0].equalsIgnoreCase("addmember")) {

                    List<ProtectedRegion> t = this.plugin.getRegions(player, false);
                    if (t.isEmpty()) {
                        return list;
                    }
                    for (ProtectedRegion pr : t) {
                        list.add(pr.getId());
                    }

                    if (args.length == 3) {
                        for (String key : list) {
                            if (key.startsWith(args[1])) {
                                l.add(key);
                            }
                        }
                        list = l;
                    }

                    return list;
                }

                // /zone removemember []
                if (args[0].equalsIgnoreCase("removemember")) {

                    List<ProtectedRegion> t = this.plugin.getRegions(player, false);
                    if (t.isEmpty()) {
                        return list;
                    }
                    for (ProtectedRegion pr : t) {
                        list.add(pr.getId());
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

                // /zone flag []
                if (args[0].equalsIgnoreCase("flag")) {

                    List<ProtectedRegion> t = this.plugin.getRegions(player, false);
                    if (t.isEmpty()) {
                        return list;
                    }
                    for (ProtectedRegion pr : t) {
                        list.add(pr.getId());
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

                // /zone subcreate []
                if (args[0].equalsIgnoreCase("subcreate")) {

                    List<ProtectedRegion> t = this.plugin.getRegions(player, false);
                    if (t.isEmpty()) {
                        return list;
                    }
                    for (ProtectedRegion pr : t) {
                        list.add(pr.getId());
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

                return list;
            }

            // /zone <> <> []
            if (args.length <= 3) {

                // /zone addmember <> []
                if (args[0].equalsIgnoreCase("addmember")) {

                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        list.add(pl.getName());
                    }
                    if (list.contains(player.getName())) {
                        list.remove(player.getName());
                    }

                    if (args.length == 3) {
                        for (String key : list) {
                            if (key.startsWith(args[2])) {
                                l.add(key);
                            }
                        }
                        list = l;
                    }

                    return list;
                }

                // /zone removemember <> []
                if (args[0].equalsIgnoreCase("removemember")) {

                    List<ProtectedRegion> t = this.plugin.getRegions(player, false);
                    ProtectedRegion protectedregion = null;
                    if (t.isEmpty()) {
                        return list;
                    }
                    for (ProtectedRegion pr : t) {
                        if (pr.getId().equalsIgnoreCase(args[1])) {
                            protectedregion = pr;
                        }
                    }
                    if (protectedregion == null) {
                        return list;
                    }
                    DefaultDomain domain = null;
                    try {
                        domain = protectedregion.getMembers();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (domain == null) {
                        return list;
                    }
                    String[] str = domain.toPlayersString(this.plugin.worldGuardPlugin.getProfileCache())
                            .replace("*", "").split(", ");
                    for (String s : str) {
                        list.add(s);
                    }

                    if (args.length == 3) {
                        for (String key : list) {
                            if (key.startsWith(args[2])) {
                                l.add(key);
                            }
                        }
                        list = l;
                    }

                    return list;
                }

                // /zone flag <> []
                if (args[0].equalsIgnoreCase("flag")) {

                    for (Flag<?> f : DefaultFlag.getDefaultFlags()) {
                        list.add(f.getName());
                    }

                    if (args.length == 3) {
                        for (String key : list) {
                            if (key.startsWith(args[2])) {
                                l.add(key);
                            }
                        }
                        list = l;
                    }
                }

                return list;
            }

            if (args.length <= 4) {

                // /zone flag <> <> []
                if (args[0].equalsIgnoreCase("flag")) {

                    for (Flag<?> f : DefaultFlag.getDefaultFlags()) {
                        if (f.getName().equalsIgnoreCase(args[2])) {
                            if (f instanceof StateFlag) {
                                list.add("allow");
                                list.add("deny");
                                list.add("none");
                            } else if (f instanceof SetFlag<?>) {
                                if (DefaultFlag.DENY_SPAWN.getName().equalsIgnoreCase(((SetFlag<?>) f).getName())) {
                                    for (EntityType et : EntityType.values()) {
                                        list.add(et.name().toLowerCase());
                                    }
                                }
                            } else if (f instanceof StringFlag) {

                            } else if (f instanceof BooleanFlag) {
                                list.add("false");
                                list.add("true");
                            } else if (f instanceof IntegerFlag) {

                            } else if (f instanceof DoubleFlag) {

                            } else if (f instanceof LocationFlag) {
                                list.add("here");
                            } else if (f instanceof EnumFlag<?>) {
                                if (DefaultFlag.GAME_MODE.getName().equalsIgnoreCase(((EnumFlag<?>) f).getName())) {
                                    for (GameMode gm : GameMode.values()) {
                                        list.add(gm.name().toLowerCase());
                                    }
                                } else if (DefaultFlag.WEATHER_LOCK.getName()
                                        .equalsIgnoreCase(((EnumFlag<?>) f).getName())) {
                                    for (WeatherType wt : WeatherType.values()) {
                                        list.add(wt.name().toLowerCase());
                                    }
                                }
                            } else {

                            }
                        }
                    }

                    if (args.length == 4) {
                        for (String key : list) {
                            if (key.startsWith(args[3])) {
                                l.add(key);
                            }
                        }
                        list = l;
                    }
                }
            }

            return list;
        }

        // Console
        return list;
    }
}
