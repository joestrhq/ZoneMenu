/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.zonemenu.command;

import at.joestr.zonemenu.util.ZoneMenuManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Iterator;
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
public class CommandZoneList implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneList.class.getName());

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      // TODO: send message
    }

    if (args.length != 0) {
      return false;
    }

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

      String sregionString = "";

      Iterator<ProtectedRegion> iterator = t.iterator();

      while (iterator.hasNext()) {

        ProtectedRegion protectedRegion_ = iterator.next();

        if (iterator.hasNext()) {

          sregionString = sregionString + protectedRegion_.getId() + ", ";
        } else {

          sregionString = sregionString + protectedRegion_.getId();
        }
      }

      /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_list")).replace("{list}",
                    sregionString)));*/
      return;
    });

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
