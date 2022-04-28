package at.joestr.zonemenu.command.subcommand;

import java.io.IOException;
import java.util.List;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import at.joestr.zonemenu.ZoneMenu;
import at.joestr.zonemenu.util.ZoneMenuPlayer;
import at.joestr.zonemenu.util.ZoneMenuSignType;
import at.joestr.zonemenu.util.ZoneMenuToolType;

public class SubCommandZoneExpand {

    ZoneMenu plugin = null;

    public SubCommandZoneExpand(ZoneMenu plugin) {
        this.plugin = plugin;
    }

    public void process(Player player, String[] args) {

        if (args.length != 1) {

            // Wrong usage of the /zone expand <Zone> command
            player.sendMessage(
                this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("usage_message"))
                    .replace("{0}", "/zone create"));

            return;
        }

        this.plugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

            // Grab players worldedit selection
            Region selectedregion = null;
            try {
                selectedregion = WorldEdit.getInstance().getSessionManager().findByName(player.getName()).getSelection((com.sk89q.worldedit.world.World) player.getWorld());
            } catch (IncompleteRegionException ex) {
                Logger.getLogger(SubCommandZoneExpand.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Check if selection is valid
            if (selectedregion == null) {

                // Check if players inventory contains a stick
                if (!player.getInventory().contains(Material.STICK)) {

                    // Add a stick to players inventory
                    player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.STICK, 1)});
                }

                // If the player is in the map ...
                if (this.plugin.zoneMenuPlayers.containsKey(player)) {

                    // ... set the ToolType and SignType
                    this.plugin.zoneMenuPlayers.get(player).setToolType(ZoneMenuToolType.SIGN);
                    this.plugin.zoneMenuPlayers.get(player).setSignType(ZoneMenuSignType.ZONE);
                } else {

                    // If not, create, set ToolType and SingType and finally put them into the map.
                    ZoneMenuPlayer zoneMenuPlayer = new ZoneMenuPlayer(player);

                    zoneMenuPlayer.setToolType(ZoneMenuToolType.SIGN);
                    zoneMenuPlayer.setSignType(ZoneMenuSignType.ZONE);

                    this.plugin.zoneMenuPlayers.put(player, zoneMenuPlayer);
                }

                // Send player a message
                player.sendMessage(
                    this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
                player.sendMessage(this.plugin.colorCode('&',
                    (String) this.plugin.configDelegate.getMap().get("zone_create_sign")));

                return;
            }

            // If the player is not in the map ...
            if (!this.plugin.zoneMenuPlayers.containsKey(player)) {

                // ... do not proceed.
                return;
            }

            // If SignType and ToolType are wrong ...
            if (this.plugin.zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.SIGN
                || this.plugin.zoneMenuPlayers.get(player).getSignType() != ZoneMenuSignType.ZONE) {

                // ... do not proceed.
                return;
            }

