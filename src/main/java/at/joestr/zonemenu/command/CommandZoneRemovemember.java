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
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class CommandZoneRemovemember implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneRemovemember.class.getName());
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

    if (args.length != 2) {
      return false;
    }

    String zoneName = args[0];
    String regionName = ZoneMenuUtils.zoneToRegionName(zoneName);
    String targetPlayerName = args[1];
    Player player = (Player) sender;

    ZoneMenuManager.getInstance().futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {
      ProtectedRegion protectedregion = null;

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

      for (ProtectedRegion pr : t) {
        if (pr.getId().equalsIgnoreCase(regionName)) {
          protectedregion = pr;
        }
      }

      if (protectedregion == null) {
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_GEN_NOT_EXISTING_ZONE.toString())
          .modify(s -> s.replace("%zonename", zoneName))
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
        return;
      }

      DefaultDomain domainMembers = protectedregion.getMembers();

      if (!domainMembers.contains(
        ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapOfflinePlayer(Bukkit.getServer().getOfflinePlayer(targetPlayerName)))) {

        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_CMD_ZONE_REMOVEMEMBER_NOT_A_MEMBER.toString())
          .modify(s -> s.replace("%playername", targetPlayerName))
          .modify(s -> s.replace("%zonename", zoneName))
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
        return;
      }

      domainMembers.removePlayer(
        ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapOfflinePlayer(Bukkit.getServer().getOfflinePlayer(targetPlayerName)));
      protectedregion.setMembers(domainMembers);

      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_REMOVEMEMBER_SUCCESS.toString())
        .modify(s -> s.replace("%playername", targetPlayerName))
        .modify(s -> s.replace("%zonename", zoneName))
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
