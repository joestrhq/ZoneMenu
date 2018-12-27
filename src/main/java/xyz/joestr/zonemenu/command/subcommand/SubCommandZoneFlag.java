package xyz.joestr.zonemenu.command.subcommand;

import com.sk89q.worldguard.WorldGuard;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.WeatherType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.EnumFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.md_5.bungee.api.ChatColor;
import xyz.joestr.zonemenu.ZoneMenu;

/**
 * Class which handles subcommand "flag" of command "zone".
 *
 * @author joestr
 * @since ${project.version}
 * @version ${project.version}
 */
public class SubCommandZoneFlag {

    ZoneMenu zoneMenuPlugin = null;

    /**
     * Constrcutor for the null null null null null null null null null null
     * null null null     {@link xyz.joestr.zonemenu.command.subcommand.SubCommandZoneFlag
     * SubCommandZoneFlag} class.
     *
     * @param zoneMenuPlugin A {@link xyz.joestr.zonemenu.ZoneMenu ZoneMenu}.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    public SubCommandZoneFlag(ZoneMenu plugin) {

        this.zoneMenuPlugin = plugin;
    }

    /**
     * Constrcutor for the null null null null null null null null null null
     * null null null     {@link xyz.joestr.zonemenu.command.subcommand.SubCommandZoneCreate
     * SubCommandZoneCreate} class.
     *
     * @param zoneMenuPlugin A {@link xyz.joestr.zonemenu.ZoneMenu ZoneMenu}.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    @SuppressWarnings("unchecked")
    public void process(Player player, String[] arguments) {

        // If arguments' length does not equals 1 ...
        if (arguments.length != 4) {

            // ... wrong usage of "/zone cancel".
            // Send the player a message.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message")).replace("{0}",
                    "/zone flag <Zone> <Flag> <Flagvalue>")));

            return;
        }

        this.zoneMenuPlugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

            // If the list is empty ...
            if (t.isEmpty()) {

                // ... send the player a message.
                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("no_zone"))));

                return;
            }

            // Initialise new region
            ProtectedRegion protectedRegion = null;

            // Loop through all regions ...
            for (ProtectedRegion protectedRegion_ : t) {

                // ... and if the region ID equals the second argument (<Zone>) ...
                if (protectedRegion_.getId().equalsIgnoreCase(arguments[1])) {

                    // ... set the found region.
                    protectedRegion = protectedRegion_;
                }
            }

            // If region equals null ...
            if (protectedRegion == null) {

                // ... no region with this ID was not found.
                // Send the player a message.
                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("not_exisiting_zone")).replace("{0}",
                        arguments[1])));

                return;
            }

            // Holds arguments for the flag
            String flagArguments = "";

            for (int i = 3; i < arguments.length; i++) {

                if (i == arguments.length - 1) {

                    flagArguments = flagArguments.concat(arguments[i]);
                } else {

                    flagArguments = flagArguments.concat(arguments[i].concat(" "));
                }
            }

            Flag<?> matchedFlag = Flags.fuzzyMatchFlag(WorldGuard.getInstance().getFlagRegistry(),
                arguments[2]);

            // If the found flag is not a default flag
            //if (!DefaultFlag.getDefaultFlags().contains(matchedFlag)) {
            //
            // ... sned the player amessage.
            //   player.sendMessage(this.zoneMenuPlugin.colorCode('&',
            //        ((String) zoneMenuPlugin.configDelegate.getMap().get("zone_flag_not_found")).replace("{0}",
            //            arguments[2])));
            //}
            // If the matched flag is a StateFlag
            if (matchedFlag instanceof StateFlag) {

                StateFlag stateFlag_ = (StateFlag) matchedFlag;

                try {

                    // If the flag argument equals "null" ...
                    if (flagArguments.equalsIgnoreCase("null")) {

                        // ... unset the flag.
                        protectedRegion.setFlag(stateFlag_, null);
                    } else {

                        // If not set the flag with the parsed input.
                        protectedRegion.setFlag(stateFlag_, stateFlag_.parseInput(FlagContext.create().setSender(this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(player))
                            .setInput(flagArguments).setObject("region", protectedRegion).build()));
                    }

                    // Send the player a message.
                    player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        ((String) zoneMenuPlugin.configDelegate.getMap().get("zone_flag_changed"))
                            .replace("{0}", matchedFlag.getName()).replace("{1}", flagArguments)));

                } catch (InvalidFlagFormat exception) {

                    // If there was an exception while parsing the flag send a message to the
                    // player.
                    player.sendMessage(ChatColor.RED + "/zone flag " + arguments[1] + " " + arguments[2]
                        + " <null|allow|deny|none>");
                }

                return;
            }

            // TODO: Better comments
            // If the matched flag is a SetFlag (with unknown type)
            if (matchedFlag instanceof SetFlag<?>) {

                // If the matched flag is the deny spawn flag ...
                if (Flags.DENY_SPAWN.getName().equalsIgnoreCase(((SetFlag<?>) matchedFlag).getName())) {

                    // ... we can say that this flag accepts entity types
                    SetFlag<EntityType> setFlag_ = (SetFlag<EntityType>) matchedFlag;

                    try {

                        if (flagArguments.equalsIgnoreCase("null")) {

                            protectedRegion.setFlag(setFlag_, null);
                        } else {

                            protectedRegion.setFlag(setFlag_, setFlag_.parseInput(FlagContext.create().setSender(this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(player))
                                .setInput(flagArguments).setObject("region", protectedRegion).build()));
                        }

                        player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                            ((String) zoneMenuPlugin.configDelegate.getMap().get("zone_flag_changed"))
                                .replace("{0}", matchedFlag.getName()).replace("{1}", flagArguments)));
                    } catch (InvalidFlagFormat e) {

                        player.sendMessage(ChatColor.RED + "/zone flag " + arguments[1] + " " + arguments[2]
                            + " <null|EntityType[,EntityType ...]>");
                    }

                    return;
                }

                // We do not know the type of the set flag here.
                Type type = ((ParameterizedType) matchedFlag.getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];

                SetFlag<Type> setFlag_ = (SetFlag<Type>) matchedFlag;

                try {

                    if (flagArguments.equalsIgnoreCase("null")) {

                        protectedRegion.setFlag(setFlag_, null);
                    } else {

                        protectedRegion.setFlag(setFlag_, setFlag_.parseInput(FlagContext.create().setSender(this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(player))
                            .setInput(flagArguments).setObject("region", protectedRegion).build()));
                    }

                    player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        ((String) zoneMenuPlugin.configDelegate.getMap().get("zone_flag_changed"))
                            .replace("{0}", matchedFlag.getName()).replace("{1}", flagArguments)));
                } catch (InvalidFlagFormat e) {

                    player.sendMessage(ChatColor.RED + "/zone flag " + arguments[1] + " " + arguments[2] + " <null|"
                        + type.getTypeName() + "[," + type.getTypeName() + " ...]>");
                }

                return;
            }

            if (matchedFlag instanceof StringFlag) {

                StringFlag stringFlag_ = (StringFlag) matchedFlag;

                try {

                    if (flagArguments.equalsIgnoreCase("null")) {

                        protectedRegion.setFlag(stringFlag_, null);
                    } else {

                        protectedRegion.setFlag(stringFlag_,
                            stringFlag_.parseInput(FlagContext.create().setSender(this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(player)).setInput(flagArguments)
                                .setObject("region", protectedRegion).build()));
                    }

                    player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_flag_changed"))
                            .replace("{0}", matchedFlag.getName()).replace("{1}", flagArguments)));
                } catch (InvalidFlagFormat e) {

                    player.sendMessage(
                        ChatColor.RED + "/zone flag " + arguments[1] + " " + arguments[2] + " <null|String ...>");
                }

                return;
            }

            if (matchedFlag instanceof BooleanFlag) {

                BooleanFlag booleanFlag_ = (BooleanFlag) matchedFlag;

                try {

                    if (flagArguments.equalsIgnoreCase("null")) {

                        protectedRegion.setFlag(booleanFlag_, null);
                    } else {

                        protectedRegion.setFlag(booleanFlag_,
                            booleanFlag_.parseInput(FlagContext.create().setSender(this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(player)).setInput(flagArguments)
                                .setObject("region", protectedRegion).build()));
                    }

                    player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        ((String) zoneMenuPlugin.configDelegate.getMap().get("zone_flag_changed"))
                            .replace("{0}", matchedFlag.getName()).replace("{1}", flagArguments)));
                } catch (InvalidFlagFormat e) {

                    player.sendMessage(
                        ChatColor.RED + "/zone flag " + arguments[1] + " " + arguments[2] + " <null|true|false>");
                }

                return;
            }

            if (matchedFlag instanceof IntegerFlag) {

                IntegerFlag integerFlag_ = (IntegerFlag) matchedFlag;

                try {

                    if (flagArguments.equalsIgnoreCase("null")) {

                        protectedRegion.setFlag(integerFlag_, null);
                    } else {

                        protectedRegion.setFlag(integerFlag_,
                            integerFlag_.parseInput(FlagContext.create().setSender(this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(player)).setInput(flagArguments)
                                .setObject("region", protectedRegion).build()));
                    }

                    player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        ((String) zoneMenuPlugin.configDelegate.getMap().get("zone_flag_changed"))
                            .replace("{0}", matchedFlag.getName()).replace("{1}", flagArguments)));
                } catch (InvalidFlagFormat e) {

                    player.sendMessage(
                        ChatColor.RED + "/zone flag " + arguments[1] + " " + arguments[2] + " <null|Integer>");
                }

                return;
            }

            if (matchedFlag instanceof DoubleFlag) {

                DoubleFlag doubleFlag_ = (DoubleFlag) matchedFlag;

                try {

                    if (flagArguments.equalsIgnoreCase("null")) {

                        protectedRegion.setFlag(doubleFlag_, null);
                    } else {

                        protectedRegion.setFlag(doubleFlag_,
                            doubleFlag_.parseInput(FlagContext.create().setSender(this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(player)).setInput(flagArguments)
                                .setObject("region", protectedRegion).build()));
                    }

                    player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_flag_changed"))
                            .replace("{0}", matchedFlag.getName()).replace("{1}", flagArguments)));
                } catch (InvalidFlagFormat e) {

                    player.sendMessage(
                        ChatColor.RED + "/zone flag " + arguments[1] + " " + arguments[2] + " <null|Double>");
                }

                return;
            }

            if (matchedFlag instanceof LocationFlag) {

                LocationFlag locationFlag_ = (LocationFlag) matchedFlag;

                try {

                    if (flagArguments.equalsIgnoreCase("null")) {

                        protectedRegion.setFlag(locationFlag_, null);
                    } else {

                        protectedRegion.setFlag(locationFlag_,
                            locationFlag_.parseInput(FlagContext.create().setSender(this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(player)).setInput(flagArguments)
                                .setObject("region", protectedRegion).build()));
                    }

                    player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        ((String) zoneMenuPlugin.configDelegate.getMap().get("zone_flag_changed"))
                            .replace("{0}", matchedFlag.getName()).replace("{1}", flagArguments)));
                } catch (InvalidFlagFormat e) {

                    player.sendMessage(
                        ChatColor.RED + "/zone flag " + arguments[1] + " " + arguments[2] + " <null|here|X,Y,Z>");
                }

                return;
            }

            if (matchedFlag instanceof EnumFlag<?>) {

                if (Flags.GAME_MODE.getName().equalsIgnoreCase(((EnumFlag<?>) matchedFlag).getName())) {

                    EnumFlag<GameMode> enumFlag_ = (EnumFlag<GameMode>) matchedFlag;

                    try {

                        if (flagArguments.equalsIgnoreCase("null")) {

                            protectedRegion.setFlag(enumFlag_, null);
                        } else {

                            protectedRegion.setFlag(enumFlag_,
                                enumFlag_.parseInput(FlagContext.create().setSender(this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(player)).setInput(flagArguments)
                                    .setObject("region", protectedRegion).build()));
                        }

                        player.sendMessage(zoneMenuPlugin.colorCode('&',
                            ((String) zoneMenuPlugin.configDelegate.getMap().get("zone_flag_changed"))
                                .replace("{0}", matchedFlag.getName()).replace("{1}", flagArguments)));
                    } catch (InvalidFlagFormat e) {

                        player.sendMessage(
                            ChatColor.RED + "/zone flag " + arguments[1] + " " + arguments[2] + " <null|GameMode>");
                    }

                    return;
                }

                if (Flags.WEATHER_LOCK.getName().equalsIgnoreCase(((EnumFlag<?>) matchedFlag).getName())) {

                    EnumFlag<WeatherType> enumFlag_ = (EnumFlag<WeatherType>) matchedFlag;

                    try {

                        if (flagArguments.equalsIgnoreCase("null")) {

                            protectedRegion.setFlag(enumFlag_, null);
                        } else {

                            protectedRegion.setFlag(enumFlag_,
                                enumFlag_.parseInput(FlagContext.create().setSender(this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(player)).setInput(flagArguments)
                                    .setObject("region", protectedRegion).build()));
                        }

                        player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                            ((String) zoneMenuPlugin.configDelegate.getMap().get("zone_flag_changed"))
                                .replace("{0}", matchedFlag.getName()).replace("{1}", flagArguments)));
                    } catch (InvalidFlagFormat e) {

                        player.sendMessage(ChatColor.RED + "/zone flag " + arguments[1] + " " + arguments[2]
                            + " <null|WeatherType>");
                    }

                    return;
                }
            }
        });
    }
}
