package xyz.joestr.zonemenu.command.subcommand;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import java.io.IOException;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

import xyz.joestr.zonemenu.ZoneMenu;
import xyz.joestr.zonemenu.util.ZoneMenuPlayer;
import xyz.joestr.zonemenu.util.ZoneMenuSignType;
import xyz.joestr.zonemenu.util.ZoneMenuToolType;

/**
 * Class which handles subcommand "create" of command "zone".
 *
 * @author joestr
 * @since ${project.version}
 * @version ${project.version}
 */
public class SubCommandZoneCreate {

    ZoneMenu zoneMenuPlugin = null;

    /**
     * Constrcutor for the {@link xyz.joestr.zonemenu.command.subcommand.SubCommandZoneCreate
     * SubCommandZoneCreate} class.
     *
     * @param zoneMenuPlugin A {@link xyz.joestr.zonemenu.ZoneMenu ZoneMenu}.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    public SubCommandZoneCreate(ZoneMenu zoneMenuPlugin) {

        this.zoneMenuPlugin = zoneMenuPlugin;
    }

    /**
     * Processes.
     *
     * @param player A {@link org.bukkit.entity.Player Player}.
     * @param arguments An array of {@link java.lang.String String}s.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    public void process(Player player, String[] arguments) {

        // If arguments' length does not equals 1 ...
        if (arguments.length != 1) {

            // ... wrong usage of "/zone cancel".
            // Send the player a message.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message")).replace("{0}",
                    "/zone create")));

            return;
        }

        // If the player is in the map ...
        if (this.zoneMenuPlugin.zoneMenuPlayers.containsKey(player)) {

            // ... set the ToolType and SignType
            this.zoneMenuPlugin.zoneMenuPlayers.get(player).setToolType(ZoneMenuToolType.SIGN);
            this.zoneMenuPlugin.zoneMenuPlayers.get(player).setSignType(ZoneMenuSignType.ZONE);
        } else {

            // If not, create, set ToolType and SingType and finally put them into the map.
            ZoneMenuPlayer zoneMenuPlayer = new ZoneMenuPlayer(player);

            zoneMenuPlayer.setToolType(ZoneMenuToolType.SIGN);
            zoneMenuPlayer.setSignType(ZoneMenuSignType.ZONE);

            this.zoneMenuPlugin.zoneMenuPlayers.put(player, zoneMenuPlayer);
        }

        // Grab players worldedit selection
        Region selectedRegion_ = this.zoneMenuPlugin.getPlayerSelection(player);

        // Check if selection is valid
        if (selectedRegion_ == null) {

            // Check if players inventory contains a stick
            if (!player.getInventory().contains(Material.STICK)) {

                // Add a stick to players inventory
                player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.STICK, 1)});
            }

            // Send player a message
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_sign"))));

            return;
        }
        
        player.sendMessage("Test");
        
        Bukkit.getLogger().log(Level.INFO, "Hot swapping");
        
        final Region selectedRegion = selectedRegion_;
        
        try {
                WorldGuard.getInstance().getProfileService().findByName(player.getName());
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(SubCommandZoneCreate.class.getName()).log(Level.SEVERE, null, ex);
            }

        this.zoneMenuPlugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

            if (u != null) {
                u.printStackTrace(System.err);
                return;
            }

            // If the player is not in the map ...
            if (!this.zoneMenuPlugin.zoneMenuPlayers.containsKey(player)) {

                // ... do not proceed.
                return;
            }

            // If SignType or ToolType is wrong ...
            if (this.zoneMenuPlugin.zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.SIGN
                || this.zoneMenuPlugin.zoneMenuPlayers.get(player).getSignType() != ZoneMenuSignType.ZONE) {

                // ... do not proceed.
                return;
            }

            // If the selection exceeds the with or length limitations ...
            if (selectedRegion
                .getLength() < (Integer) this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_length_min")
                || selectedRegion.getLength() > (Integer) this.zoneMenuPlugin.configDelegate.getMap()
                .get("zone_create_length_max")
                || selectedRegion.getWidth() < (Integer) this.zoneMenuPlugin.configDelegate.getMap()
                .get("zone_create_width_min")
                || selectedRegion.getWidth() > (Integer) this.zoneMenuPlugin.configDelegate.getMap()
                .get("zone_create_width_max")) {

                // ... do not proceed.
                // Send the player a message.
                player.sendMessage(
                    this.zoneMenuPlugin.colorCode(
                        '&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                        + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_width_length_error"))
                            .replace(
                                "{0}",
                                ((Integer) this.zoneMenuPlugin.configDelegate.getMap()
                                    .get("zone_create_length_min")).toString()
                            ).replace(
                                "{1}",
                                ((Integer) this.zoneMenuPlugin.configDelegate.getMap()
                                    .get("zone_create_width_min")).toString()
                            ).replace(
                                "{2}",
                                ((Integer) this.zoneMenuPlugin.configDelegate.getMap()
                                    .get("zone_create_length_max")).toString()
                            ).replace(
                                "{3}",
                                ((Integer) this.zoneMenuPlugin.configDelegate.getMap()
                                    .get("zone_create_width_max")).toString()
                            )
                    )
                );

                return;
            }

            // Holds the number of zones (with no childs)
            int zoneCounter = 1;

            // Holds the area of zones (with no childs)
            int zoneArea = 0;

            for (ProtectedRegion protectedRegion_ : t) {

                if (protectedRegion_.getParent() == null) {

                    zoneCounter = zoneCounter + 1;
                    zoneArea = zoneArea + (protectedRegion_.volume()
                        / (this.zoneMenuPlugin.difference(protectedRegion_.getMinimumPoint().getBlockY(),
                            protectedRegion_.getMaximumPoint().getBlockY())));
                }
            }

            // If the area of the selection is over the limit of claimable blocks ...
            if (zoneArea + (selectedRegion.getWidth()
                * selectedRegion.getLength()) > (Integer) this.zoneMenuPlugin.configDelegate.getMap()
                .get("zone_create_area_max_claimable")) {

                // ... do not proceed.
                // Send the player a message.
                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                    + ((String) this.zoneMenuPlugin.configDelegate.getMap()
                        .get("zone_create_area_max_claimable_over")).replace("{area}", "" + zoneArea)
                        .replace("{count}", "" + zoneCounter)));

                return;
            }

            // If the number of claimed zones exceeds the limit ...
            if (zoneCounter >= (Integer) this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_have_max")) {

                // ... do not proceed.
                // Send the player a message.
                player.sendMessage(this.zoneMenuPlugin.colorCode('&', ((String) this.zoneMenuPlugin.configDelegate
                    .getMap().get("prefix"))
                    + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_have_over_equal"))
                        .replace("{count}", "" + zoneCounter).replace("{zone_create_have_max}",
                        "" + this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_have_max"))));

                return;
            }

            // If the selected area is smaller than the specified value ...
            if (selectedRegion.getWidth() * selectedRegion.getLength() < Integer
                .parseInt(this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_area_min").toString())) {

                // ... do not proceed.
                // Send the player a message.
                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                    + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_area_under"))
                        .replace("{0}", this.zoneMenuPlugin.configDelegate.getMap()
                            .get("zone_create_area_min").toString())));

                return;
            }

            // If the selected area is larger than the specified value ...
            if (selectedRegion.getWidth() * selectedRegion.getLength() > Integer
                .parseInt(this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_area_max").toString())) {

                // ... do not proceed.
                // Send the player a message.
                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                    + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_area_over"))
                        .replace("{0}", (String) this.zoneMenuPlugin.configDelegate.getMap()
                            .get("zone_create_area_max"))));

                return;
            }

            // Create a new WorldGuard region
            ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion(
                ((String) this.zoneMenuPlugin.idDelegate.getMap().get("zone_id"))
                    .replace("{creator}", player.getName()).replace("{count}", "" + zoneCounter++),
                selectedRegion.getMinimumPoint(), selectedRegion.getMaximumPoint());

            // If region overlaps with unowned regions ...
            if (WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld())).overlapsUnownedRegion(protectedCuboidRegion, this.zoneMenuPlugin.worldGuardPlugin.wrapPlayer(player))) {

                // ... do not proceed.
                // Send the player a message.
                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                    + ((String) this.zoneMenuPlugin.configDelegate.getMap()
                        .get("zone_create_overlaps_unowned"))));

                return;
            }

            try {
                WorldGuard.getInstance().getProfileService().findByName(player.getName());
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(SubCommandZoneCreate.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Create a new domain
            DefaultDomain domain = new DefaultDomain();

            // Wrap player and add it to the domain
            domain.addPlayer(this.zoneMenuPlugin.worldGuardPlugin.wrapPlayer(player));

            // Apply the domain to owners
            protectedCuboidRegion.setOwners(domain);

            // Set the priority to the specified value in the config file
            protectedCuboidRegion.setPriority(Integer
                .parseInt(this.zoneMenuPlugin.configDelegate.getMap().get("zone_create_priority").toString()));

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
            this.zoneMenuPlugin.clearUpZoneMenuPlayer(player);

            // Send player a message
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_create")).replace("{0}",
                    protectedCuboidRegion.getId())));
        });
    }
}
