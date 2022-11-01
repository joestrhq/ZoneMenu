/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.zonemenu.command;

import at.joestr.zonemenu.util.ZoneMenuManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 *
 * @author Joel
 */
public class CommandZoneDelete implements TabExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      // TODO: send message
    }

    if (args.length != 1) {
      return false;
    }

    String zoneName = args[0];
    Player player = (Player) sender;

    if (!ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {

      // TODO: send message
      return true;
    }

    ZoneMenuManager.getInstance().futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

      // Initialise region
      ProtectedRegion protectedRegion = null;

      // If the list is empty ...
      if (t.isEmpty()) {

        // ... send the player a message.
        //player.sendMessage(this.zoneMenuPlugin.colorCode('&', (String) this.zoneMenuPlugin.configDelegate.getMap().get("no_zone")));
        return;
      }

      // Loop through all regions ...
      for (ProtectedRegion protectedRegion_ : t) {

        // ... and if the region ID equals the second argument (<Zone>) ...
        if (protectedRegion_.getId().equalsIgnoreCase(zoneName)) {

          // ... set the found region.
          protectedRegion = protectedRegion_;
        }
      }

      // If region equals null ...
      if (protectedRegion == null) {

        // ... no region with this ID was not found.
        // Send the player a message.
        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
          ((String) this.zoneMenuPlugin.configDelegate.getMap().get("not_exisiting_zone")).replace("{0}",
            arguments[1])));*/
        return;
      }

      // Remove the region from worlds region manager
      WorldGuard
        .getInstance()
        .getPlatform()
        .getRegionContainer()
        .get(BukkitAdapter.adapt(player.getWorld()))
        .removeRegion(
          protectedRegion.getId(),
          RemovalStrategy.REMOVE_CHILDREN
        );

      // Send a message to the player
      /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
        ((String) zoneMenuPlugin.configDelegate.getMap().get("zone_delete")).replace("{0}", arguments[1])));*/
    });

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
