package at.joestr.zonemenu.command.subcommand;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import at.joestr.zonemenu.ZoneMenu;

public class SubCommandZoneList {

    ZoneMenu zoneMenuPlugin = null;

    public SubCommandZoneList(ZoneMenu plugin) {

        this.zoneMenuPlugin = plugin;
    }

    public void process(Player player, String[] args) {

        // If arguments' length does not equals 1 ...
        if (args.length != 1) {

            // ... wrong usage of "/zone cancel".
            // Send the player a message.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message")).replace("{0}",
                    "/zone list")));

            return;
        }

        this.zoneMenuPlugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {

            // If the list is empty ...
            if (t.isEmpty()) {

                // ... send the player a message.
                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("no_zone"))));

                return;
            }

            String sregionString = "";

            Iterator<ProtectedRegion> iterator = t.iterator();

            while (iterator.hasNext()) {

                ProtectedRegion protectedRegion_ = iterator.next();

                if (iterator.hasNext()) {

                    sregionString = sregionString + protectedRegion_.getId() + ", ";
                } else {

                    sregionString = sregionString + protectedRegion_.getId();
                }
            }

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_list")).replace("{list}",
                    sregionString)));

            return;
        });
    }
}
