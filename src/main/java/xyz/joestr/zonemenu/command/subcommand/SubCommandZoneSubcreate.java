package xyz.joestr.zonemenu.command.subcommand;

import java.io.IOException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;

import xyz.joestr.zonemenu.ZoneMenu;
import xyz.joestr.zonemenu.util.ZoneMenuPlayer;
import xyz.joestr.zonemenu.util.ZoneMenuSignType;
import xyz.joestr.zonemenu.util.ZoneMenuToolType;

/**
 * Class which handles subcommand "subcreate" of command "zone".
 * 
 * @author joestr
 * @since ${project.version}
 * @version ${project.version}
 */
public class SubCommandZoneSubcreate {

    ZoneMenu zoneMenuPlugin = null;

    /**
     * Constrcutor for the
     * {@link xyz.joestr.zonemenu.command.subcommand.SubCommandZoneSubcreate
     * SubCommandZoneSubcreate} class.
     * 
     * @param zoneMenuPlugin
     *            A {@link xyz.joestr.zonemenu.ZoneMenu ZoneMenu}.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    public SubCommandZoneSubcreate(ZoneMenu plugin) {

        this.zoneMenuPlugin = plugin;
    }

    /**
     * Processes.
     * 
     * @param player
     *            A {@link org.bukkit.entity.Player Player}.
     * @param arguments
     *            An array of {@link java.lang.String String}s.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    public void process(Player player, String[] args) {

        // If arguments' length does not equals 2 ...
        if (args.length != 2) {

            // ... wrong usage of "/zone subcreate <Zone>".

            // Send the player a message.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message")).replace("{0}",
                            "/zone subcreate <Zone>")));

            return;
        }

        this.zoneMenuPlugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

            // Grab players worldedit selection
            Selection selectedRegion = this.zoneMenuPlugin.worldEditPlugin.getSelection(player);

            // Check if selection is valid
            if (selectedRegion == null) {

                // Check if players inventory contains a stick
                if (!player.getInventory().contains(Material.STICK)) {

                    // Add a stick to players inventory
                    player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.STICK, 1) });
                }

                // If the player is in the map ...
                if (this.zoneMenuPlugin.zoneMenuPlayers.containsKey(player)) {

                    // ... set the ToolType and SignType.
                    this.zoneMenuPlugin.zoneMenuPlayers.get(player).setToolType(ZoneMenuToolType.SIGN);
                    this.zoneMenuPlugin.zoneMenuPlayers.get(player).setSignType(ZoneMenuSignType.SUBZONE);
                } else {

                    // If not, create, set SignType and ToolType and finally put them into the map.
                    ZoneMenuPlayer zoneMenuPlayer = new ZoneMenuPlayer(player);

                    zoneMenuPlayer.setToolType(ZoneMenuToolType.SIGN);
                    zoneMenuPlayer.setSignType(ZoneMenuSignType.SUBZONE);

                    this.zoneMenuPlugin.zoneMenuPlayers.put(player, zoneMenuPlayer);
                }

                // Send player a message
                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_subcreate_sign")));

                return;
            }

            // If the player is not in the map ...
            if (!this.zoneMenuPlugin.zoneMenuPlayers.containsKey(player)) {

                // ... do not proceed.
                return;
            }

            // If ToolType or SingType is wrong ...
            if (this.zoneMenuPlugin.zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.SIGN
                    || this.zoneMenuPlugin.zoneMenuPlayers.get(player).getSignType() != ZoneMenuSignType.SUBZONE) {

                // ... do not proceed.
                return;
            }

            ProtectedRegion protectedRegion = null;

            if (t.isEmpty()) {

                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        (String) this.zoneMenuPlugin.configDelegate.getMap().get("no_zone")));

                return;
            }

            for (ProtectedRegion pr : t) {

                if (pr.getId().equalsIgnoreCase(args[1])) {

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

                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("not_exisiting_zone")).replace("{0}",
                                args[1])));

                return;
            }

            int minBlockX = selectedRegion.getMinimumPoint().getBlockX();
            int minBlockY = selectedRegion.getMinimumPoint().getBlockY();
            int minBlockZ = selectedRegion.getMinimumPoint().getBlockZ();
            int maxBlockX = selectedRegion.getMaximumPoint().getBlockX();
            int maxBlockY = selectedRegion.getMaximumPoint().getBlockY();
            int maxBlockZ = selectedRegion.getMaximumPoint().getBlockZ();

            if (!protectedRegion.contains(minBlockX, minBlockY, minBlockZ)) {

                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_subcreate_not_in_zone"))
                                .replace("{0}", protectedRegion.getId())));

                return;
            }

            if (!protectedRegion.contains(maxBlockX, maxBlockY, maxBlockZ)) {

                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_subcreate_not_in_zone")));

                return;
            }

            // Grab some values to work with
            Location min = selectedRegion.getMinimumPoint();
            Location max = selectedRegion.getMaximumPoint();
            double firstX = min.getX();
            double firstY = min.getY();
            double firstZ = min.getZ();
            double secondX = max.getX();
            double secondY = max.getY();
            double secondZ = max.getZ();

            // Create a new WorldGuard region
            ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion(
                    ((String) this.zoneMenuPlugin.idDelegate.getMap().get("subzone_id"))
                            .replace("{parent}", protectedRegion.getId()).replace("{count}", "" + subZoneCounter++),
                    new BlockVector(firstX, firstY, firstZ), new BlockVector(secondX, secondY, secondZ));

            try {

                protectedCuboidRegion.setParent(protectedRegion);
            } catch (CircularInheritanceException e) {

                e.printStackTrace();

                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_subcreate_circular")));

                return;
            }

            // Check if Worldguards profileservice contains players name
            ProfileService ps = this.zoneMenuPlugin.worldGuardPlugin.getProfileService();

            try {

                ps.findByName(player.getName());
            } catch (IOException e) {

                e.printStackTrace();
            } catch (InterruptedException e) {

                e.printStackTrace();
            }

            // Create a new domain
            DefaultDomain domain = new DefaultDomain();

            // Wrap player and add it to the domain
            domain.addPlayer(this.zoneMenuPlugin.worldGuardPlugin.wrapPlayer(player));

            // Apply the domain to owners
            protectedCuboidRegion.setOwners(domain);

            // Set the priority to the specified value in the config file
            protectedCuboidRegion.setPriority(protectedRegion.getPriority() + 1);

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
            this.zoneMenuPlugin.worldGuardPlugin.getRegionManager(player.getWorld()).addRegion(protectedCuboidRegion);

            this.zoneMenuPlugin.clearUpZoneMenuPlayer(player);

            // Send player a message
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_subcreate")).replace("{0}",
                            protectedCuboidRegion.getId())));
        });
    }
}
