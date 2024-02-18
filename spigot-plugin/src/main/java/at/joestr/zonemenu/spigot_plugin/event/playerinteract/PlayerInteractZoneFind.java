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
package at.joestr.zonemenu.spigot_plugin.event.playerinteract;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.configuration.LocaleHelper;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.spigot_plugin.configuration.CurrentEntries;
import at.joestr.zonemenu.spigot_plugin.util.ZoneMenuManager;
import at.joestr.zonemenu.spigot_plugin.util.ZoneMenuMode;
import at.joestr.zonemenu.spigot_plugin.util.ZoneMenuPlayer;
import at.joestr.zonemenu.spigot_plugin.util.ZoneMenuUtils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractZoneFind implements Listener {

  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  public PlayerInteractZoneFind() {
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {

    Player player = event.getPlayer();

    if (!ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {
      return;
    }

    ZoneMenuPlayer zoneMenuPlayer = ZoneMenuManager.getInstance().zoneMenuPlayers.get(player);

    boolean hasStickInMainHand = player.getInventory().getItemInMainHand().getType() == Material.STICK;
    boolean isToolTypeFind = ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() == ZoneMenuMode.FIND;

    if (!(hasStickInMainHand && isToolTypeFind)) {
      return;
    }

    boolean isLeftClickBlock = event.getAction() == Action.LEFT_CLICK_BLOCK;
    boolean isRightClickBlock = event.getAction() == Action.RIGHT_CLICK_BLOCK;

    if (isLeftClickBlock || isRightClickBlock) {
      if (event.getClickedBlock().getLocation().equals(zoneMenuPlayer.getFindLocation())) {
        event.setCancelled(true);
        return;
      }

      zoneMenuPlayer.setFindLocation(event.getClickedBlock().getLocation());
      event.setCancelled(true);

      RegionManager worldRegionManager
        = WorldGuard.getInstance().getPlatform().getRegionContainer()
          .get(BukkitAdapter.adapt(player.getWorld()));

      ApplicableRegionSet regiononloc
        = worldRegionManager.getApplicableRegions(
          BlockVector3.at(
            event.getClickedBlock().getLocation().getBlockX(),
            event.getClickedBlock().getLocation().getBlockY(),
            event.getClickedBlock().getLocation().getBlockZ()
          )
        );

      List<String> foundRegions = new ArrayList<>();
      for (ProtectedRegion region : regiononloc) {
        foundRegions.add(ZoneMenuUtils.regionToZoneName(region.getId()));
      }

      if (foundRegions.isEmpty()) {
        new MessageHelper(languageResolverFunction)
          .path(CurrentEntries.LANG_EVT_FIND_FOUND_NONE.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
      } else {
        String foundRegionsString = foundRegions.stream().collect(
          Collectors.joining(", ")
        );
        new MessageHelper(languageResolverFunction)
          .path(CurrentEntries.LANG_EVT_FIND_FOUND.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .modify((s) -> s.replace("%zonenameslist", foundRegionsString))
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
      }
    }
  }
}
