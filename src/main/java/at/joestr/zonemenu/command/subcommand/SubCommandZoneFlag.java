//
// MIT License
//
// Copyright (c) 2017-2022 Joel Strasser <joelstrasser1@gmail.com>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package at.joestr.zonemenu.command.subcommand;

import at.joestr.zonemenu.ZoneMenuPlugin;
import com.sk89q.worldedit.util.Location;
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
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.WeatherType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SubCommandZoneFlag {

  ZoneMenuPlugin zoneMenuPlugin = null;

  public SubCommandZoneFlag(ZoneMenuPlugin plugin) {

    this.zoneMenuPlugin = plugin;
  }

  @SuppressWarnings("unchecked")
  public void process(Player player, String[] arguments) {

    // If arguments' length does not equals 1 ...
    if (arguments.length < 4) {

      // ... wrong usage of "/zone cancel".
      // Send the player a message.
      player.sendMessage(
          this.zoneMenuPlugin.colorCode(
              '&',
              ((String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message"))
                  .replace("{0}", "/zone flag <Zone> <Flag> <Flagvalue>")));

      return;
    }

    this.zoneMenuPlugin.futuristicRegionProcessing(
        player,
        true,
        (List<ProtectedRegion> t, Throwable u) -> {

          // If the list is empty ...
          if (t.isEmpty()) {

            // ... send the player a message.
            player.sendMessage(
                this.zoneMenuPlugin.colorCode(
                    '&', ((String) this.zoneMenuPlugin.configDelegate.getMap().get("no_zone"))));

            return;
          }

          Bukkit.getScheduler()
              .runTask(
                  zoneMenuPlugin,
                  () -> {
                    // Initialise new region
                    ProtectedRegion protectedRegion = null;

                    // Loop through all regions ...
                    for (ProtectedRegion protectedRegion_ : t) {

                      // ... and if the region ID equals the second argument (<Zone>) ...
                      if (protectedRegion_.getId().equalsIgnoreCase(arguments[1])) {

                        // ... set the found region.
                        protectedRegion = protectedRegion_;
                        break;
                      }
                    }

                    // If region equals null ...
                    if (protectedRegion == null) {

                      // ... no region with this ID was not found.
                      // Send the player a message.
                      player.sendMessage(
                          this.zoneMenuPlugin.colorCode(
                              '&',
                              ((String)
                                      this.zoneMenuPlugin
                                          .configDelegate
                                          .getMap()
                                          .get("not_exisiting_zone"))
                                  .replace("{0}", arguments[1])));

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

                    Flag<?> matchedFlag =
                        Flags.fuzzyMatchFlag(
                            WorldGuard.getInstance().getFlagRegistry(), arguments[2]);

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
                      StateFlag.State previousFlag =
                          (StateFlag.State) protectedRegion.getFlag(matchedFlag);
                      String previousFlagValue =
                          previousFlag != null ? previousFlag.toString() : "";

                      try {

                        // If the flag argument equals "null" ...
                        if (flagArguments.equalsIgnoreCase("null")) {

                          // ... unset the flag.
                          protectedRegion.setFlag(stateFlag_, null);
                        } else {

                          // If not set the flag with the parsed input.
                          protectedRegion.setFlag(
                              stateFlag_,
                              stateFlag_.parseInput(
                                  FlagContext.create()
                                      .setSender(
                                          this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(
                                              player))
                                      .setInput(flagArguments)
                                      .setObject("region", protectedRegion)
                                      .build()));
                        }

                        // Send the player a message.
                        player.sendMessage(
                            this.zoneMenuPlugin.colorCode(
                                '&',
                                ((String)
                                        zoneMenuPlugin
                                            .configDelegate
                                            .getMap()
                                            .get("zone_flag_changed"))
                                    .replace("{0}", matchedFlag.getName())
                                    .replace("{1}", protectedRegion.getId())
                                    .replace("{2}", previousFlagValue)
                                    .replace("{3}", flagArguments)));

                      } catch (InvalidFlagFormat exception) {

                        // If there was an exception while parsing the flag send a message to the
                        // player.
                        player.sendMessage(
                            ChatColor.RED
                                + "/zone flag "
                                + arguments[1]
                                + " "
                                + arguments[2]
                                + " <null|allow|deny|none>");
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
                        String previousFlagValue =
                            previousFlag != null ? previousFlag.toString() : "";

                        try {

                          if (flagArguments.equalsIgnoreCase("null")) {

                            protectedRegion.setFlag(setFlag_, null);
                          } else {

                            protectedRegion.setFlag(
                                setFlag_,
                                setFlag_.parseInput(
                                    FlagContext.create()
                                        .setSender(
                                            this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(
                                                player))
                                        .setInput(flagArguments)
                                        .setObject("region", protectedRegion)
                                        .build()));
                          }

                          player.sendMessage(
                              this.zoneMenuPlugin.colorCode(
                                  '&',
                                  ((String)
                                          zoneMenuPlugin
                                              .configDelegate
                                              .getMap()
                                              .get("zone_flag_changed"))
                                      .replace("{0}", matchedFlag.getName())
                                      .replace("{1}", protectedRegion.getId())
                                      .replace("{2}", previousFlagValue)
                                      .replace("{3}", flagArguments)));
                        } catch (InvalidFlagFormat e) {

                          player.sendMessage(
                              ChatColor.RED
                                  + "/zone flag "
                                  + arguments[1]
                                  + " "
                                  + arguments[2]
                                  + " <null|EntityType[,EntityType ...]>");
                        }

                        return;
                      }

                      // We do not know the type of the set flag here.
                      Type type =
                          ((ParameterizedType) matchedFlag.getClass().getGenericSuperclass())
                              .getActualTypeArguments()[0];

                      SetFlag<Type> setFlag_ = (SetFlag<Type>) matchedFlag;
                      Type previousFlag = (Type) protectedRegion.getFlag(matchedFlag);
                      String previousFlagValue =
                          previousFlag != null ? previousFlag.toString() : "";

                      try {

                        if (flagArguments.equalsIgnoreCase("null")) {

                          protectedRegion.setFlag(setFlag_, null);
                        } else {

                          protectedRegion.setFlag(
                              setFlag_,
                              setFlag_.parseInput(
                                  FlagContext.create()
                                      .setSender(
                                          this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(
                                              player))
                                      .setInput(flagArguments)
                                      .setObject("region", protectedRegion)
                                      .build()));
                        }

                        player.sendMessage(
                            this.zoneMenuPlugin.colorCode(
                                '&',
                                ((String)
                                        zoneMenuPlugin
                                            .configDelegate
                                            .getMap()
                                            .get("zone_flag_changed"))
                                    .replace("{0}", matchedFlag.getName())
                                    .replace("{1}", protectedRegion.getId())
                                    .replace("{2}", previousFlagValue)
                                    .replace("{3}", flagArguments)));
                      } catch (InvalidFlagFormat e) {

                        player.sendMessage(
                            ChatColor.RED
                                + "/zone flag "
                                + arguments[1]
                                + " "
                                + arguments[2]
                                + " <null|"
                                + type.getTypeName()
                                + "[,"
                                + type.getTypeName()
                                + " ...]>");
                      }

                      return;
                    }

                    if (matchedFlag instanceof StringFlag) {

                      StringFlag stringFlag_ = (StringFlag) matchedFlag;
                      String previousFlag = (String) protectedRegion.getFlag(matchedFlag);
                      String previousFlagValue =
                          previousFlag != null ? previousFlag.toString() : "";

                      try {

                        if (flagArguments.equalsIgnoreCase("null")) {

                          protectedRegion.setFlag(stringFlag_, null);
                        } else {

                          protectedRegion.setFlag(
                              stringFlag_,
                              stringFlag_.parseInput(
                                  FlagContext.create()
                                      .setSender(
                                          this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(
                                              player))
                                      .setInput(flagArguments)
                                      .setObject("region", protectedRegion)
                                      .build()));
                        }

                        player.sendMessage(
                            this.zoneMenuPlugin.colorCode(
                                '&',
                                ((String)
                                        this.zoneMenuPlugin
                                            .configDelegate
                                            .getMap()
                                            .get("zone_flag_changed"))
                                    .replace("{0}", matchedFlag.getName())
                                    .replace("{1}", protectedRegion.getId())
                                    .replace("{2}", previousFlagValue)
                                    .replace("{3}", flagArguments)));
                      } catch (InvalidFlagFormat e) {

                        player.sendMessage(
                            ChatColor.RED
                                + "/zone flag "
                                + arguments[1]
                                + " "
                                + arguments[2]
                                + " <null|String ...>");
                      }

                      return;
                    }

                    if (matchedFlag instanceof BooleanFlag) {

                      BooleanFlag booleanFlag_ = (BooleanFlag) matchedFlag;
                      Boolean previousFlag = (Boolean) protectedRegion.getFlag(matchedFlag);
                      String previousFlagValue =
                          previousFlag != null ? previousFlag.toString() : "";

                      try {

                        if (flagArguments.equalsIgnoreCase("null")) {

                          protectedRegion.setFlag(booleanFlag_, null);
                        } else {

                          protectedRegion.setFlag(
                              booleanFlag_,
                              booleanFlag_.parseInput(
                                  FlagContext.create()
                                      .setSender(
                                          this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(
                                              player))
                                      .setInput(flagArguments)
                                      .setObject("region", protectedRegion)
                                      .build()));
                        }

                        player.sendMessage(
                            this.zoneMenuPlugin.colorCode(
                                '&',
                                ((String)
                                        zoneMenuPlugin
                                            .configDelegate
                                            .getMap()
                                            .get("zone_flag_changed"))
                                    .replace("{0}", matchedFlag.getName())
                                    .replace("{1}", protectedRegion.getId())
                                    .replace("{2}", previousFlagValue)
                                    .replace("{3}", flagArguments)));
                      } catch (InvalidFlagFormat e) {

                        player.sendMessage(
                            ChatColor.RED
                                + "/zone flag "
                                + arguments[1]
                                + " "
                                + arguments[2]
                                + " <null|true|false>");
                      }

                      return;
                    }

                    if (matchedFlag instanceof IntegerFlag) {

                      IntegerFlag integerFlag_ = (IntegerFlag) matchedFlag;
                      Integer previousFlag = (Integer) protectedRegion.getFlag(matchedFlag);
                      String previousFlagValue =
                          previousFlag != null ? previousFlag.toString() : "";

                      try {

                        if (flagArguments.equalsIgnoreCase("null")) {

                          protectedRegion.setFlag(integerFlag_, null);
                        } else {

                          protectedRegion.setFlag(
                              integerFlag_,
                              integerFlag_.parseInput(
                                  FlagContext.create()
                                      .setSender(
                                          this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(
                                              player))
                                      .setInput(flagArguments)
                                      .setObject("region", protectedRegion)
                                      .build()));
                        }

                        player.sendMessage(
                            this.zoneMenuPlugin.colorCode(
                                '&',
                                ((String)
                                        zoneMenuPlugin
                                            .configDelegate
                                            .getMap()
                                            .get("zone_flag_changed"))
                                    .replace("{0}", matchedFlag.getName())
                                    .replace("{1}", protectedRegion.getId())
                                    .replace("{2}", previousFlagValue)
                                    .replace("{3}", flagArguments)));
                      } catch (InvalidFlagFormat e) {

                        player.sendMessage(
                            ChatColor.RED
                                + "/zone flag "
                                + arguments[1]
                                + " "
                                + arguments[2]
                                + " <null|Integer>");
                      }

                      return;
                    }

                    if (matchedFlag instanceof DoubleFlag) {

                      DoubleFlag doubleFlag_ = (DoubleFlag) matchedFlag;
                      Double previousFlag = (Double) protectedRegion.getFlag(matchedFlag);
                      String previousFlagValue =
                          previousFlag != null ? previousFlag.toString() : "";

                      try {

                        if (flagArguments.equalsIgnoreCase("null")) {

                          protectedRegion.setFlag(doubleFlag_, null);
                        } else {

                          protectedRegion.setFlag(
                              doubleFlag_,
                              doubleFlag_.parseInput(
                                  FlagContext.create()
                                      .setSender(
                                          this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(
                                              player))
                                      .setInput(flagArguments)
                                      .setObject("region", protectedRegion)
                                      .build()));
                        }

                        player.sendMessage(
                            this.zoneMenuPlugin.colorCode(
                                '&',
                                ((String)
                                        this.zoneMenuPlugin
                                            .configDelegate
                                            .getMap()
                                            .get("zone_flag_changed"))
                                    .replace("{0}", matchedFlag.getName())
                                    .replace("{1}", protectedRegion.getId())
                                    .replace("{2}", previousFlagValue)
                                    .replace("{3}", flagArguments)));
                      } catch (InvalidFlagFormat e) {

                        player.sendMessage(
                            ChatColor.RED
                                + "/zone flag "
                                + arguments[1]
                                + " "
                                + arguments[2]
                                + " <null|Double>");
                      }

                      return;
                    }

                    if (matchedFlag instanceof LocationFlag) {

                      LocationFlag locationFlag_ = (LocationFlag) matchedFlag;
                      Location previousFlag = (Location) protectedRegion.getFlag(matchedFlag);
                      String previousFlagValue =
                          previousFlag != null ? previousFlag.toString() : "";

                      try {

                        if (flagArguments.equalsIgnoreCase("null")) {

                          protectedRegion.setFlag(locationFlag_, null);
                        } else {

                          protectedRegion.setFlag(
                              locationFlag_,
                              locationFlag_.parseInput(
                                  FlagContext.create()
                                      .setSender(
                                          this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(
                                              player))
                                      .setInput(flagArguments)
                                      .setObject("region", protectedRegion)
                                      .build()));
                        }

                        player.sendMessage(
                            this.zoneMenuPlugin.colorCode(
                                '&',
                                ((String)
                                        zoneMenuPlugin
                                            .configDelegate
                                            .getMap()
                                            .get("zone_flag_changed"))
                                    .replace("{0}", matchedFlag.getName())
                                    .replace("{1}", protectedRegion.getId())
                                    .replace("{2}", previousFlagValue)
                                    .replace("{3}", flagArguments)));
                      } catch (InvalidFlagFormat e) {

                        player.sendMessage(
                            ChatColor.RED
                                + "/zone flag "
                                + arguments[1]
                                + " "
                                + arguments[2]
                                + " <null|here|X,Y,Z>");
                      }

                      return;
                    }

                    if (matchedFlag instanceof EnumFlag<?>) {

                      if (Flags.GAME_MODE
                          .getName()
                          .equalsIgnoreCase(((EnumFlag<?>) matchedFlag).getName())) {

                        EnumFlag<GameMode> enumFlag_ = (EnumFlag<GameMode>) matchedFlag;
                        GameMode previousFlag = (GameMode) protectedRegion.getFlag(matchedFlag);
                        String previousFlagValue =
                            previousFlag != null ? previousFlag.toString() : "";

                        try {

                          if (flagArguments.equalsIgnoreCase("null")) {

                            protectedRegion.setFlag(enumFlag_, null);
                          } else {

                            protectedRegion.setFlag(
                                enumFlag_,
                                enumFlag_.parseInput(
                                    FlagContext.create()
                                        .setSender(
                                            this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(
                                                player))
                                        .setInput(flagArguments)
                                        .setObject("region", protectedRegion)
                                        .build()));
                          }

                          player.sendMessage(
                              zoneMenuPlugin.colorCode(
                                  '&',
                                  ((String)
                                          zoneMenuPlugin
                                              .configDelegate
                                              .getMap()
                                              .get("zone_flag_changed"))
                                      .replace("{0}", matchedFlag.getName())
                                      .replace("{1}", protectedRegion.getId())
                                      .replace("{2}", previousFlagValue)
                                      .replace("{3}", flagArguments)));
                        } catch (InvalidFlagFormat e) {

                          player.sendMessage(
                              ChatColor.RED
                                  + "/zone flag "
                                  + arguments[1]
                                  + " "
                                  + arguments[2]
                                  + " <null|GameMode>");
                        }

                        return;
                      }

                      if (Flags.WEATHER_LOCK
                          .getName()
                          .equalsIgnoreCase(((EnumFlag<?>) matchedFlag).getName())) {

                        EnumFlag<WeatherType> enumFlag_ = (EnumFlag<WeatherType>) matchedFlag;
                        WeatherType previousFlag =
                            (WeatherType) protectedRegion.getFlag(matchedFlag);
                        String previousFlagValue =
                            previousFlag != null ? previousFlag.toString() : "";

                        try {

                          if (flagArguments.equalsIgnoreCase("null")) {

                            protectedRegion.setFlag(enumFlag_, null);
                          } else {

                            protectedRegion.setFlag(
                                enumFlag_,
                                enumFlag_.parseInput(
                                    FlagContext.create()
                                        .setSender(
                                            this.zoneMenuPlugin.worldGuardPlugin.wrapCommandSender(
                                                player))
                                        .setInput(flagArguments)
                                        .setObject("region", protectedRegion)
                                        .build()));
                          }

                          player.sendMessage(
                              this.zoneMenuPlugin.colorCode(
                                  '&',
                                  ((String)
                                          zoneMenuPlugin
                                              .configDelegate
                                              .getMap()
                                              .get("zone_flag_changed"))
                                      .replace("{0}", matchedFlag.getName())
                                      .replace("{1}", protectedRegion.getId())
                                      .replace("{2}", previousFlagValue)
                                      .replace("{3}", flagArguments)));
                        } catch (InvalidFlagFormat e) {

                          player.sendMessage(
                              ChatColor.RED
                                  + "/zone flag "
                                  + arguments[1]
                                  + " "
                                  + arguments[2]
                                  + " <null|WeatherType>");
                        }

                        return;
                      }
                    }
                  });
        });
  }
}
