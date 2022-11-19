/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.zonemenu.command;

import at.joestr.javacommon.spigotutils.SpigotUtils;
import at.joestr.zonemenu.util.ZoneMenuManager;
import com.sk89q.worldguard.WorldGuard;
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.GameMode;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 *
 * @author Joel
 */
public class CommandZoneFlag implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneFlag.class.getName());

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      // TODO: send message
    }

    List<String> parsedArgs = SpigotUtils.parseSingelQuotedArguments(args);

    if (parsedArgs.size() != 3) {
      return false;
    }

    String zoneName = parsedArgs.get(0);
    String flagName = parsedArgs.get(1);
    String flagValue = parsedArgs.get(2);
    Player player = (Player) sender;

    if (!ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {

      // TODO: send message
      return true;
    }

    ZoneMenuManager.getInstance().futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

      if (t.isEmpty()) {

        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
          (String) this.zoneMenuPlugin.configDelegate.getMap().get("no_zone")));*/
        return;
      }

      // Initialise new region
      ProtectedRegion protectedRegion = null;

      // Loop through all regions ...
      for (ProtectedRegion protectedRegion_ : t) {

        // ... and if the region ID equals the second argument (<Zone>) ...
        if (protectedRegion_.getId().equalsIgnoreCase(zoneName)) {

          // ... set the found region.
          protectedRegion = protectedRegion_;
          break;
        }
      }

      // If region equals null ...
      if (protectedRegion == null) {

        // ... no region with this ID was not found.
        // Send the player a message.
        /*player.sendMessage(
                          this.zoneMenuPlugin.colorCode(
                              '&',
                              ((String)
                                      this.zoneMenuPlugin
                                          .configDelegate
                                          .getMap()
                                          .get("not_exisiting_zone"))
                                  .replace("{0}", arguments[1])));*/
        return;
      }

      Flag<?> matchedFlag
        = Flags.fuzzyMatchFlag(
          WorldGuard.getInstance().getFlagRegistry(), flagName);

      // If the found flag is not a default flag
      // if (!DefaultFlag.getDefaultFlags().contains(matchedFlag)) {
      //
      // ... sned the player amessage.
      //   player.sendMessage(this.zoneMenuPlugin.colorCode('&',
      //        ((String)
      // zoneMenuPlugin.configDelegate.getMap().get("zone_flag_not_found")).replace("{0}",
      //            arguments[2])));
      // }
      // If the matched flag is a StateFlag
      if (matchedFlag instanceof StateFlag) {

        StateFlag stateFlag_ = (StateFlag) matchedFlag;
        StateFlag.State previousFlag
          = (StateFlag.State) protectedRegion.getFlag(matchedFlag);
        String previousFlagValue
          = previousFlag != null ? previousFlag.toString() : "";

        try {

          // If the flag argument equals "null" ...
          if (flagValue.equalsIgnoreCase("null")) {

            // ... unset the flag.
            protectedRegion.setFlag(stateFlag_, null);
          } else {

            // If not set the flag with the parsed input.
            protectedRegion.setFlag(
              stateFlag_,
              stateFlag_.parseInput(
                FlagContext.create()
                  .setSender(
                    ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapCommandSender(
                      player))
                  .setInput(flagValue)
                  .setObject("region", protectedRegion)
                  .build()));
          }

          // Send the player a message.
          /*player.sendMessage(
            this.zoneMenuPlugin.colorCode(
              '&',
              ((String) zoneMenuPlugin.configDelegate
                .getMap()
                .get("zone_flag_changed"))
                .replace("{0}", matchedFlag.getName())
                .replace("{1}", protectedRegion.getId())
                .replace("{2}", previousFlagValue)
                .replace("{3}", flagArguments)));*/
        } catch (InvalidFlagFormat exception) {

          // If there was an exception while parsing the flag send a message to the
          // player.
          /*player.sendMessage(
            ChatColor.RED
            + "/zone flag "
            + arguments[1]
            + " "
            + arguments[2]
            + " <null|allow|deny|none>");*/
        }

        return;
      }

      // TODO: Better comments
      // If the matched flag is a SetFlag (with unknown type)
      if (matchedFlag instanceof SetFlag<?>) {

        // If the matched flag is the deny spawn flag ...
        if (Flags.DENY_SPAWN
          .getName()
          .equalsIgnoreCase(((SetFlag<?>) matchedFlag).getName())) {

          // ... we can say that this flag accepts entity types
          SetFlag<EntityType> setFlag_ = (SetFlag<EntityType>) matchedFlag;
          EntityType previousFlag = (EntityType) protectedRegion.getFlag(matchedFlag);
          String previousFlagValue
            = previousFlag != null ? previousFlag.toString() : "";

          try {

            if (flagValue.equalsIgnoreCase("null")) {

              protectedRegion.setFlag(setFlag_, null);
            } else {

              protectedRegion.setFlag(
                setFlag_,
                setFlag_.parseInput(
                  FlagContext.create()
                    .setSender(
                      ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapCommandSender(
                        player))
                    .setInput(flagValue)
                    .setObject("region", protectedRegion)
                    .build()));
            }

            /*player.sendMessage(
              this.zoneMenuPlugin.colorCode(
                '&',
                ((String) zoneMenuPlugin.configDelegate
                  .getMap()
                  .get("zone_flag_changed"))
                  .replace("{0}", matchedFlag.getName())
                  .replace("{1}", protectedRegion.getId())
                  .replace("{2}", previousFlagValue)
                  .replace("{3}", flagArguments)));*/
          } catch (InvalidFlagFormat e) {

            /*player.sendMessage(
              ChatColor.RED
              + "/zone flag "
              + arguments[1]
              + " "
              + arguments[2]
              + " <null|EntityType[,EntityType ...]>");*/
          }

          return;
        }

        // We do not know the type of the set flag here.
        Type type
          = ((ParameterizedType) matchedFlag.getClass().getGenericSuperclass())
            .getActualTypeArguments()[0];

        SetFlag<Type> setFlag_ = (SetFlag<Type>) matchedFlag;
        Type previousFlag = (Type) protectedRegion.getFlag(matchedFlag);
        String previousFlagValue
          = previousFlag != null ? previousFlag.toString() : "";

        try {

          if (flagValue.equalsIgnoreCase("null")) {

            protectedRegion.setFlag(setFlag_, null);
          } else {

            protectedRegion.setFlag(
              setFlag_,
              setFlag_.parseInput(
                FlagContext.create()
                  .setSender(
                    ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapCommandSender(
                      player))
                  .setInput(flagValue)
                  .setObject("region", protectedRegion)
                  .build()));
          }

          /*player.sendMessage(
            this.zoneMenuPlugin.colorCode(
              '&',
              ((String) zoneMenuPlugin.configDelegate
                .getMap()
                .get("zone_flag_changed"))
                .replace("{0}", matchedFlag.getName())
                .replace("{1}", protectedRegion.getId())
                .replace("{2}", previousFlagValue)
                .replace("{3}", flagArguments)));*/
        } catch (InvalidFlagFormat e) {

          /*player.sendMessage(
            ChatColor.RED
            + "/zone flag "
            + arguments[1]
            + " "
            + arguments[2]
            + " <null|"
            + type.getTypeName()
            + "[,"
            + type.getTypeName()
            + " ...]>");*/
        }

        return;
      }

      if (matchedFlag instanceof StringFlag) {

        StringFlag stringFlag_ = (StringFlag) matchedFlag;
        String previousFlag = (String) protectedRegion.getFlag(matchedFlag);
        String previousFlagValue
          = previousFlag != null ? previousFlag.toString() : "";

        try {

          if (flagValue.equalsIgnoreCase("null")) {

            protectedRegion.setFlag(stringFlag_, null);
          } else {

            protectedRegion.setFlag(
              stringFlag_,
              stringFlag_.parseInput(
                FlagContext.create()
                  .setSender(
                    ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapCommandSender(
                      player))
                  .setInput(flagValue)
                  .setObject("region", protectedRegion)
                  .build()));
          }

          /*player.sendMessage(
            this.zoneMenuPlugin.colorCode(
              '&',
              ((String) this.zoneMenuPlugin.configDelegate
                .getMap()
                .get("zone_flag_changed"))
                .replace("{0}", matchedFlag.getName())
                .replace("{1}", protectedRegion.getId())
                .replace("{2}", previousFlagValue)
                .replace("{3}", flagArguments)));*/
        } catch (InvalidFlagFormat e) {

          /*player.sendMessage(
            ChatColor.RED
            + "/zone flag "
            + arguments[1]
            + " "
            + arguments[2]
            + " <null|String ...>");*/
        }

        return;
      }

      if (matchedFlag instanceof BooleanFlag) {

        BooleanFlag booleanFlag_ = (BooleanFlag) matchedFlag;
        Boolean previousFlag = (Boolean) protectedRegion.getFlag(matchedFlag);
        String previousFlagValue
          = previousFlag != null ? previousFlag.toString() : "";

        try {

          if (flagValue.equalsIgnoreCase("null")) {

            protectedRegion.setFlag(booleanFlag_, null);
          } else {

            protectedRegion.setFlag(
              booleanFlag_,
              booleanFlag_.parseInput(
                FlagContext.create()
                  .setSender(
                    ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapCommandSender(
                      player))
                  .setInput(flagValue)
                  .setObject("region", protectedRegion)
                  .build()));
          }

          /*player.sendMessage(
            this.zoneMenuPlugin.colorCode(
              '&',
              ((String) zoneMenuPlugin.configDelegate
                .getMap()
                .get("zone_flag_changed"))
                .replace("{0}", matchedFlag.getName())
                .replace("{1}", protectedRegion.getId())
                .replace("{2}", previousFlagValue)
                .replace("{3}", flagArguments)));*/
        } catch (InvalidFlagFormat e) {

          /*player.sendMessage(
            ChatColor.RED
            + "/zone flag "
            + arguments[1]
            + " "
            + arguments[2]
            + " <null|true|false>");*/
        }

        return;
      }

      if (matchedFlag instanceof IntegerFlag) {

        IntegerFlag integerFlag_ = (IntegerFlag) matchedFlag;
        Integer previousFlag = (Integer) protectedRegion.getFlag(matchedFlag);
        String previousFlagValue
          = previousFlag != null ? previousFlag.toString() : "";

        try {

          if (flagValue.equalsIgnoreCase("null")) {

            protectedRegion.setFlag(integerFlag_, null);
          } else {

            protectedRegion.setFlag(
              integerFlag_,
              integerFlag_.parseInput(
                FlagContext.create()
                  .setSender(
                    ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapCommandSender(
                      player))
                  .setInput(flagValue)
                  .setObject("region", protectedRegion)
                  .build()));
          }

          /*player.sendMessage(
            this.zoneMenuPlugin.colorCode(
              '&',
              ((String) zoneMenuPlugin.configDelegate
                .getMap()
                .get("zone_flag_changed"))
                .replace("{0}", matchedFlag.getName())
                .replace("{1}", protectedRegion.getId())
                .replace("{2}", previousFlagValue)
                .replace("{3}", flagArguments)));*/
        } catch (InvalidFlagFormat e) {

          /* player.sendMessage(
            ChatColor.RED
            + "/zone flag "
            + arguments[1]
            + " "
            + arguments[2]
            + " <null|Integer>");*/
        }

        return;
      }

      if (matchedFlag instanceof DoubleFlag) {

        DoubleFlag doubleFlag_ = (DoubleFlag) matchedFlag;
        Double previousFlag = (Double) protectedRegion.getFlag(matchedFlag);
        String previousFlagValue
          = previousFlag != null ? previousFlag.toString() : "";

        try {

          if (flagValue.equalsIgnoreCase("null")) {

            protectedRegion.setFlag(doubleFlag_, null);
          } else {

            protectedRegion.setFlag(
              doubleFlag_,
              doubleFlag_.parseInput(
                FlagContext.create()
                  .setSender(
                    ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapCommandSender(
                      player))
                  .setInput(flagValue)
                  .setObject("region", protectedRegion)
                  .build()));
          }

          /*player.sendMessage(
            this.zoneMenuPlugin.colorCode(
              '&',
              ((String) this.zoneMenuPlugin.configDelegate
                .getMap()
                .get("zone_flag_changed"))
                .replace("{0}", matchedFlag.getName())
                .replace("{1}", protectedRegion.getId())
                .replace("{2}", previousFlagValue)
                .replace("{3}", flagArguments)));*/
        } catch (InvalidFlagFormat e) {

          /*player.sendMessage(
            ChatColor.RED
            + "/zone flag "
            + arguments[1]
            + " "
            + arguments[2]
            + " <null|Double>");*/
        }

        return;
      }

      if (matchedFlag instanceof LocationFlag) {

        LocationFlag locationFlag_ = (LocationFlag) matchedFlag;
        com.sk89q.worldedit.util.Location previousFlag = (com.sk89q.worldedit.util.Location) protectedRegion.getFlag(matchedFlag);
        String previousFlagValue
          = previousFlag != null ? previousFlag.toString() : "";

        try {

          if (flagValue.equalsIgnoreCase("null")) {

            protectedRegion.setFlag(locationFlag_, null);
          } else {

            protectedRegion.setFlag(
              locationFlag_,
              locationFlag_.parseInput(
                FlagContext.create()
                  .setSender(
                    ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapCommandSender(
                      player))
                  .setInput(flagValue)
                  .setObject("region", protectedRegion)
                  .build()));
          }

          /*player.sendMessage(
            this.zoneMenuPlugin.colorCode(
              '&',
              ((String) zoneMenuPlugin.configDelegate
                .getMap()
                .get("zone_flag_changed"))
                .replace("{0}", matchedFlag.getName())
                .replace("{1}", protectedRegion.getId())
                .replace("{2}", previousFlagValue)
                .replace("{3}", flagArguments)));*/
        } catch (InvalidFlagFormat e) {

          /*player.sendMessage(
            ChatColor.RED
            + "/zone flag "
            + arguments[1]
            + " "
            + arguments[2]
            + " <null|here|X,Y,Z>");*/
        }

        return;
      }

      if (matchedFlag instanceof EnumFlag<?>) {

        if (Flags.GAME_MODE
          .getName()
          .equalsIgnoreCase(((EnumFlag<?>) matchedFlag).getName())) {

          EnumFlag<GameMode> enumFlag_ = (EnumFlag<GameMode>) matchedFlag;
          GameMode previousFlag = (GameMode) protectedRegion.getFlag(matchedFlag);
          String previousFlagValue
            = previousFlag != null ? previousFlag.toString() : "";

          try {

            if (flagValue.equalsIgnoreCase("null")) {

              protectedRegion.setFlag(enumFlag_, null);
            } else {

              protectedRegion.setFlag(
                enumFlag_,
                enumFlag_.parseInput(
                  FlagContext.create()
                    .setSender(
                      ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapCommandSender(
                        player))
                    .setInput(flagValue)
                    .setObject("region", protectedRegion)
                    .build()));
            }

            /*player.sendMessage(
              zoneMenuPlugin.colorCode(
                '&',
                ((String) zoneMenuPlugin.configDelegate
                  .getMap()
                  .get("zone_flag_changed"))
                  .replace("{0}", matchedFlag.getName())
                  .replace("{1}", protectedRegion.getId())
                  .replace("{2}", previousFlagValue)
                  .replace("{3}", flagArguments)));*/
          } catch (InvalidFlagFormat e) {

            /*player.sendMessage(
              ChatColor.RED
              + "/zone flag "
              + arguments[1]
              + " "
              + arguments[2]
              + " <null|GameMode>");*/
          }

          return;
        }

        if (Flags.WEATHER_LOCK
          .getName()
          .equalsIgnoreCase(((EnumFlag<?>) matchedFlag).getName())) {

          EnumFlag<WeatherType> enumFlag_ = (EnumFlag<WeatherType>) matchedFlag;
          WeatherType previousFlag
            = (WeatherType) protectedRegion.getFlag(matchedFlag);
          String previousFlagValue
            = previousFlag != null ? previousFlag.toString() : "";

          try {

            if (flagValue.equalsIgnoreCase("null")) {

              protectedRegion.setFlag(enumFlag_, null);
            } else {

              protectedRegion.setFlag(
                enumFlag_,
                enumFlag_.parseInput(
                  FlagContext.create()
                    .setSender(
                      ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapCommandSender(
                        player))
                    .setInput(flagValue)
                    .setObject("region", protectedRegion)
                    .build()));
            }

            /*player.sendMessage(
              this.zoneMenuPlugin.colorCode(
                '&',
                ((String) zoneMenuPlugin.configDelegate
                  .getMap()
                  .get("zone_flag_changed"))
                  .replace("{0}", matchedFlag.getName())
                  .replace("{1}", protectedRegion.getId())
                  .replace("{2}", previousFlagValue)
                  .replace("{3}", flagArguments)));*/
          } catch (InvalidFlagFormat e) {

            /*player.sendMessage(
              ChatColor.RED
              + "/zone flag "
              + arguments[1]
              + " "
              + arguments[2]
              + " <null|WeatherType>");*/
          }

          return;
        }
      }
    });

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
