package xyz.joestr.zonemenu.command.subcommand;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import xyz.joestr.zonemenu.ZoneMenu;

public class SubCommandZoneSelect {

    ZoneMenu zoneMenuPlugin = null;

    public SubCommandZoneSelect(ZoneMenu zoneMenuPlugin) {
        this.zoneMenuPlugin = zoneMenuPlugin;
    }

    public void process(Player player, String[] args) {

        if (args.length != 2) {

            // Wrong usage of the /zone select <Zone> command
            player.sendMessage(this.zoneMenuPlugin
                .colorCode('&', (String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message"))
                .replace("{0}", "/zone select <Zone>"));

            return;
        }

        this.zoneMenuPlugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

            // Initialise new region
            ProtectedRegion protectedregion = null;

            if (t.isEmpty()) {

                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    (String) this.zoneMenuPlugin.configDelegate.getMap().get("no_zone")));

                return;
            }

            for (ProtectedRegion pr : t) {

                if (pr.getId().equalsIgnoreCase(args[1])) {

                    protectedregion = pr;
                }
            }

            // Check if region in invalid
            if (protectedregion == null) {

                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("not_exisiting_zone")).replace("{0}",
                        args[1])));

                return;
            }

            Location minLoc = new Location(player.getWorld(), protectedregion.getMinimumPoint().getBlockX(),
                protectedregion.getMinimumPoint().getBlockY(), protectedregion.getMinimumPoint().getBlockZ());

            Location maxLoc = new Location(player.getWorld(), protectedregion.getMaximumPoint().getBlockX(),
                protectedregion.getMaximumPoint().getBlockY(), protectedregion.getMaximumPoint().getBlockZ());

            WorldEdit
                .getInstance()
                .getSessionManager()
                .findByName(player.getName())
                .getRegionSelector((com.sk89q.worldedit.world.World) player.getWorld())
                .selectPrimary(BlockVector3.at(minLoc.getBlockX(), minLoc.getBlockY(), minLoc.getBlockZ()), null);

            WorldEdit
                .getInstance()
                .getSessionManager()
                .findByName(player.getName())
                .getRegionSelector((com.sk89q.worldedit.world.World) player.getWorld())
                .selectSecondary(BlockVector3.at(maxLoc.getBlockX(), maxLoc.getBlockY(), maxLoc.getBlockZ()), null);

            player.sendMessage(this.zoneMenuPlugin.colorCode(
                '&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_select")).replace("{0}", args[1]))
            );

            return;
        });
    }
}
