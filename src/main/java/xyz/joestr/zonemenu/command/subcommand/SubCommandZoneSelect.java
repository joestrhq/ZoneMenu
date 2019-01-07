package xyz.joestr.zonemenu.command.subcommand;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
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

                if (pr.getId().replace("+", "#").replace("-", ".").equalsIgnoreCase(args[1])) {

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

            LocalSession session
                = WorldEdit
                    .getInstance()
                    .getSessionManager()
                    .get(BukkitAdapter.adapt(player));

            com.sk89q.worldedit.world.World weWorld
                = BukkitAdapter.adapt(player.getWorld());

            session.setRegionSelector(
                weWorld,
                new CuboidRegionSelector(
                    weWorld,
                    BukkitAdapter.asBlockVector(minLoc),
                    BukkitAdapter.asBlockVector(maxLoc)
                )
            );

            session.dispatchCUISelection(BukkitAdapter.adapt(player));

            player.sendMessage(this.zoneMenuPlugin.colorCode(
                '&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_select")).replace("{0}", args[1]))
            );

            return;
        });
    }
}
