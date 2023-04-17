/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.zonemenu.command;

import at.joestr.javacommon.configuration.AppConfiguration;
import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.configuration.CurrentEntries;
import at.joestr.zonemenu.util.ZoneMenuManager;
import at.joestr.zonemenu.util.ZoneMenuPlayer;
import at.joestr.zonemenu.util.ZoneMenuSignType;
import at.joestr.zonemenu.util.ZoneMenuToolType;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Joel
 */
public class CommandZoneSubcreate implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneSubcreate.class.getName());

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

    String zoneName = args[0];
    Player player = (Player) sender;

    ZoneMenuManager.getInstance().futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {
      Region selectedRegion = ZoneMenuManager.getInstance().getPlayerSelection(player);

      if (selectedRegion == null) {
        if (!player.getInventory().contains(Material.STICK)) {
          player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.STICK, 1)});
        }

        if (ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {
          ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).setToolType(ZoneMenuToolType.SIGN);
          ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).setSignType(ZoneMenuSignType.SUBZONE);
        } else {
          ZoneMenuPlayer zoneMenuPlayer = new ZoneMenuPlayer(player);

          zoneMenuPlayer.setToolType(ZoneMenuToolType.SIGN);
          zoneMenuPlayer.setSignType(ZoneMenuSignType.SUBZONE);

          ZoneMenuManager.getInstance().zoneMenuPlayers.put(player, zoneMenuPlayer);
        }

        new MessageHelper(LanguageConfiguration.getInstance().getResolver())
          .locale(Locale.ENGLISH)
          .path(CurrentEntries.LANG_CMD_ZONE_SUBCREATE_SIGN.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
        return;
      }

      if (!ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {
        return;
      }

      if (ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.SIGN
        || ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getSignType() != ZoneMenuSignType.SUBZONE) {
        return;
      }

      ProtectedRegion protectedRegion = null;

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
        if (pr.getId().equalsIgnoreCase(zoneName)) {
          protectedRegion = pr;
        }
      }

      int subZoneCounter = 1;

      for (ProtectedRegion pr : t) {
        if (pr.getParent() != null) {
          if (pr.getParent().equals(protectedRegion)) {
            subZoneCounter++;
          }
        }
      }

      if (protectedRegion == null) {
        new MessageHelper(LanguageConfiguration.getInstance().getResolver())
          .locale(Locale.ENGLISH)
          .path(CurrentEntries.LANG_GEN_NOT_EXISTING_ZONE.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
        return;
      }

      int minBlockX = selectedRegion.getMinimumPoint().getBlockX();
      int minBlockY = selectedRegion.getMinimumPoint().getBlockY();
      int minBlockZ = selectedRegion.getMinimumPoint().getBlockZ();
      int maxBlockX = selectedRegion.getMaximumPoint().getBlockX();
      int maxBlockY = selectedRegion.getMaximumPoint().getBlockY();
      int maxBlockZ = selectedRegion.getMaximumPoint().getBlockZ();

      if (!protectedRegion.contains(minBlockX, minBlockY, minBlockZ)) {
        new MessageHelper(LanguageConfiguration.getInstance().getResolver())
          .locale(Locale.ENGLISH)
          .path(CurrentEntries.LANG_CMD_ZONE_SUBCREATE_NOT_IN_ZONE.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
        return;
      }

      if (!protectedRegion.contains(maxBlockX, maxBlockY, maxBlockZ)) {
        new MessageHelper(LanguageConfiguration.getInstance().getResolver())
          .locale(Locale.ENGLISH)
          .path(CurrentEntries.LANG_CMD_ZONE_SUBCREATE_NOT_IN_ZONE.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
        return;
      }

      ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion(
        (AppConfiguration.getInstance().getString(CurrentEntries.CONF_SUBZONE_ID.toString()))
          .replace("{parent}", protectedRegion.getId()).replace("{count}", "" + subZoneCounter++),
        selectedRegion.getMinimumPoint(), selectedRegion.getMaximumPoint());
      try {
        protectedCuboidRegion.setParent(protectedRegion);
      } catch (ProtectedRegion.CircularInheritanceException e) {
        e.printStackTrace();

        new MessageHelper(LanguageConfiguration.getInstance().getResolver())
          .locale(Locale.ENGLISH)
          .path(CurrentEntries.LANG_CMD_ZONE_SUBCREATE_CIRCULAR.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(sender)
          .send();
        return;
      }

      try {
        WorldGuard.getInstance().getProfileService().findByName(player.getName());
      } catch (IOException ex) {
        Logger.getLogger(CommandZoneSubcreate.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InterruptedException ex) {
        Logger.getLogger(CommandZoneSubcreate.class.getName()).log(Level.SEVERE, null, ex);
      }

      DefaultDomain domain = new DefaultDomain();
      domain.addPlayer(ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapPlayer(player));

      protectedCuboidRegion.setOwners(domain);
      protectedCuboidRegion.setPriority(protectedRegion.getPriority() + 1);
      WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld())).addRegion(protectedCuboidRegion);
      ZoneMenuManager.getInstance().clearUpZoneMenuPlayer(player);

      new MessageHelper(LanguageConfiguration.getInstance().getResolver())
        .locale(Locale.ENGLISH)
        .path(CurrentEntries.LANG_CMD_ZONE_SUBCREATE_CREATED.toString())
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
