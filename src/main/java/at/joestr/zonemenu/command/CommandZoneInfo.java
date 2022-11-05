/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.zonemenu.command;

import at.joestr.zonemenu.util.ZoneMenuManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 *
 * @author Joel
 */
public class CommandZoneInfo implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneInfo.class.getName());

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

      // If the list is empty ...
      if (t.isEmpty()) {

        // ... send the player a message.
        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("no_zone"))));*/
        return;
      }

      // Initialise new region with null
      ProtectedRegion protectedRegion = null;

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

      /*// Grab some infos
            DefaultDomain domainOwners = protectedRegion.getOwners();
            DefaultDomain domainMembers = protectedRegion.getMembers();
            int minimumX = protectedRegion.getMinimumPoint().getBlockX();
            int minimumZ = protectedRegion.getMinimumPoint().getBlockZ();
            int maximumX = protectedRegion.getMaximumPoint().getBlockX();
            int maximumZ = protectedRegion.getMaximumPoint().getBlockZ();
            int area = (this.zoneMenuPlugin.difference(minimumX, maximumX) + 1)
                * (this.zoneMenuPlugin.difference(minimumZ, maximumZ) + 1);

            // Send infos to the player.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_id")).replace("{id}",
                    protectedRegion.getId())));

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_priority")
                + protectedRegion.getPriority()));

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_parent")
                + (protectedRegion.getParent() == null ? "" : protectedRegion.getParent().getId())));

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_owners")
                + domainOwners
                    .toPlayersString(WorldGuard.getInstance().getProfileCache()).replace("*", "")));

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_members")
                + domainMembers
                    .toPlayersString(WorldGuard.getInstance().getProfileCache()).replace("*", "")));

            Iterator<Map.Entry<Flag<?>, Object>> iterator = protectedRegion.getFlags().entrySet().iterator();

            while (iterator.hasNext()) {

                Map.Entry<Flag<?>, Object> entry_ = iterator.next();

                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_flag"))
                        .replace("{0}", entry_.getKey().getName())
                        .replace("{1}", entry_.getValue().toString())));
            }

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_start"))
                    .replace("{0}", Integer.toString(minimumX)).replace("{1}", Integer.toString(minimumZ))));

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_end"))
                    .replace("{0}", Integer.toString(maximumX)).replace("{1}", Integer.toString(maximumZ))));

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_area")).replace("{0}",
                    Integer.toString(area))));*/
    });

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
