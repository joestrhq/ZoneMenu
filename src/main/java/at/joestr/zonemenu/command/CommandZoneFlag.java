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
import at.joestr.zonemenu.util.ZoneMenuUtils;
import com.sk89q.worldguard.LocalPlayer;
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
import java.util.function.BiFunction;
import java.util.logging.Level;
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
  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      new MessageHelper(languageResolverFunction)
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
    String regionName = ZoneMenuUtils.zoneToRegionName(zoneName);
    String flagName = parsedArgs.get(1);
    String flagValue = parsedArgs.get(2);
    Player player = (Player) sender;
    LocalPlayer worldGuardLocalPlayer
      = ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapPlayer(player);

    ZoneMenuManager.getInstance().futuristicRegionProcessing(player, true, (List<ProtectedRegion> foundRegions, Throwable u) -> {
      if (foundRegions.isEmpty()) {
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_GEN_NO_ZONE.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
        return;
      }

      final ArrayList<ProtectedRegion> targetRegions = new ArrayList<>();

      for (ProtectedRegion foundRegion : foundRegions) {
        if (foundRegion.getId().equalsIgnoreCase(regionName)) {
          targetRegions.add(foundRegion);
          break;
        }
      }

      if (targetRegions.isEmpty()) {
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_GEN_NOT_EXISTING_ZONE.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
        return;
      }

      ProtectedRegion targetRegion = targetRegions.get(0);

      Flag<?> fuzzyMatchedFlag
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
      if (fuzzyMatchedFlag instanceof StateFlag) {

        StateFlag matchedFlag = (StateFlag) fuzzyMatchedFlag;
        StateFlag.State previousFlagState
          = (StateFlag.State) targetRegion.getFlag(fuzzyMatchedFlag);
        String previousFlagValue
          = previousFlagState != null ? previousFlagState.toString() : "";

        try {
          if (flagValue.equalsIgnoreCase("null")) {
            targetRegion.setFlag(matchedFlag, null);
          } else {
            targetRegion.setFlag(
              matchedFlag,
              matchedFlag.parseInput(
                FlagContext.create()
                  .setSender(worldGuardLocalPlayer)
                  .setInput(flagValue)
                  .setObject("region", targetRegion)
                  .build()
              )
            );
          }

          new MessageHelper(languageResolverFunction)
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
            .modify(s -> s.replace("%flagname", fuzzyMatchedFlag.getName()))
            .modify(s -> s.replace("%zonename", zoneName))
            .modify(s -> s.replace("%oldvalue", previousFlagValue))
            .modify(s -> s.replace("%newvalue", flagValue))
            .prefixPath(CurrentEntries.LANG_PREFIX.toString())
            .showPrefix(true)
            .receiver(sender)
            .send();
        } catch (InvalidFlagFormat exception) {
          String logMessage = new StringBuilder()
            .append("Error parsing flag value '")
            .append(flagValue)
            .append("' for flag '")
            .append(matchedFlag.getName())
            .append("' of type '")
            .append(matchedFlag.getClass().getName())
            .append("'!")
            .toString();
          LOG.log(Level.SEVERE, logMessage, exception);
        }
      } else if (fuzzyMatchedFlag instanceof SetFlag<?>) {
        String setFlagName = ((SetFlag<?>) fuzzyMatchedFlag).getName();

        if (Flags.DENY_SPAWN.getName().equalsIgnoreCase(setFlagName)) {
          SetFlag<EntityType> matchedFlag
            = (SetFlag<EntityType>) fuzzyMatchedFlag;
          EntityType previousFlag
            = (EntityType) targetRegion.getFlag(fuzzyMatchedFlag);
          String previousFlagValue
            = previousFlag != null ? previousFlag.toString() : "";

          try {
            if (flagValue.equalsIgnoreCase("null")) {
              targetRegion.setFlag(matchedFlag, null);
            } else {
              targetRegion.setFlag(
                matchedFlag,
                matchedFlag.parseInput(
                  FlagContext.create()
                    .setSender(
                      ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapCommandSender(
                        player))
                    .setInput(flagValue)
                    .setObject("region", targetRegion)
                    .build()));
            }

            new MessageHelper(languageResolverFunction)
              .locale(LocaleHelper.resolve(player.getLocale()))
              .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
              .modify(s -> s.replace("%flagname", fuzzyMatchedFlag.getName()))
              .modify(s -> s.replace("%zonename", zoneName))
              .modify(s -> s.replace("%oldvalue", previousFlagValue))
              .modify(s -> s.replace("%newvalue", flagValue))
              .prefixPath(CurrentEntries.LANG_PREFIX.toString())
              .showPrefix(true)
              .receiver(sender)
              .send();
          } catch (InvalidFlagFormat exception) {
            String logMessage = new StringBuilder()
              .append("Error parsing flag value '")
              .append(flagValue)
              .append("' for flag '")
              .append(matchedFlag.getName())
              .append("' of type '")
              .append(matchedFlag.getClass().getName())
              .append("'!")
              .toString();
            LOG.log(Level.SEVERE, logMessage, exception);
          }
        }

        Type type
          = ((ParameterizedType) fuzzyMatchedFlag.getClass().getGenericSuperclass())
            .getActualTypeArguments()[0];

        SetFlag<Type> matchedFlag = (SetFlag<Type>) fuzzyMatchedFlag;
        Type previousFlag = (Type) targetRegion.getFlag(fuzzyMatchedFlag);
        String previousFlagValue
          = previousFlag != null ? previousFlag.toString() : "";

        try {
          if (flagValue.equalsIgnoreCase("null")) {
            targetRegion.setFlag(matchedFlag, null);
          } else {

            targetRegion.setFlag(
              matchedFlag,
              matchedFlag.parseInput(
                FlagContext.create()
                  .setSender(worldGuardLocalPlayer)
                  .setInput(flagValue)
                  .setObject("region", targetRegion)
                  .build()));
          }

          new MessageHelper(languageResolverFunction)
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
            .modify(s -> s.replace("%flagname", fuzzyMatchedFlag.getName()))
            .modify(s -> s.replace("%zonename", zoneName))
            .modify(s -> s.replace("%oldvalue", previousFlagValue))
            .modify(s -> s.replace("%newvalue", flagValue))
            .prefixPath(CurrentEntries.LANG_PREFIX.toString())
            .showPrefix(true)
            .receiver(sender)
            .send();
        } catch (InvalidFlagFormat exception) {
          String logMessage = new StringBuilder()
            .append("Error parsing flag value '")
            .append(flagValue)
            .append("' for flag '")
            .append(matchedFlag.getName())
            .append("' of type '")
            .append(matchedFlag.getClass().getName())
            .append("'!")
            .toString();
          LOG.log(Level.SEVERE, logMessage, exception);
        }
      } else if (fuzzyMatchedFlag instanceof StringFlag) {
        StringFlag matchedFlag = (StringFlag) fuzzyMatchedFlag;
        String previousFlag = (String) targetRegion.getFlag(fuzzyMatchedFlag);
        String previousFlagValue
          = previousFlag != null ? previousFlag.toString() : "";

        try {
          if (flagValue.equalsIgnoreCase("null")) {
            targetRegion.setFlag(matchedFlag, null);
          } else {
            targetRegion.setFlag(
              matchedFlag,
              matchedFlag.parseInput(
                FlagContext.create()
                  .setSender(worldGuardLocalPlayer)
                  .setInput(flagValue)
                  .setObject("region", targetRegion)
                  .build()));
          }

          new MessageHelper(languageResolverFunction)
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
            .modify(s -> s.replace("%flagname", fuzzyMatchedFlag.getName()))
            .modify(s -> s.replace("%zonename", zoneName))
            .modify(s -> s.replace("%oldvalue", previousFlagValue))
            .modify(s -> s.replace("%newvalue", flagValue))
            .prefixPath(CurrentEntries.LANG_PREFIX.toString())
            .showPrefix(true)
            .receiver(sender)
            .send();
        } catch (InvalidFlagFormat exception) {
          String logMessage = new StringBuilder()
            .append("Error parsing flag value '")
            .append(flagValue)
            .append("' for flag '")
            .append(matchedFlag.getName())
            .append("' of type '")
            .append(matchedFlag.getClass().getName())
            .append("'!")
            .toString();
          LOG.log(Level.SEVERE, logMessage, exception);
        }
      } else if (fuzzyMatchedFlag instanceof BooleanFlag) {
        BooleanFlag matchedFlag = (BooleanFlag) fuzzyMatchedFlag;
        Boolean previousFlag = (Boolean) targetRegion.getFlag(fuzzyMatchedFlag);
        String previousFlagValue
          = previousFlag != null ? previousFlag.toString() : "";

        try {
          if (flagValue.equalsIgnoreCase("null")) {
            targetRegion.setFlag(matchedFlag, null);
          } else {
            targetRegion.setFlag(
              matchedFlag,
              matchedFlag.parseInput(
                FlagContext.create()
                  .setSender(worldGuardLocalPlayer)
                  .setInput(flagValue)
                  .setObject("region", targetRegion)
                  .build()));
          }

          new MessageHelper(languageResolverFunction)
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
            .modify(s -> s.replace("%flagname", fuzzyMatchedFlag.getName()))
            .modify(s -> s.replace("%zonename", zoneName))
            .modify(s -> s.replace("%oldvalue", previousFlagValue))
            .modify(s -> s.replace("%newvalue", flagValue))
            .prefixPath(CurrentEntries.LANG_PREFIX.toString())
            .showPrefix(true)
            .receiver(sender)
            .send();
        } catch (InvalidFlagFormat exception) {
          String logMessage = new StringBuilder()
            .append("Error parsing flag value '")
            .append(flagValue)
            .append("' for flag '")
            .append(matchedFlag.getName())
            .append("' of type '")
            .append(matchedFlag.getClass().getName())
            .append("'!")
            .toString();
          LOG.log(Level.SEVERE, logMessage, exception);
        }
      } else if (fuzzyMatchedFlag instanceof IntegerFlag) {
        IntegerFlag matchedFlag = (IntegerFlag) fuzzyMatchedFlag;
        Integer previousFlag = (Integer) targetRegion.getFlag(fuzzyMatchedFlag);
        String previousFlagValue
          = previousFlag != null ? previousFlag.toString() : "";

        try {
          if (flagValue.equalsIgnoreCase("null")) {
            targetRegion.setFlag(matchedFlag, null);
          } else {
            targetRegion.setFlag(
              matchedFlag,
              matchedFlag.parseInput(
                FlagContext.create()
                  .setSender(worldGuardLocalPlayer)
                  .setInput(flagValue)
                  .setObject("region", targetRegion)
                  .build()));
          }

          new MessageHelper(languageResolverFunction)
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
            .modify(s -> s.replace("%flagname", fuzzyMatchedFlag.getName()))
            .modify(s -> s.replace("%zonename", zoneName))
            .modify(s -> s.replace("%oldvalue", previousFlagValue))
            .modify(s -> s.replace("%newvalue", flagValue))
            .prefixPath(CurrentEntries.LANG_PREFIX.toString())
            .showPrefix(true)
            .receiver(sender)
            .send();
        } catch (InvalidFlagFormat exception) {
          String logMessage = new StringBuilder()
            .append("Error parsing flag value '")
            .append(flagValue)
            .append("' for flag '")
            .append(matchedFlag.getName())
            .append("' of type '")
            .append(matchedFlag.getClass().getName())
            .append("'!")
            .toString();
          LOG.log(Level.SEVERE, logMessage, exception);
        }
      } else if (fuzzyMatchedFlag instanceof DoubleFlag) {
        DoubleFlag matchedFlag = (DoubleFlag) fuzzyMatchedFlag;
        Double previousFlag = (Double) targetRegion.getFlag(fuzzyMatchedFlag);
        String previousFlagValue
          = previousFlag != null ? previousFlag.toString() : "";

        try {
          if (flagValue.equalsIgnoreCase("null")) {
            targetRegion.setFlag(matchedFlag, null);
          } else {

            targetRegion.setFlag(
              matchedFlag,
              matchedFlag.parseInput(
                FlagContext.create()
                  .setSender(worldGuardLocalPlayer)
                  .setInput(flagValue)
                  .setObject("region", targetRegion)
                  .build()));
          }

          new MessageHelper(languageResolverFunction)
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
            .modify(s -> s.replace("%flagname", fuzzyMatchedFlag.getName()))
            .modify(s -> s.replace("%zonename", zoneName))
            .modify(s -> s.replace("%oldvalue", previousFlagValue))
            .modify(s -> s.replace("%newvalue", flagValue))
            .prefixPath(CurrentEntries.LANG_PREFIX.toString())
            .showPrefix(true)
            .receiver(sender)
            .send();
        } catch (InvalidFlagFormat exception) {
          String logMessage = new StringBuilder()
            .append("Error parsing flag value '")
            .append(flagValue)
            .append("' for flag '")
            .append(matchedFlag.getName())
            .append("' of type '")
            .append(matchedFlag.getClass().getName())
            .append("'!")
            .toString();
          LOG.log(Level.SEVERE, logMessage, exception);
        }
      } else if (fuzzyMatchedFlag instanceof LocationFlag) {
        LocationFlag matchedFlag = (LocationFlag) fuzzyMatchedFlag;
        com.sk89q.worldedit.util.Location previousFlag
          = (com.sk89q.worldedit.util.Location) targetRegion.getFlag(fuzzyMatchedFlag);
        String previousFlagValue
          = previousFlag != null ? previousFlag.toString() : "";

        try {
          if (flagValue.equalsIgnoreCase("null")) {
            targetRegion.setFlag(matchedFlag, null);
          } else {
            targetRegion.setFlag(
              matchedFlag,
              matchedFlag.parseInput(
                FlagContext.create()
                  .setSender(worldGuardLocalPlayer)
                  .setInput(flagValue)
                  .setObject("region", targetRegion)
                  .build()));
          }

          new MessageHelper(languageResolverFunction)
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
            .modify(s -> s.replace("%flagname", fuzzyMatchedFlag.getName()))
            .modify(s -> s.replace("%zonename", zoneName))
            .modify(s -> s.replace("%oldvalue", previousFlagValue))
            .modify(s -> s.replace("%newvalue", flagValue))
            .prefixPath(CurrentEntries.LANG_PREFIX.toString())
            .showPrefix(true)
            .receiver(sender)
            .send();
        } catch (InvalidFlagFormat exception) {
          String logMessage = new StringBuilder()
            .append("Error parsing flag value '")
            .append(flagValue)
            .append("' for flag '")
            .append(matchedFlag.getName())
            .append("' of type '")
            .append(matchedFlag.getClass().getName())
            .append("'!")
            .toString();
          LOG.log(Level.SEVERE, logMessage, exception);
        }
      } else if (fuzzyMatchedFlag instanceof EnumFlag<?>) {
        String fuzzyMatchedFlagName
          = ((EnumFlag<?>) fuzzyMatchedFlag).getName();

        if (Flags.GAME_MODE.getName().equalsIgnoreCase(fuzzyMatchedFlagName)) {
          EnumFlag<GameMode> matchedFlag
            = (EnumFlag<GameMode>) fuzzyMatchedFlag;
          GameMode previousFlag
            = (GameMode) targetRegion.getFlag(fuzzyMatchedFlag);
          String previousFlagValue
            = previousFlag != null ? previousFlag.toString() : "";

          try {
            if (flagValue.equalsIgnoreCase("null")) {
              targetRegion.setFlag(matchedFlag, null);
            } else {
              targetRegion.setFlag(
                matchedFlag,
                matchedFlag.parseInput(
                  FlagContext.create()
                    .setSender(worldGuardLocalPlayer)
                    .setInput(flagValue)
                    .setObject("region", targetRegion)
                    .build()));
            }

            new MessageHelper(languageResolverFunction)
              .locale(LocaleHelper.resolve(player.getLocale()))
              .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
              .modify(s -> s.replace("%flagname", fuzzyMatchedFlag.getName()))
              .modify(s -> s.replace("%zonename", zoneName))
              .modify(s -> s.replace("%oldvalue", previousFlagValue))
              .modify(s -> s.replace("%newvalue", flagValue))
              .prefixPath(CurrentEntries.LANG_PREFIX.toString())
              .showPrefix(true)
              .receiver(sender)
              .send();
          } catch (InvalidFlagFormat exception) {
            String logMessage = new StringBuilder()
              .append("Error parsing flag value '")
              .append(flagValue)
              .append("' for flag '")
              .append(matchedFlag.getName())
              .append("' of type '")
              .append(matchedFlag.getClass().getName())
              .append("'!")
              .toString();
            LOG.log(Level.SEVERE, logMessage, exception);
          }
        } else if (Flags.WEATHER_LOCK.getName().equalsIgnoreCase(fuzzyMatchedFlagName)) {
          EnumFlag<WeatherType> matchedFlag = (EnumFlag<WeatherType>) fuzzyMatchedFlag;
          WeatherType previousFlag
            = (WeatherType) targetRegion.getFlag(fuzzyMatchedFlag);
          String previousFlagValue
            = previousFlag != null ? previousFlag.toString() : "";

          try {
            if (flagValue.equalsIgnoreCase("null")) {
              targetRegion.setFlag(matchedFlag, null);
            } else {
              targetRegion.setFlag(
                matchedFlag,
                matchedFlag.parseInput(
                  FlagContext.create()
                    .setSender(worldGuardLocalPlayer)
                    .setInput(flagValue)
                    .setObject("region", targetRegion)
                    .build()));
            }

            new MessageHelper(languageResolverFunction)
              .locale(LocaleHelper.resolve(player.getLocale()))
              .path(CurrentEntries.LANG_CMD_ZONE_FLAG_CHANGED.toString())
              .modify(s -> s.replace("%flagname", fuzzyMatchedFlag.getName()))
              .modify(s -> s.replace("%zonename", zoneName))
              .modify(s -> s.replace("%oldvalue", previousFlagValue))
              .modify(s -> s.replace("%newvalue", flagValue))
              .prefixPath(CurrentEntries.LANG_PREFIX.toString())
              .showPrefix(true)
              .receiver(sender)
              .send();
          } catch (InvalidFlagFormat exception) {
            String logMessage = new StringBuilder()
              .append("Error parsing flag value '")
              .append(flagValue)
              .append("' for flag '")
              .append(matchedFlag.getName())
              .append("' of type '")
              .append(matchedFlag.getClass().getName())
              .append("'!")
              .toString();
            LOG.log(Level.SEVERE, logMessage, exception);
          }
        }
      }
    });

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    List<String> result = new ArrayList<>();

    if (!(sender instanceof Player)) {
      return List.of();
    }

    Player player = (Player) sender;

    if (args.length <= 1) {
      for (ProtectedRegion region : ZoneMenuManager.getInstance().getRegions(player, false)) {
        result.add(ZoneMenuUtils.regionToZoneName(region.getId()));
      }

      if (args.length == 1 && !args[0].isEmpty()) {
        result.removeIf((s) -> !s.startsWith(args[0]));
      }

      return result;
    }

    if (args.length <= 2) {
      for (Flag<?> flag : WorldGuard.getInstance().getFlagRegistry().getAll()) {
        result.add(flag.getName());
      }

      if (args.length == 2 && !args[1].isEmpty()) {
        result.removeIf((s) -> !s.startsWith(args[1]));
      }

      return result;
    }

    return result;
  }
}
