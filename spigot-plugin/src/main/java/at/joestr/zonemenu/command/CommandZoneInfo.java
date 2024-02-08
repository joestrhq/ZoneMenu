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
import at.joestr.zonemenu.configuration.CurrentEntries;
import at.joestr.zonemenu.util.ZoneMenuManager;
import at.joestr.zonemenu.util.ZoneMenuUtils;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class CommandZoneInfo implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneInfo.class.getName());
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

    Player player = (Player) sender;

    if (args.length != 1) {
      return false;
    }

    String zoneName = args[0];
    String regionName = ZoneMenuUtils.zoneToRegionName(zoneName);

    ZoneMenuManager.getInstance().futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {
      if (t.isEmpty()) {
        new MessageHelper(languageResolverFunction)
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
        if (protectedRegion_.getId().equalsIgnoreCase(regionName)) {
          protectedRegions.add(protectedRegion_);
          break;
        }
      }

      if (protectedRegions.isEmpty()) {
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_GEN_NOT_EXISTING_ZONE.toString())
          .modify(message -> message.replace("%zonename", zoneName))
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
        return;
      }

      ProtectedRegion protectedRegion = protectedRegions.get(0);

      // Grab some infos
      DefaultDomain domainOwners = protectedRegion.getOwners();
      DefaultDomain domainMembers = protectedRegion.getMembers();
      int minimumX = protectedRegion.getMinimumPoint().getBlockX();
      int minimumY = protectedRegion.getMinimumPoint().getBlockY();
      int minimumZ = protectedRegion.getMinimumPoint().getBlockZ();
      int maximumX = protectedRegion.getMaximumPoint().getBlockX();
      int maximumY = protectedRegion.getMaximumPoint().getBlockY();
      int maximumZ = protectedRegion.getMaximumPoint().getBlockZ();
      int area = (ZoneMenuManager.getInstance().difference(minimumX, maximumX) + 1)
        * (ZoneMenuManager.getInstance().difference(minimumZ, maximumZ) + 1);
      int volume = protectedRegion.volume();

      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_INFO_ID.toString())
        .modify(s -> s.replace("%zonename", zoneName))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();

      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_INFO_PRIORITY.toString())
        .modify(s -> s.replace("%zonepriority", Integer.toString(protectedRegion.getPriority())))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();

      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_INFO_PARENT.toString())
        .modify(s -> s.replace("%zoneparent", protectedRegion.getParent() == null ? "" : protectedRegion.getParent().getId()))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();

      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_INFO_OWNERS.toString())
        .modify(s -> s.replace("%zoneownerslist", domainOwners.toPlayersString().replace("*", "")))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();

      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_INFO_MEMBERS.toString())
        .modify(s -> s.replace("%zonememberslist", domainMembers.toPlayersString().replace("*", "")))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();

      for (Map.Entry<Flag<?>, Object> entry_ : protectedRegion.getFlags().entrySet()) {
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_CMD_ZONE_INFO_FLAG.toString())
          .modify(s -> s.replace("%flagname", entry_.getKey().toString()))
          .modify(s -> s.replace("%flagvalue", entry_.getValue().toString()))
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
      }

      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_INFO_START.toString())
        .modify(s -> s.replace("%pos_x", Integer.toString(minimumX)))
        .modify(s -> s.replace("%pos_y", Integer.toString(minimumY)))
        .modify(s -> s.replace("%pos_z", Integer.toString(minimumZ)))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();

      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_INFO_END.toString())
        .modify(s -> s.replace("%pos_x", Integer.toString(maximumX)))
        .modify(s -> s.replace("%pos_y", Integer.toString(maximumY)))
        .modify(s -> s.replace("%pos_z", Integer.toString(maximumZ)))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();

      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_INFO_AREA.toString())
        .modify(s -> s.replace("%area", Integer.toString(area)))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_INFO_VOLUME.toString())
        .modify(s -> s.replace("%volume", Integer.toString(volume)))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();
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

    return result;
  }
}
