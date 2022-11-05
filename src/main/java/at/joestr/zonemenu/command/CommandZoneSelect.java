/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.zonemenu.command;

import at.joestr.zonemenu.util.ZoneMenuManager;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 *
 * @author Joel
 */
public class CommandZoneSelect implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneSelect.class.getName());

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

      // Initialise new region
      ProtectedRegion protectedregion = null;

      if (t.isEmpty()) {

        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
          (String) this.zoneMenuPlugin.configDelegate.getMap().get("no_zone")));*/
        return;
      }

      for (ProtectedRegion pr : t) {

        if (pr.getId().replace("+", "#").replace("-", ".").equalsIgnoreCase(args[1])) {

          protectedregion = pr;
        }
      }

      // Check if region in invalid
      if (protectedregion == null) {

        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
          ((String) this.zoneMenuPlugin.configDelegate.getMap().get("not_exisiting_zone")).replace("{0}",
            args[1])));*/
        return;
      }

      Location minLoc = new Location(player.getWorld(), protectedregion.getMinimumPoint().getBlockX(),
        protectedregion.getMinimumPoint().getBlockY(), protectedregion.getMinimumPoint().getBlockZ());

      Location maxLoc = new Location(player.getWorld(), protectedregion.getMaximumPoint().getBlockX(),
        protectedregion.getMaximumPoint().getBlockY(), protectedregion.getMaximumPoint().getBlockZ());

      LocalSession session
        = WorldEdit
          .getInstance()
          .getSessionManager()
          .get(BukkitAdapter.adapt(player));

      com.sk89q.worldedit.world.World weWorld
        = BukkitAdapter.adapt(player.getWorld());

      session.setRegionSelector(
        weWorld,
        new CuboidRegionSelector(
          weWorld,
          BukkitAdapter.asBlockVector(minLoc),
          BukkitAdapter.asBlockVector(maxLoc)
        )
      );

      session.dispatchCUISelection(BukkitAdapter.adapt(player));

      /*player.sendMessage(this.zoneMenuPlugin.colorCode(
        '&',
        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_select")).replace("{0}", args[1]))
      );*/
      return;
    });

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
