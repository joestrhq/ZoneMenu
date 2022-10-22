/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.zonemenu.listener;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.ZoneMenu;
import at.joestr.zonemenu.configuration.CurrentEntries;
import at.joestr.zonemenu.util.ZoneMenuToolType;
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
import org.bukkit.boss.BarColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Joel
 */
public class PlayerMove implements Listener {

  private ZoneMenu zoneMenuPlugin;
  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  public PlayerMove(ZoneMenu zonemenu) {

    this.zoneMenuPlugin = zonemenu;
    this.zoneMenuPlugin.getServer().getPluginManager().registerEvents(this, this.zoneMenuPlugin);
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {

    if (!this.zoneMenuPlugin.zoneMenuPlayers.containsKey(event.getPlayer())) {
      return;
    }

    if (!this.zoneMenuPlugin.zoneMenuPlayers.get(event.getPlayer()).getToolType().equals(ZoneMenuToolType.FIND)) {
      return;
    }

    CompletableFuture.supplyAsync(() -> {
      RegionQuery regionQuery
        = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();

      ApplicableRegionSet applicableRegions = regionQuery.getApplicableRegions(BukkitAdapter.adapt(event.getTo()));

      List<String> regionNames = new ArrayList<>();

      applicableRegions.forEach((region) -> regionNames.add(region.getId().replace("+", "#").replace("-", ".")));

      String regions = regionNames.stream().collect(Collectors.joining(", "));

      if (regions.isEmpty()) {
        String text = new MessageHelper(languageResolverFunction).path(CurrentEntries.LANG_EVT_FIND_NONE.toString()).rawString();
        this.zoneMenuPlugin.zoneMenuPlayers.get(event.getPlayer()).getZoneFindBossbar().setColor(BarColor.GREEN);
        this.zoneMenuPlugin.zoneMenuPlayers.get(event.getPlayer()).getZoneFindBossbar().setTitle(this.zoneMenuPlugin.colorCode('&', text));
      } else {
        String text = new MessageHelper(languageResolverFunction).path(CurrentEntries.LANG_EVT_FIND_FOUND.toString()).rawString();
        this.zoneMenuPlugin.zoneMenuPlayers.get(event.getPlayer()).getZoneFindBossbar().setColor(BarColor.RED);
        this.zoneMenuPlugin.zoneMenuPlayers.get(event.getPlayer()).getZoneFindBossbar().setTitle(this.zoneMenuPlugin.colorCode('&', text).replace("%zonenameslist", regions));
      }

      return true;
    });
  }
}
