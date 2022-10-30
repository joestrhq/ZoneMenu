/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.zonemenu.command;

import at.joestr.javacommon.configuration.AppConfiguration;
import at.joestr.zonemenu.configuration.CurrentEntries;
import at.joestr.zonemenu.util.ZoneMenuManager;
import at.joestr.zonemenu.util.ZoneMenuPlayer;
import at.joestr.zonemenu.util.ZoneMenuSignType;
import at.joestr.zonemenu.util.ZoneMenuToolType;
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
public class CommandZoneCreate implements TabExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      // TODO: send message
    }

    if (args.length != 0) {
      return false;
    }

    Player player = (Player) sender;

    if (ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {
      ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).setToolType(ZoneMenuToolType.SIGN);
      ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).setSignType(ZoneMenuSignType.ZONE);
    } else {
      ZoneMenuPlayer zoneMenuPlayer = new ZoneMenuPlayer(player);

      zoneMenuPlayer.setToolType(ZoneMenuToolType.SIGN);
      zoneMenuPlayer.setSignType(ZoneMenuSignType.ZONE);

      ZoneMenuManager.getInstance().zoneMenuPlayers.put(player, zoneMenuPlayer);
    }

    Region selectedRegion_ = ZoneMenuManager.getInstance().getPlayerSelection(player);

    if (selectedRegion_ == null) {
      if (!player.getInventory().contains(Material.STICK)) {
        player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.STICK, 1)});
      }

      // TODO: send message
      return true;
    }

    if (ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() == null) {
      // TODO: send message
      return true;
    }

    final Region selectedRegion = selectedRegion_;

    ZoneMenuManager.getInstance().futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

      if (u != null) {
        u.printStackTrace(System.err);
        return;
      }

      if (!ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {

        //do not proceed
        return;
      }

      if (ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.SIGN
        || ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getSignType() != ZoneMenuSignType.ZONE) {

        //do not proceed.
        return;
      }

      int minLength = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_LENGTH_MIN.toString());
      int maxLength = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_LENGTH_MAX.toString());
      int minWidth = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_WIDTH_MIN.toString());
      int maxWidth = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_WIDTH_MAX.toString());

      boolean lengthOk = selectedRegion.getLength() >= minLength && selectedRegion.getLength() <= maxLength;
      boolean widthOk = selectedRegion.getWidth() >= minWidth && selectedRegion.getWidth() <= maxWidth;

      if (!lengthOk || !widthOk) {

        // do not proceed.
        // TODO: send message
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

        // ... do not proceed.
        // Send the player a message.
        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
          ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
          + ((String) this.zoneMenuPlugin.configDelegate.getMap()
            .get("zone_create_area_max_claimable_over")).replace("{area}", "" + zoneArea)
            .replace("{count}", "" + zoneCounter)));*/
        return;
      }

      // If the number of claimed zones exceeds the limit ...
      if (zoneCounter > AppConfiguration.getInstance().getInt(CurrentEntries.CONF_HAVE_MAX.toString())) {

        // ... do not proceed.
        // Send the player a message.
        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&', ((String) this.zoneMenuPlugin.configDelegate
          .getMap().get("prefix"))
          + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_have_over_equal"))
            .replace("{count}", "" + zoneCounter).replace("{zone_create_have_max}",
            "" + this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_have_max"))));*/
        return;
      }

      // If the selected area is smaller than the specified value ...
      if (selectionArea < AppConfiguration.getInstance().getInt(CurrentEntries.CONF_AREA_MIN.toString())) {

        // ... do not proceed.
        // Send the player a message.
        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
          ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
          + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_area_under"))
            .replace("{0}", this.zoneMenuPlugin.configDelegate.getMap()
              .get("zone_create_area_min").toString())));*/
        return;
      }

      // If the selected area is larger than the specified value ...
      if (selectionArea > AppConfiguration.getInstance().getInt(CurrentEntries.CONF_AREA_MAX.toString())) {

        // ... do not proceed.
        // Send the player a message.
        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
          ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
          + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_area_over"))
            .replace("{0}", (String) this.zoneMenuPlugin.configDelegate.getMap()
              .get("zone_create_area_max"))));*/
        return;
      }

      // Create a new WorldGuard region
      ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion(
        (AppConfiguration.getInstance().getString(CurrentEntries.CONF_ZONE_ID.toString()))
          .replace("{creator}", player.getName()).replace("{count}", "" + zoneCounter++),
        selectedRegion.getMinimumPoint(), selectedRegion.getMaximumPoint());

      LocalPlayer wrapedPlayer = ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapPlayer(player);
      RegionManager regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));

      // If region overlaps with unowned regions ...
      if (regionContainer.overlapsUnownedRegion(protectedCuboidRegion, wrapedPlayer)) {

        // ... do not proceed.
        // Send the player a message.
        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
          ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
          + ((String) this.zoneMenuPlugin.configDelegate.getMap()
            .get("zone_create_overlaps_unowned"))));*/
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

      // Clear up player
      ZoneMenuManager.getInstance().clearUpZoneMenuPlayer(player);

      // Send player a message
      /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
        + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_create")).replace("{0}",
          protectedCuboidRegion.getId())));*/
    });

    return true;
  }
  private static final Logger LOG = Logger.getLogger(CommandZoneCreate.class.getName());

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
