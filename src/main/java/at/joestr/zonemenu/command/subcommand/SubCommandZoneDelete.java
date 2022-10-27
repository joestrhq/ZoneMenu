package at.joestr.zonemenu.command.subcommand;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import java.util.List;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import at.joestr.zonemenu.ZoneMenuPlugin;

public class SubCommandZoneDelete {

    ZoneMenuPlugin zoneMenuPlugin = null;

    public SubCommandZoneDelete(ZoneMenuPlugin zoneMenuPlugin) {

        this.zoneMenuPlugin = zoneMenuPlugin;
    }

    public void process(Player player, String[] arguments) {

        // If arguments' length does not equals 2 ...
        if (arguments.length != 2) {

            // ... wrong usage of "/zone delete <Zone>".
            // Send the player a message.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&', ((String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message"))
                .replace("{0}", "/zone delete <Zone>")));

            return;
        }

        zoneMenuPlugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

            // Initialise region
            ProtectedRegion protectedRegion = null;

            // If the list is empty ...
            if (t.isEmpty()) {

                // ... send the player a message.
                player.sendMessage(this.zoneMenuPlugin.colorCode('&', (String) this.zoneMenuPlugin.configDelegate.getMap().get("no_zone")));

                return;
            }

            // Loop through all regions ...
            for (ProtectedRegion protectedRegion_ : t) {

                // ... and if the region ID equals the second argument (<Zone>) ...
                if (protectedRegion_.getId().equalsIgnoreCase(arguments[1])) {

                    // ... set the found region.
                    protectedRegion = protectedRegion_;
                }
            }

            // If region equals null ...
            if (protectedRegion == null) {

                // ... no region with this ID was not found.
                // Send the player a message.
                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("not_exisiting_zone")).replace("{0}",
                        arguments[1])));

                return;
            }

            // Remove the region from worlds region manager
            WorldGuard
                .getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(player.getWorld()))
                .removeRegion(
                    protectedRegion.getId(),
                    RemovalStrategy.REMOVE_CHILDREN
                );

            // Send a message to the player
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) zoneMenuPlugin.configDelegate.getMap().get("zone_delete")).replace("{0}", arguments[1])));
        });
    }
}