            if (selectedregion.getLength() < (Integer) this.plugin.configDelegate.getMap().get("zone_create_length_min")
                || selectedregion
                    .getLength() > (Integer) this.plugin.configDelegate.getMap().get("zone_create_length_max")
                || selectedregion
                    .getWidth() < (Integer) this.plugin.configDelegate.getMap().get("zone_create_width_min")
                || selectedregion
                    .getWidth() > (Integer) this.plugin.configDelegate.getMap().get("zone_create_width_max")) {

                player.sendMessage(
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
                return;
            }

            // Holds the number of zones (with no childs)
            int zoneCounter = 1;

            // Holds the area of zones (with no childs)
            int zoneArea = 0;

            for (ProtectedRegion pr : t) {

                if (pr.getParent() == null) {

                    zoneCounter = zoneCounter + 1;
                    zoneArea = zoneArea + (pr.volume() / (this.plugin.difference(pr.getMinimumPoint().getBlockY(),
                        pr.getMaximumPoint().getBlockY())));
                }
            }

            if (zoneArea
                + (selectedregion.getWidth() * selectedregion.getLength()) > (Integer) this.plugin.configDelegate
                .getMap().get("zone_create_area_max_claimable")) {

                player.sendMessage(
                    this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
                player.sendMessage(this.plugin.colorCode('&',
                    ((String) this.plugin.configDelegate.getMap().get("zone_create_area_max_claimable_over"))
                        .replace("{area}", "" + zoneArea).replace("{count}", "" + zoneCounter)));

                return;
            }

            if (zoneCounter >= (Integer) this.plugin.configDelegate.getMap().get("zone_create_have_max")) {

                player.sendMessage(
                    this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
                player.sendMessage(this.plugin.colorCode('&',
                    ((String) this.plugin.configDelegate.getMap().get("zone_create_have_over_equal"))
                        .replace("{count}", "" + zoneCounter).replace("{zone_create_have_max}",
                        "" + this.plugin.configDelegate.getMap().get("zone_create_have_max"))));
                return;
            }

            // Check if selected area is smaller than the specified maximum area in the
            // config file
            if (selectedregion.getWidth() * selectedregion.getLength() < Integer
                .parseInt(this.plugin.configDelegate.getMap().get("zone_create_area_min").toString())) {
                player.sendMessage(
                    this.plugin.colorCode('&', (String) this.plugin.configDelegate.getMap().get("head")));
                player.sendMessage(this.plugin
                    .colorCode('&', (String) this.plugin.configDelegate.getMap().get("zone_create_area_under"))
                    .replace("{0}", this.plugin.configDelegate.getMap().get("zone_create_area_min").toString()));
                return;
            }

            // Check if selected area is larger than the specified minimum area in the
            // config file
            if (selectedregion.getWidth() * selectedregion.getLength() > Integer
                .parseInt(this.plugin.configDelegate.getMap().get("zone_create_area_max").toString())) {

                player.sendMessage(this.plugin
                    .colorCode('&', (String) this.plugin.configDelegate.getMap().get("zone_create_area_over"))
                    .replace("{0}", (String) this.plugin.configDelegate.getMap().get("zone_create_area_max")));
                return;
            }

            // Create a new WorldGuard region
            ProtectedCuboidRegion protectedcuboidregion = new ProtectedCuboidRegion(
                ((String) this.plugin.idDelegate.getMap().get("zone_id")).replace("{creator}", player.getName())
                    .replace("{count}", "" + zoneCounter++),
                selectedregion.getMinimumPoint(), selectedregion.getMaximumPoint());

            // Check if region overlaps with unowned regions
            if (WorldGuard.getInstance().getPlatform().getRegionContainer().get((com.sk89q.worldedit.world.World) player.getWorld())
                .overlapsUnownedRegion(protectedcuboidregion, this.plugin.worldGuardPlugin.wrapPlayer(player))) {

                player.sendMessage(this.plugin.colorCode('&',
                    (String) this.plugin.configDelegate.getMap().get("zone_create_overlaps_unowned")));
                return;
            }

            // Check if Worldguards profileservice contains players name
            try {
                WorldGuard.getInstance().getProfileService().findByName(player.getName());
            } catch (IOException ex) {
                Logger.getLogger(SubCommandZoneExpand.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(SubCommandZoneExpand.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Create a new domain
            DefaultDomain domain = new DefaultDomain();
            // Wrap player and add it to the domain
            domain.addPlayer(this.plugin.worldGuardPlugin.wrapPlayer(player));
            // Apply the domain to owners
            protectedcuboidregion.setOwners(domain);
            // Set the priority to the specified value in the config file
            protectedcuboidregion.setPriority(
                Integer.parseInt(this.plugin.configDelegate.getMap().get("zone_create_priority").toString()));

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

            this.plugin.clearUpZoneMenuPlayer(player);

            // Send player a message
            player.sendMessage(
                this.plugin.colorCode('&', ((String) this.plugin.configDelegate.getMap().get("zone_create"))
                    .replace("{0}", protectedcuboidregion.getId())));
        });
    }
}
