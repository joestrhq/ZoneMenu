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
package at.joestr.zonemenu.listener;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.configuration.CurrentEntries;
import at.joestr.zonemenu.util.ZoneMenuManager;
import at.joestr.zonemenu.util.ZoneMenuMode;
import at.joestr.zonemenu.util.ZoneMenuUtils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  public PlayerMove() {
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {

    if (!ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(event.getPlayer())) {
      return;
    }

    if (!ZoneMenuManager.getInstance().zoneMenuPlayers.get(event.getPlayer()).getToolType().equals(ZoneMenuMode.FIND)) {
      return;
    }

    CompletableFuture.supplyAsync(() -> {
      RegionQuery regionQuery
        = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();

      ApplicableRegionSet applicableRegions = regionQuery.getApplicableRegions(BukkitAdapter.adapt(event.getTo()));
      List<String> regionNames = new ArrayList<>();
      applicableRegions.forEach((region) -> ZoneMenuUtils.regionToZoneName(region.getId()));
      String regions = regionNames.stream().collect(Collectors.joining(", "));

      if (regions.isEmpty()) {
        String text = new MessageHelper(languageResolverFunction).path(CurrentEntries.LANG_EVT_FIND_ACTIONBAR_FOUND_NONE.toString()).rawString();
        ZoneMenuManager.getInstance().zoneMenuPlayers.get(event.getPlayer()).getZoneFindBossbar().setColor(BarColor.GREEN);
        ZoneMenuManager.getInstance().zoneMenuPlayers.get(event.getPlayer()).getZoneFindBossbar().setTitle(ChatColor.translateAlternateColorCodes('&', text));
      } else {
        String text = new MessageHelper(languageResolverFunction).path(CurrentEntries.LANG_EVT_FIND_ACTIONBAR_FOUND.toString()).rawString();
        ZoneMenuManager.getInstance().zoneMenuPlayers.get(event.getPlayer()).getZoneFindBossbar().setColor(BarColor.RED);
        ZoneMenuManager.getInstance().zoneMenuPlayers.get(event.getPlayer()).getZoneFindBossbar().setTitle(ChatColor.translateAlternateColorCodes('&', text).replace("%zonenameslist", regions));
      }

      return true;
    });
  }
}
