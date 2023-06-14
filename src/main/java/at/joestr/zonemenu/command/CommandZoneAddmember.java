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
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.util.DomainInputResolver;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class CommandZoneAddmember implements TabExecutor {

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

    Player player = (Player) sender;

    final String zoneName = args[0];
    final String regionName = ZoneMenuUtils.zoneToRegionName(zoneName);
    final String playerName = args[1];

    ZoneMenuManager.getInstance()
      .futuristicRegionProcessing(
        player,
        false,
        (protectedRegions, thrownError) -> {
          if (protectedRegions.isEmpty()) {
            new MessageHelper(languageResolverFunction)
              .locale(LocaleHelper.resolve(player.getLocale()))
              .path(CurrentEntries.LANG_GEN_NO_ZONE.toString())
              .prefixPath(CurrentEntries.LANG_PREFIX.toString())
              .showPrefix(true)
              .receiver(player)
              .send();
            return;
          }

          ProtectedRegion targetRegion = null;

          for (ProtectedRegion region : protectedRegions) {
            if (region.getId().equalsIgnoreCase(regionName)) {
              targetRegion = region;
            }
          }

          if (targetRegion == null) {
            new MessageHelper(languageResolverFunction)
              .locale(LocaleHelper.resolve(player.getLocale()))
              .path(CurrentEntries.LANG_GEN_NOT_EXISTING_ZONE.toString())
              .modify((msg) -> msg.replace("%zonename", zoneName))
              .prefixPath(CurrentEntries.LANG_PREFIX.toString())
              .showPrefix(true)
              .receiver(player)
              .send();
            return;
          }

          DefaultDomain targetRegionMembers = targetRegion.getMembers();

          LocalPlayer wrapedPlayer
          = ZoneMenuManager.getInstance()
            .getWorldGuardPlugin()
            .wrapOfflinePlayer(Bukkit.getServer().getOfflinePlayer(playerName));

          if (targetRegionMembers.contains(wrapedPlayer)) {
            new MessageHelper(languageResolverFunction)
              .locale(LocaleHelper.resolve(player.getLocale()))
              .path(CurrentEntries.LANG_CMD_ZONE_ADDMEMBER_ALREADY_A_MEMBER.toString())
              .modify((msg) -> msg.replace("%playername", playerName))
              .prefixPath(CurrentEntries.LANG_PREFIX.toString())
              .showPrefix(true)
              .receiver(player)
              .send();
            return;
          }

          ListeningExecutorService listeningExecutorService
          = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

          DomainInputResolver domainInputResolver
          = new DomainInputResolver(
            WorldGuard.getInstance().getProfileService(), new String[]{playerName});
          domainInputResolver.setLocatorPolicy(
            DomainInputResolver.UserLocatorPolicy.UUID_AND_NAME);
          ListenableFuture<DefaultDomain> listenableFuture
          = listeningExecutorService.submit(domainInputResolver);

          Futures.addCallback(
            listenableFuture,
            new FutureCallback<DefaultDomain>() {
            @Override
            public void onSuccess(DefaultDomain result) {
              new MessageHelper(languageResolverFunction)
                .locale(LocaleHelper.resolve(player.getLocale()))
                .path(CurrentEntries.LANG_CMD_ZONE_ADDMEMBER_SUCCESS.toString())
                .modify((msg) -> msg.replace("%playername", playerName))
                .prefixPath(CurrentEntries.LANG_PREFIX.toString())
                .showPrefix(true)
                .receiver(player)
                .send();
            }

            @Override
            public void onFailure(Throwable throwable) {
              new MessageHelper(languageResolverFunction)
                .locale(LocaleHelper.resolve(player.getLocale()))
                .path(
                  CurrentEntries.LANG_CMD_ZONE_ADDMEMBER_PLAYER_DOES_NOT_EXIST
                    .toString())
                .modify((msg) -> msg.replace("%playername", playerName))
                .prefixPath(CurrentEntries.LANG_PREFIX.toString())
                .showPrefix(true)
                .receiver(player)
                .send();
            }
          }, null);
        }
      );

    return true;
  }

  @Override
  public List<String> onTabComplete(
    CommandSender sender, Command command, String label, String[] args) {

    List<String> result = new ArrayList<>();

    if (!(sender instanceof Player)) {
      return List.of();
    }

    Player player = (Player) sender;

    if (args.length <= 1) {
      for (ProtectedRegion region : ZoneMenuManager.getInstance().getRegions(player, false)) {
        result.add(ZoneMenuUtils.regionToZoneName(region.getId()));
      }

      if (args.length == 1) {
        result.removeIf((s) -> s.startsWith(args[0]));
      }

      return result;
    }

    if (args.length <= 2) {
      result.addAll(
        Bukkit.getServer().getOnlinePlayers().stream()
          .map(Player::getName)
          .collect(Collectors.toList()));

      if (args.length == 2) {
        result.removeIf((s) -> s.startsWith(args[1]));
      }

      return result;
    }

    return result;
  }
}
