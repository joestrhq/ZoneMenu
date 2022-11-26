/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.zonemenu.command;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.configuration.LocaleHelper;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.configuration.CurrentEntries;
import at.joestr.zonemenu.util.ZoneMenuManager;
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
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 * @author Joel
 */
public class CommandZoneAddmember implements TabExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      new MessageHelper(LanguageConfiguration.getInstance().getResolver())
        .locale(Locale.ENGLISH)
        .path(CurrentEntries.LANG_GEN_NOT_A_PLAYER.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();
    }

    if (args.length != 2) {
      return false;
    }

    Player player = (Player) sender;

    final String zoneName = args[0];
    final String playerName = args[1];

    ZoneMenuManager.getInstance()
      .futuristicRegionProcessing(
        player,
        false,
        (protectedRegions, thrownError) -> {
          if (protectedRegions.isEmpty()) {
            new MessageHelper(LanguageConfiguration.getInstance().getResolver())
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
            if (region.getId().equalsIgnoreCase(zoneName)) {
              targetRegion = region;
            }
          }

          if (targetRegion == null) {
            new MessageHelper(LanguageConfiguration.getInstance().getResolver())
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
            new MessageHelper(LanguageConfiguration.getInstance().getResolver())
              .locale(LocaleHelper.resolve(player.getLocale()))
              .path(CurrentEntries.LANG_CMD_ZONE_ADDMEMBER_ALREADY_MEMBER.toString())
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
              new MessageHelper(LanguageConfiguration.getInstance().getResolver())
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
              new MessageHelper(LanguageConfiguration.getInstance().getResolver())
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

    if (!player.hasPermission(CurrentEntries.PERM_CMD_ZONE_ADDMEMBER.toString())) {
      return List.of();
    }

    if (args.length <= 1) {
      for (ProtectedRegion pr : ZoneMenuManager.getInstance().getRegions(player, false)) {
        result.add(pr.getId().replace("+", "#").replace("-", "."));
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

      if (args.length == 1) {
        result.removeIf((s) -> s.startsWith(args[1]));
      }

      return result;
    }

    return result;
  }
}
