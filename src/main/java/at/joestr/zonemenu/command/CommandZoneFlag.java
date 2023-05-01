//
// MIT License
//
// Copyright (c) 2017-2023 Joel Strasser <joelstrasser1@gmail.com>
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
package at.joestr.zonemenu.command;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.configuration.LocaleHelper;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.javacommon.spigotutils.SpigotUtils;
import at.joestr.zonemenu.configuration.CurrentEntries;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import org.bukkit.GameMode;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CommandZoneFlag implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneFlag.class.getName());

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      new MessageHelper(LanguageConfiguration.getInstance().getResolver())
        .locale(Locale.ENGLISH)
        .path(CurrentEntries.LANG_GEN_NOT_A_PLAYER.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();
      return true;
    }

    List<String> parsedArgs = SpigotUtils.parseSingelQuotedArguments(args);

    if (parsedArgs.size() != 3) {
      return false;
    }

    String zoneName = parsedArgs.get(0);
    String flagName = parsedArgs.get(1);
    String flagValue = parsedArgs.get(2);
    Player player = (Player) sender;

    ZoneMenuManager.getInstance().futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {
      if (t.isEmpty()) {
        new MessageHelper(LanguageConfiguration.getInstance().getResolver())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_GEN_NO_ZONE.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
        return;
      }

      final ArrayList<ProtectedRegion> protectedRegions = new ArrayList<>();

      for (ProtectedRegion protectedRegion_ : t) {
        if (protectedRegion_.getId().equalsIgnoreCase(zoneName)) {
          protectedRegions.add(protectedRegion_);
          break;
        }
      }

      if (protectedRegions.isEmpty()) {
        new MessageHelper(LanguageConfiguration.getInstance().getResolver())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_GEN_NOT_EXISTING_ZONE.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
        return;
      }

      ProtectedRegion protectedRegion = protectedRegions.get(0);

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
          if (flagValue.equalsIgnoreCase("null")) {
            protectedRegion.setFlag(stateFlag_, null);
          } else {
            protectedRegion.setFlag(
              stateFlag_,
              stateFlag_.parseInput(
                FlagContext.create()
                  .setSender(
                    ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapCommandSender(
                      player))
                  .setInput(flagValue)
                  .setObject("region", protectedRegion)
                  .build()
              )
            );
          }

          new MessageHelper(LanguageConfiguration.getInstance().getResolver())
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
            .prefixPath(CurrentEntries.LANG_PREFIX.toString())
            .showPrefix(true)
            .receiver(sender)
            .modify(s -> s.replace("%flagname", matchedFlag.getName()))
            .modify(s -> s.replace("%zonename", protectedRegion.getId()))
            .modify(s -> s.replace("%oldvalue", previousFlagValue))
            .modify(s -> s.replace("%newvalue", flagValue))
            .send();
        } catch (InvalidFlagFormat exception) {
          return;
        }

        return;
      }

      if (matchedFlag instanceof SetFlag<?>) {
        if (Flags.DENY_SPAWN
          .getName()
          .equalsIgnoreCase(((SetFlag<?>) matchedFlag).getName())) {

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

            new MessageHelper(LanguageConfiguration.getInstance().getResolver())
              .locale(LocaleHelper.resolve(player.getLocale()))
              .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
              .prefixPath(CurrentEntries.LANG_PREFIX.toString())
              .showPrefix(true)
              .receiver(sender)
              .modify(s -> s.replace("%flagname", matchedFlag.getName()))
              .modify(s -> s.replace("%zonename", protectedRegion.getId()))
              .modify(s -> s.replace("%oldvalue", previousFlagValue))
              .modify(s -> s.replace("%newvalue", flagValue))
              .send();
          } catch (InvalidFlagFormat e) {
            return;
          }

          return;
        }

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

          new MessageHelper(LanguageConfiguration.getInstance().getResolver())
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
            .prefixPath(CurrentEntries.LANG_PREFIX.toString())
            .showPrefix(true)
            .receiver(sender)
            .modify(s -> s.replace("%flagname", matchedFlag.getName()))
            .modify(s -> s.replace("%zonename", protectedRegion.getId()))
            .modify(s -> s.replace("%oldvalue", previousFlagValue))
            .modify(s -> s.replace("%newvalue", flagValue))
            .send();
        } catch (InvalidFlagFormat e) {
          return;
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

          new MessageHelper(LanguageConfiguration.getInstance().getResolver())
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
            .prefixPath(CurrentEntries.LANG_PREFIX.toString())
            .showPrefix(true)
            .receiver(sender)
            .modify(s -> s.replace("%flagname", matchedFlag.getName()))
            .modify(s -> s.replace("%zonename", protectedRegion.getId()))
            .modify(s -> s.replace("%oldvalue", previousFlagValue))
            .modify(s -> s.replace("%newvalue", flagValue))
            .send();
        } catch (InvalidFlagFormat e) {
          return;
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

          new MessageHelper(LanguageConfiguration.getInstance().getResolver())
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
            .prefixPath(CurrentEntries.LANG_PREFIX.toString())
            .showPrefix(true)
            .receiver(sender)
            .modify(s -> s.replace("%flagname", matchedFlag.getName()))
            .modify(s -> s.replace("%zonename", protectedRegion.getId()))
            .modify(s -> s.replace("%oldvalue", previousFlagValue))
            .modify(s -> s.replace("%newvalue", flagValue))
            .send();
        } catch (InvalidFlagFormat e) {
          return;
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

          new MessageHelper(LanguageConfiguration.getInstance().getResolver())
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
            .prefixPath(CurrentEntries.LANG_PREFIX.toString())
            .showPrefix(true)
            .receiver(sender)
            .modify(s -> s.replace("%flagname", matchedFlag.getName()))
            .modify(s -> s.replace("%zonename", protectedRegion.getId()))
            .modify(s -> s.replace("%oldvalue", previousFlagValue))
            .modify(s -> s.replace("%newvalue", flagValue))
            .send();
        } catch (InvalidFlagFormat e) {
          return;
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

          new MessageHelper(LanguageConfiguration.getInstance().getResolver())
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
            .prefixPath(CurrentEntries.LANG_PREFIX.toString())
            .showPrefix(true)
            .receiver(sender)
            .modify(s -> s.replace("%flagname", matchedFlag.getName()))
            .modify(s -> s.replace("%zonename", protectedRegion.getId()))
            .modify(s -> s.replace("%oldvalue", previousFlagValue))
            .modify(s -> s.replace("%newvalue", flagValue))
            .send();
        } catch (InvalidFlagFormat e) {
          return;
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

          new MessageHelper(LanguageConfiguration.getInstance().getResolver())
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
            .prefixPath(CurrentEntries.LANG_PREFIX.toString())
            .showPrefix(true)
            .receiver(sender)
            .modify(s -> s.replace("%flagname", matchedFlag.getName()))
            .modify(s -> s.replace("%zonename", protectedRegion.getId()))
            .modify(s -> s.replace("%oldvalue", previousFlagValue))
            .modify(s -> s.replace("%newvalue", flagValue))
            .send();
        } catch (InvalidFlagFormat e) {
          return;
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

            new MessageHelper(LanguageConfiguration.getInstance().getResolver())
              .locale(LocaleHelper.resolve(player.getLocale()))
              .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
              .prefixPath(CurrentEntries.LANG_PREFIX.toString())
              .showPrefix(true)
              .receiver(sender)
              .modify(s -> s.replace("%flagname", matchedFlag.getName()))
              .modify(s -> s.replace("%zonename", protectedRegion.getId()))
              .modify(s -> s.replace("%oldvalue", previousFlagValue))
              .modify(s -> s.replace("%newvalue", flagValue))
              .send();
          } catch (InvalidFlagFormat e) {
            return;
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

            new MessageHelper(LanguageConfiguration.getInstance().getResolver())
              .locale(LocaleHelper.resolve(player.getLocale()))
              .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
              .prefixPath(CurrentEntries.LANG_PREFIX.toString())
              .showPrefix(true)
              .receiver(sender)
              .modify(s -> s.replace("%flagname", matchedFlag.getName()))
              .modify(s -> s.replace("%zonename", protectedRegion.getId()))
              .modify(s -> s.replace("%oldvalue", previousFlagValue))
              .modify(s -> s.replace("%newvalue", flagValue))
              .send();
          } catch (InvalidFlagFormat e) {
            return;
          }
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
