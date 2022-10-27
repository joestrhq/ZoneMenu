/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.zonemenu.command;

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
import java.util.List;
import java.util.concurrent.Executors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 *
 * @author Joel
 */
public class CommandZoneAddmember implements TabExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      // TODO: send message
    }

    if (args.length != 2) {
      return false;
    }

    Player player = (Player) sender;

    final String zoneName = args[0];
    final String playerName = args[1];

    ZoneMenuManager.getInstance().futuristicRegionProcessing(player, false, (t, u) -> {
      if (t.isEmpty()) {
        // TODO: send message
      }

      ProtectedRegion targetRegion = null;

      for (ProtectedRegion region : t) {
        if (region.getId().equalsIgnoreCase(zoneName)) {
          targetRegion = region;
        }
      }

      if (targetRegion == null) {
        // TODO: send message
      }

      DefaultDomain regionMembers = targetRegion.getMembers();

      LocalPlayer wrapedPlayer
        = ZoneMenuManager.getInstance().getWorldGuardPlugin()
          .wrapOfflinePlayer(Bukkit.getServer().getOfflinePlayer(playerName));

      if (regionMembers.contains(wrapedPlayer)) {
        // TODO: send message
        return;
      }

      ListeningExecutorService listeningExecutorService = MoreExecutors
        .listeningDecorator(Executors.newCachedThreadPool());

      DomainInputResolver domainInputResolver
        = new DomainInputResolver(WorldGuard.getInstance().getProfileService(), new String[]{playerName});
      domainInputResolver.setLocatorPolicy(DomainInputResolver.UserLocatorPolicy.UUID_AND_NAME);
      ListenableFuture<DefaultDomain> listenableFuture = listeningExecutorService.submit(domainInputResolver);

      Futures.addCallback(listenableFuture, new FutureCallback<DefaultDomain>() {
        @Override
        public void onSuccess(DefaultDomain result) {
          // TODO: send message
        }

        @Override
        public void onFailure(Throwable throwable) {
          // TODO: send message
        }
      }, null);
    });

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
