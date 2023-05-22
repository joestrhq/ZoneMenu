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

    Player player = (Player) sender;

    if (args.length == 0) {
      LocalSession session
        = WorldEdit
          .getInstance()
          .getSessionManager()
          .get(BukkitAdapter.adapt(player));

      session.getRegionSelector(BukkitAdapter.adapt(player.getWorld())).clear();
      session.dispatchCUISelection(BukkitAdapter.adapt(player));

      // TODO: send message that selection was cleared
      return true;
    }

    if (args.length != 1) {
      return false;
    }

    String targetZoneName = args[0];

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
