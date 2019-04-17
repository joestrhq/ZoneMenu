package xyz.joestr.zonemenu.command.subcommand;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import xyz.joestr.zonemenu.ZoneMenu;

/**
 * Class which handles subcommand "list" of command "zone".
 *
 * @author joestr
 * @since ${project.version}
 * @version ${project.version}
 */
public class SubCommandZoneList {

    ZoneMenu zoneMenuPlugin = null;

    /**
     * Constrcutor for the null     {@link xyz.joestr.zonemenu.command.subcommand.SubCommandZoneList
     * SubCommandZoneList} class.
     *
     * @param zoneMenuPlugin A {@link xyz.joestr.zonemenu.ZoneMenu ZoneMenu}.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    public SubCommandZoneList(ZoneMenu plugin) {

        this.zoneMenuPlugin = plugin;
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
