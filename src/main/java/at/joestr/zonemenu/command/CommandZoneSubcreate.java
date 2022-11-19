/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.zonemenu.command;

import at.joestr.zonemenu.util.ZoneMenuManager;
import at.joestr.zonemenu.util.ZoneMenuPlayer;
import at.joestr.zonemenu.util.ZoneMenuSignType;
import at.joestr.zonemenu.util.ZoneMenuToolType;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
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
public class CommandZoneSubcreate implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneSubcreate.class.getName());

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

    ZoneMenuManager.getInstance().futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {
      // Grab players worldedit selection
      Region selectedRegion = ZoneMenuManager.getInstance().getPlayerSelection(player);

      // Check if selection is valid
      if (selectedRegion == null) {

        // Check if players inventory contains a stick
        if (!player.getInventory().contains(Material.STICK)) {

          // Add a stick to players inventory
          player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.STICK, 1)});
        }

        // If the player is in the map ...
        if (ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {

          // ... set the ToolType and SignType.
          ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).setToolType(ZoneMenuToolType.SIGN);
          ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).setSignType(ZoneMenuSignType.SUBZONE);
        } else {

          // If not, create, set SignType and ToolType and finally put them into the map.
          ZoneMenuPlayer zoneMenuPlayer = new ZoneMenuPlayer(player);

          zoneMenuPlayer.setToolType(ZoneMenuToolType.SIGN);
          zoneMenuPlayer.setSignType(ZoneMenuSignType.SUBZONE);

          ZoneMenuManager.getInstance().zoneMenuPlayers.put(player, zoneMenuPlayer);
        }

        // Send player a message
        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_subcreate_sign")));*/
        return;
      }

      // If the player is not in the map ...
      if (!ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {

        // ... do not proceed.
        return;
      }

      // If ToolType or SingType is wrong ...
      if (ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.SIGN
        || ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getSignType() != ZoneMenuSignType.SUBZONE) {

        // ... do not proceed.
        return;
      }

      ProtectedRegion protectedRegion = null;

      if (t.isEmpty()) {

        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    (String) this.zoneMenuPlugin.configDelegate.getMap().get("no_zone")));*/
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

        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("not_exisiting_zone")).replace("{0}",
                        arguments[1])));*/
        return;
      }

      int minBlockX = selectedRegion.getMinimumPoint().getBlockX();
      int minBlockY = selectedRegion.getMinimumPoint().getBlockY();
      int minBlockZ = selectedRegion.getMinimumPoint().getBlockZ();
      int maxBlockX = selectedRegion.getMaximumPoint().getBlockX();
      int maxBlockY = selectedRegion.getMaximumPoint().getBlockY();
      int maxBlockZ = selectedRegion.getMaximumPoint().getBlockZ();

      if (!protectedRegion.contains(minBlockX, minBlockY, minBlockZ)) {

        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_subcreate_not_in_zone"))
                        .replace("{0}", protectedRegion.getId())));*/
        return;
      }

      if (!protectedRegion.contains(maxBlockX, maxBlockY, maxBlockZ)) {

        /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_subcreate_not_in_zone")));*/
        return;
      }

      // Create a new WorldGuard region
      /*ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion(
                ((String) this.zoneMenuPlugin.idDelegate.getMap().get("subzone_id"))
                    .replace("{parent}", protectedRegion.getId()).replace("{count}", "" + subZoneCounter++),
                selectedRegion.getMinimumPoint(), selectedRegion.getMaximumPoint());*/
      //try {
      //protectedCuboidRegion.setParent(protectedRegion);
      //} catch (ProtectedRegion.CircularInheritanceException e) {
      //e.printStackTrace();

      /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_subcreate_circular")));*/
      //return;
      //}
      try {
        WorldGuard.getInstance().getProfileService().findByName(player.getName());
      } catch (IOException ex) {
        Logger.getLogger(CommandZoneSubcreate.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InterruptedException ex) {
        Logger.getLogger(CommandZoneSubcreate.class.getName()).log(Level.SEVERE, null, ex);
      }

      // Create a new domain
      DefaultDomain domain = new DefaultDomain();

      // Wrap player and add it to the domain
      domain.addPlayer(ZoneMenuManager.getInstance().getWorldGuardPlugin().wrapPlayer(player));

      // Apply the domain to owners
      //protectedCuboidRegion.setOwners(domain);
      // Set the priority to the specified value in the config file
      //protectedCuboidRegion.setPriority(protectedRegion.getPriority() + 1);
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
      //WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld())).addRegion(protectedCuboidRegion);
      ZoneMenuManager.getInstance().clearUpZoneMenuPlayer(player);

      // Send player a message
      /*player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_subcreate")).replace("{0}",
                    protectedCuboidRegion.getId().replace("+", "#").replace("-", "."))));*/
    });

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
