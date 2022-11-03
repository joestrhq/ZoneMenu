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
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
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
public class CommandZoneExpand implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneExpand.class.getName());

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

      // Grab players worldedit selection
      Region selectedregion = null;
      try {
        selectedregion = WorldEdit.getInstance().getSessionManager().findByName(player.getName()).getSelection((com.sk89q.worldedit.world.World) player.getWorld());
      } catch (IncompleteRegionException ex) {
        LOG.log(Level.SEVERE, "", ex);
      }

      // Check if selection is valid
      if (selectedregion == null) {

        // Check if players inventory contains a stick
        if (!player.getInventory().contains(Material.STICK)) {

          // Add a stick to players inventory
          player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.STICK, 1)});
        }

        // If the player is in the map ...
        if (ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {

          // ... set the ToolType and SignType
          ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).setToolType(ZoneMenuToolType.SIGN);
          ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).setSignType(ZoneMenuSignType.ZONE);
        } else {

          // If not, create, set ToolType and SingType and finally put them into the map.
          ZoneMenuPlayer zoneMenuPlayer = new ZoneMenuPlayer(player);

          zoneMenuPlayer.setToolType(ZoneMenuToolType.SIGN);
          zoneMenuPlayer.setSignType(ZoneMenuSignType.ZONE);

          ZoneMenuManager.getInstance().zoneMenuPlayers.put(player, zoneMenuPlayer);
        }

        // Send player a message
        /*player.sendMessage(
                    this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
                player.sendMessage(this.plugin.colorCode('&',
                    (String) this.plugin.configDelegate.getMap().get("zone_create_sign")));*/
        return;
      }

      // If the player is not in the map ...
      if (!ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {

        // ... do not proceed.
        return;
      }

      // If SignType and ToolType are wrong ...
      if (ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.SIGN
        || ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getSignType() != ZoneMenuSignType.ZONE) {

        // ... do not proceed.
        return;
      }

      int minLength = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_LENGTH_MIN.toString());
      int maxLength = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_LENGTH_MAX.toString());
      int minWidth = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_WIDTH_MIN.toString());
      int maxWidth = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_WIDTH_MAX.toString());

      boolean lengthOk = selectedregion.getLength() >= minLength && selectedregion.getLength() <= maxLength;
      boolean widthOk = selectedregion.getWidth() >= minWidth && selectedregion.getWidth() <= maxWidth;

      if (!lengthOk || !widthOk) {

        /*player.sendMessage(
                    this.plugin
                        .colorCode('&',
                            (String) this.plugin.configDelegate.getMap()
                                .get("zone_create_width_length_error"))
                        .replace("{0}",
                            (String) this.plugin.configDelegate.getMap().get("zone_create_length_min"))
                        .replace("{1}",
                            (String) this.plugin.configDelegate.getMap().get("zone_create_length_max"))
                        .replace("{2}",
                            (String) this.plugin.configDelegate.getMap().get("zone_create_width_min"))
                        .replace("{3}",
                            (String) this.plugin.configDelegate.getMap().get("zone_create_width_max")));
         */
        return;
      }

      // Holds the number of zones (with no childs)
      int zoneCounter = 1;

      // Holds the area of zones (with no childs)
      int zoneArea = 0;

      for (ProtectedRegion pr : t) {

        if (pr.getParent() == null) {

          zoneCounter = zoneCounter + 1;
          zoneArea = zoneArea + (pr.volume() / (ZoneMenuManager.getInstance().difference(pr.getMinimumPoint().getBlockY(),
            pr.getMaximumPoint().getBlockY())));
        }
      }

      int createMaxClaimable = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_AREA_MAX_CLAIMABLE.toString());
      if (zoneArea + (selectedregion.getWidth() * selectedregion.getLength()) > createMaxClaimable) {

        /*player.sendMessage(
                    this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
                player.sendMessage(this.plugin.colorCode('&',
                    ((String) this.plugin.configDelegate.getMap().get("zone_create_area_max_claimable_over"))
                        .replace("{area}", "" + zoneArea).replace("{count}", "" + zoneCounter)));*/
        return;
      }

      int createHaveMax = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_HAVE_MAX.toString());
      if (zoneCounter >= createHaveMax) {

        /*player.sendMessage(
                    this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
                player.sendMessage(this.plugin.colorCode('&',
                    ((String) this.plugin.configDelegate.getMap().get("zone_create_have_over_equal"))
                        .replace("{count}", "" + zoneCounter).replace("{zone_create_have_max}",
                        "" + this.plugin.configDelegate.getMap().get("zone_create_have_max"))));*/
        return;
      }

      int createAreaMin = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_AREA_MIN.toString());
      if (selectedregion.getWidth() * selectedregion.getLength() < createAreaMin) {
        /*player.sendMessage(
          this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
        player.sendMessage(this.plugin
          .colorCode('&', (String) this.plugin.configDelegate.getMap().get("zone_create_area_under"))
          .replace("{0}", this.plugin.configDelegate.getMap().get("zone_create_area_min").toString()));*/
        return;
      }

      int createAreaMax = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_AREA_MAX.toString());
      if (selectedregion.getWidth() * selectedregion.getLength() > createAreaMax) {

        /*player.sendMessage(this.plugin
                    .colorCode('&', (String) this.plugin.configDelegate.getMap().get("zone_create_area_over"))
                    .replace("{0}", (String) this.plugin.configDelegate.getMap().get("zone_create_area_max")));*/
        return;
      }

      String zoneId = AppConfiguration.getInstance().getString(CurrentEntries.CONF_ZONE_ID.toString());
      String filledZoneId = zoneId.replace("{creator}", player.getName()).replace("{count}", "" + zoneCounter++);
      ProtectedCuboidRegion protectedcuboidregion
        = new ProtectedCuboidRegion(
          filledZoneId, selectedregion.getMinimumPoint(), selectedregion.getMaximumPoint());

      // Check if region overlaps with unowned regions
      RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
      com.sk89q.worldedit.world.World worldEditWorld = (com.sk89q.worldedit.world.World) player.getWorld();
      LocalPlayer wrapedPlayer = ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapPlayer(player);

      if (container.get(worldEditWorld).overlapsUnownedRegion(protectedcuboidregion, wrapedPlayer)) {

        /*player.sendMessage(this.plugin.colorCode('&',
          (String) this.plugin.configDelegate.getMap().get("zone_create_overlaps_unowned")));*/
        return;
      }

      try {
        WorldGuard.getInstance().getProfileService().findByName(player.getName());
      } catch (IOException | InterruptedException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }

      DefaultDomain domain = new DefaultDomain();
      domain.addPlayer(wrapedPlayer);
      protectedcuboidregion.setOwners(domain);
      int priority = AppConfiguration.getInstance().getInt(CurrentEntries.CONF_PRIORITY.toString());
      protectedcuboidregion.setPriority(priority);

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
      WorldGuard.getInstance().getPlatform().getRegionContainer().get((com.sk89q.worldedit.world.World) player.getWorld()).addRegion(protectedcuboidregion);

      ZoneMenuManager.getInstance().clearUpZoneMenuPlayer(player);

      // Send player a message
      /*player.sendMessage(
                this.plugin.colorCode('&', ((String) this.plugin.configDelegate.getMap().get("zone_create"))
                    .replace("{0}", protectedcuboidregion.getId())));*/
    });

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
