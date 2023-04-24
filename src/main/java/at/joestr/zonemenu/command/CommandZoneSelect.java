/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.zonemenu.command;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.configuration.CurrentEntries;
import at.joestr.zonemenu.util.ZoneMenuManager;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;
import java.util.Locale;
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
    if (!(sender instanceof Player)) {
      new MessageHelper(LanguageConfiguration.getInstance().getResolver())
        .locale(Locale.ENGLISH)
        .path(CurrentEntries.LANG_GEN_NOT_A_PLAYER.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();
      return true;
    }

    if (args.length != 1) {
      return false;
    }

    String targetZoneName = args[0];
    Player player = (Player) sender;

    ZoneMenuManager.getInstance().futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

      ProtectedRegion protectedregion = null;

      if (t.isEmpty()) {
        new MessageHelper(LanguageConfiguration.getInstance().getResolver())
          .locale(Locale.ENGLISH)
          .path(CurrentEntries.LANG_GEN_NO_ZONE.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
        return;
      }

      for (ProtectedRegion pr : t) {
        if (pr.getId().replace("+", "#").replace("-", ".").equalsIgnoreCase(targetZoneName)) {
          protectedregion = pr;
        }
      }

      if (protectedregion == null) {
        new MessageHelper(LanguageConfiguration.getInstance().getResolver())
          .locale(Locale.ENGLISH)
          .path(CurrentEntries.LANG_GEN_NOT_EXISTING_ZONE.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
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

      new MessageHelper(LanguageConfiguration.getInstance().getResolver())
        .locale(Locale.ENGLISH)
        .path(CurrentEntries.LANG_CMD_ZONE_SELECT_SUCCESS.toString())
        .modify(s -> s.replace("%zonename", targetZoneName))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();
    });

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
