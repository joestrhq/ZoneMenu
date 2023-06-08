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

import at.joestr.javacommon.configuration.AppConfiguration;
import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.configuration.LocaleHelper;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.configuration.CurrentEntries;
import at.joestr.zonemenu.util.ZoneMenuManager;
import at.joestr.zonemenu.util.ZoneMenuMode;
import at.joestr.zonemenu.util.ZoneMenuPlayer;
import at.joestr.zonemenu.util.ZoneMenuUtils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandZoneCreate implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneCreate.class.getName());
  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      new MessageHelper(languageResolverFunction)
        .locale(Locale.ENGLISH)
        .path(CurrentEntries.LANG_GEN_NOT_A_PLAYER.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();
      return true;
    }

    if (args.length != 0) {
      return false;
    }

    Player player = (Player) sender;

    if (ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {
      ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).setToolType(ZoneMenuMode.CREATE);
    } else {
      ZoneMenuPlayer zoneMenuPlayer = new ZoneMenuPlayer(player);

      zoneMenuPlayer.setToolType(ZoneMenuMode.CREATE);

      ZoneMenuManager.getInstance().zoneMenuPlayers.put(player, zoneMenuPlayer);
    }

    Region selectedRegion_ = ZoneMenuManager.getInstance().getPlayerSelection(player);

    if (selectedRegion_ == null) {
      if (!player.getInventory().contains(Material.STICK)) {
        player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.STICK, 1)});
      }

      new MessageHelper(LanguageConfiguration.getInstance().getResolver())
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_CREATE_ACTIVATED.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();
      return true;
    }

    //if (ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() == null) {
    //  return true;
    //}
    final Region selectedRegion = selectedRegion_;

    ZoneMenuManager.getInstance().futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

      if (u != null) {
        u.printStackTrace(System.err);
        return;
      }

      if (!ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {
        return;
      }

      if (ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() != ZoneMenuMode.CREATE) {
        return;
      }

      int minLength = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_LENGTH_MIN.toString());
      int maxLength = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_LENGTH_MAX.toString());
      int minWidth = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_WIDTH_MIN.toString());
      int maxWidth = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_WIDTH_MAX.toString());

      boolean lengthOk = selectedRegion.getLength() >= minLength && selectedRegion.getLength() <= maxLength;
      boolean widthOk = selectedRegion.getWidth() >= minWidth && selectedRegion.getWidth() <= maxWidth;

      if (!lengthOk || !widthOk) {
        new MessageHelper(LanguageConfiguration.getInstance().getResolver())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_CMD_ZONE_CREATE_WIDTH_LENGTH_LIMIT.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
        return;
      }

      int zoneCounter = 1;
      int zoneArea = 0;

      for (ProtectedRegion protectedRegion_ : t) {

        if (protectedRegion_.getParent() == null) {

          zoneCounter = zoneCounter + 1;
          zoneArea = zoneArea + (protectedRegion_.volume()
            / (ZoneMenuManager.getInstance().difference(protectedRegion_.getMinimumPoint().getBlockY(),
              protectedRegion_.getMaximumPoint().getBlockY())));
        }
      }

      int selectionArea = selectedRegion.getWidth() * selectedRegion.getLength();

      if (zoneArea + selectionArea
        > AppConfiguration.getInstance().getInt(CurrentEntries.CONF_AREA_MAX_CLAIMABLE.toString())) {
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_CMD_ZONE_CREATE_AREA_OVER.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
        return;
      }

      if (zoneCounter > AppConfiguration.getInstance().getInt(CurrentEntries.CONF_HAVE_MAX.toString())) {
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_CMD_ZONE_CREATE_HAVE_OVER_EQUAL.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
        return;
      }

      if (selectionArea < AppConfiguration.getInstance().getInt(CurrentEntries.CONF_AREA_MIN.toString())) {
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_CMD_ZONE_CREATE_AREA_UNDER.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
        return;
      }

      if (selectionArea > AppConfiguration.getInstance().getInt(CurrentEntries.CONF_AREA_MAX.toString())) {
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_CMD_ZONE_CREATE_AREA_OVER.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
        return;
      }

      ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion(
        ZoneMenuUtils.createRegionNameFor(player.getName(), zoneCounter++),
        selectedRegion.getMinimumPoint(), selectedRegion.getMaximumPoint());

      LocalPlayer wrapedPlayer = ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapPlayer(player);
      RegionManager regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));

      if (regionContainer.overlapsUnownedRegion(protectedCuboidRegion, wrapedPlayer)) {
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_CMD_ZONE_CREATE_OVERLAPS_UNOWNED.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
        return;
      }

      try {
        WorldGuard.getInstance().getProfileService().findByName(player.getName());
      } catch (IOException | InterruptedException ex) {
        LOG.log(Level.SEVERE, "", ex);
      }

      // Create a new domain
      DefaultDomain domain = new DefaultDomain();

      // Wrap player and add it to the domain
      domain.addPlayer(wrapedPlayer);

      // Apply the domain to owners
      protectedCuboidRegion.setOwners(domain);

      // Set the priority to the specified value in the config file
      protectedCuboidRegion.setPriority(AppConfiguration.getInstance().getInt(CurrentEntries.CONF_PRIORITY.toString()));

      // Some flags
      /*
             * ProtectRegion.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
             * ProtectRegion.setFlag(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE,
             * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.TNT,
             * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.FIRE_SPREAD,
             * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.OTHER_EXPLOSION,
             * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.ENDER_BUILD,
             * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.GHAST_FIREBALL,
             * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.LAVA_FIRE,
             * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.PVP,
             * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.MOB_DAMAGE,
             * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.MOB_SPAWNING,
             * StateFlag.State.DENY);
       */
      // Finally, add the region to worlds region manager
      WorldGuard
        .getInstance()
        .getPlatform()
        .getRegionContainer()
        .get(BukkitAdapter.adapt(player.getWorld()))
        .addRegion(protectedCuboidRegion);

      ZoneMenuManager.getInstance().clearUpZoneMenuPlayer(player);

      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_CREATE_CREATED.toString())
        .modify((s) -> s.replace("%zonename", ZoneMenuUtils.regionToZoneName(protectedCuboidRegion.getId())))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();
    });

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
