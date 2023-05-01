//
// MIT License
//
// Copyright (c) 2017-2022 Joel Strasser <joelstrasser1@gmail.com>
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
package at.joestr.zonemenu.util;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ZoneMenuManager {

  private final Logger LOGGER = Logger.getLogger(ZoneMenuManager.class.getName());

  private static ZoneMenuManager INSTANCE;

  public ZoneMenuCreateCorner zoneMenuCreateCorner = null;
  public ZoneMenuSubcreateCorner zoneMenuSubcreateCorner = null;
  private Plugin plugin;
  public Map<Player, ZoneMenuPlayer> zoneMenuPlayers = new HashMap<>();

  public static ZoneMenuManager getInstance(Plugin plugin) throws RuntimeException {
    if (INSTANCE != null) {
      throw new RuntimeException("Class already instantiated!");
    }

    INSTANCE = new ZoneMenuManager(plugin);

    return INSTANCE;
  }

  public static ZoneMenuManager getInstance() throws RuntimeException {
    if (INSTANCE == null) {
      throw new RuntimeException("Class has not been instantiated yet!");
    }

    return INSTANCE;
  }

  private ZoneMenuManager(Plugin plugin) {
    this.plugin = plugin;
    this.zoneMenuCreateCorner = new ZoneMenuCreateCorner(plugin);
    this.zoneMenuSubcreateCorner = new ZoneMenuSubcreateCorner(plugin);
  }

  /**
   * Get the WorldEdit plugin.
   *
   * @return The {@link WorldEditPlugin}.
   */
  public WorldEditPlugin getWorldEditPlugin() {

    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

    if ((plugin == null) || !(plugin instanceof WorldEditPlugin)) {

      LOGGER.log(Level.SEVERE, "[ZoneMenu] WorldEdit not initialized. Disabling plugin.");
      Bukkit.getServer().getPluginManager().disablePlugin(this.plugin);
      return null;
    }

    return (WorldEditPlugin) plugin;
  }

  /**
   * Get the WorldGuard plugin.
   *
   * @return The {@link WorldGuardPlugin}.
   */
  public WorldGuardPlugin getWorldGuardPlugin() {

    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

    if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin))) {

      LOGGER.log(Level.SEVERE, "[ZoneMenu] WorldGuard not initialized. Disabling plugin.");
      Bukkit.getServer().getPluginManager().disablePlugin(this.plugin);

      return null;
    }

    return (WorldGuardPlugin) plugin;
  }

  /**
   * Calculates the difference beetween two given numbers.
   *
   * @param num1 First {@link Integer integer} number.
   * @param num2 Second integer number.
   * @return The difference between {@code num1} and {@code num2}.
   */
  public int difference(int num1, int num2) {

    return num1 > num2 ? num1 - num2 : num2 - num1;
  }

  /**
   * Calls {@linkplain #getRegion(Player, boolean)} with second parameter to be
   * {@code true}.
   *
   * @param player A {@link Player player}.
   * @return The {@linkplain ProtectedRegion region} of a player.
   */
  public ProtectedRegion getRegion(Player player) {

    return this.getRegion(player, true);
  }

  /**
   * Calls {@linkplain #getRegions(Player, Boolean)} with parameters and returns
   * the first element or {@code null}.
   *
   * @param player A {@link Player player}.
   * @param showMessage A {@link Boolean boolean} flag; {@code true} - if a
   * message should be displayed
   * @return The {@linkplain ProtectedRegion region} of the player.
   */
  public ProtectedRegion getRegion(final Player player, boolean showMessage) {

    List<ProtectedRegion> lp = getRegions(player, showMessage);

    if (lp.isEmpty()) {

      return null;
    } else {

      return lp.get(0);
    }
  }

  /**
   * Get a list of players regions an control whether you like to show a message
   * or not.
   *
   * @param player A {@link Player player}.
   * @param showMessage A {@link Boolean boolean} flag; {@code} - if a message
   * should be displayed
   * @return A {@link List list} of {@link ProtectedRegion region}s of a player.
   */
  public List<ProtectedRegion> getRegions(final Player player, final Boolean showMessage) {

    if (showMessage) {

      // TODO: send wait message
    }

    List<ProtectedRegion> protectedRegions = new ArrayList<>();
    RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionManager regeionManager = rc.get(BukkitAdapter.adapt(player.getWorld()));

    for (String string : regeionManager.getRegions().keySet()) {

      if (regeionManager.getRegions().get(string).isOwner(this.getWorldGuardPlugin().wrapPlayer(player))) {

        protectedRegions.add(regeionManager.getRegions().get(string));
      }
    }

    return protectedRegions;
  }

  /**
   * Clear up information (regarding this plugin) about the
   * {@link Player player}.
   *
   * @param player A {@link Player player}.
   */
  public void clearUpZoneMenuPlayer(Player player) {

    if (!this.zoneMenuPlayers.containsKey(player)) {
      return;
    }

    ZoneMenuPlayer zoneMenuPlayer = this.zoneMenuPlayers.get(player);

    LocalSession session
      = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));

    com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(player.getWorld());

    session.getRegionSelector(weWorld).clear();

    this.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner1(), player);
    this.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner2(), player);
    this.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner3(), player);
    this.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner4(), player);

    this.zoneMenuSubcreateCorner.reset(zoneMenuPlayer.getSubcreateCorner1(), player);
    this.zoneMenuSubcreateCorner.reset(zoneMenuPlayer.getSubcreateCorner2(), player);

    if (zoneMenuPlayer.getZoneFindBossbar() != null) {
      zoneMenuPlayer.getZoneFindBossbar().removePlayer(player);
    }

    this.zoneMenuPlayers.remove(player);
  }

  /**
   * Returns the WorldEdit selection of a player as a
   * {@link ProtectedRegion region}.
   *
   * @param player A {@link Player player}.
   * @return The WorldEdit selection as a region.
   */
  public Region getPlayerSelection(Player player) {

    Region selectedRegion = null;

    LocalSession session
      = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));

    com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(player.getWorld());

    try {
      selectedRegion = session.getRegionSelector(weWorld).getRegion();
    } catch (IncompleteRegionException ex) {

    }

    return selectedRegion;
  }

  /**
   * Gets all regions belonging to a player in a {@link CompletableFuture} and
   * gives it to a provided {@link BiConsumer}
   *
   * @param player A {@link Player player}.
   * @param showMessage A {@link Boolean boolean} flag; {@code true} - if a
   * message should be displayed
   * @param biConsumer A {@linkplain BiConsumer bi-consumer} which takes a
   * {@link List list} of {@link ProtectedRegion region}s and a
   * {@link Throwable throwable} object; This bi-consumer gets executed when the
   * search request finished.
   */
  public void futuristicRegionProcessing(
    final Player player,
    final boolean showMessage,
    BiConsumer<List<ProtectedRegion>, Throwable> biConsumer) {

    if (showMessage) {

      // TODO: send wait message
    }

    CompletableFuture.supplyAsync(
      () -> {
        RegionManager regionManager
        = WorldGuard.getInstance()
          .getPlatform()
          .getRegionContainer()
          .get(BukkitAdapter.adapt(player.getWorld()));

        List<ProtectedRegion> protectedRegions = new ArrayList<>();

        for (String string : regionManager.getRegions().keySet()) {

          if (regionManager.getRegion(string).isOwner(this.getWorldGuardPlugin().wrapPlayer(player))) {
            protectedRegions.add(regionManager.getRegions().get(string));
          }
        }

        return protectedRegions;
      })
      .whenComplete(biConsumer);
  }
}
