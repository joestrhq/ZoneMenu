/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.zonemenu.command;

import at.joestr.zonemenu.util.ZoneMenuManager;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 *
 * @author Joel
 */
public class CommandZoneRemovemember implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneRemovemember.class.getName());

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      // TODO: send message
    }

    if (args.length != 2) {
      return false;
    }

    String zoneName = args[1];
    Player player = (Player) sender;

    if (!ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {

      // TODO: send message
      return true;
    }

    ZoneMenuManager.getInstance().futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

      // Initialise new region
      ProtectedRegion protectedregion = null;

      if (t.isEmpty()) {

        /*player.sendMessage(
                    this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("no_zone")));*/
        return;
      }

      for (ProtectedRegion pr : t) {

        if (pr.getId().equalsIgnoreCase(zoneName)) {

          protectedregion = pr;
        }
      }

      // Check if region in invalid
      if (protectedregion == null) {

        /*player.sendMessage(this.plugin.colorCode('&',
                    ((String) this.plugin.configDelegate.getMap().get("not_exisiting_zone")).replace("{0}",
                        args[1])));*/
        return;
      }

      // Grab the members
      DefaultDomain domainmembers = protectedregion.getMembers();

      // Check if members does not contain the specified player
      if (!domainmembers.contains(
        ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapOfflinePlayer(Bukkit.getServer().getOfflinePlayer(args[2])))) {

        /*player.sendMessage(plugin
                    .colorCode('&', (String) plugin.configDelegate.getMap().get("zone_removemember_unknownplayer"))
                    .replace("{0}", args[2]).replace("{1}", args[1]));*/
        return;
      }

      // Remove specified player from the members
      domainmembers.removePlayer(
        ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapOfflinePlayer(Bukkit.getServer().getOfflinePlayer(args[2])));

      // Set the new members
      protectedregion.setMembers(domainmembers);

      /*player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_removemember"))
                .replace("{0}", args[2]).replace("{1}", args[1]));*/
    });

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
