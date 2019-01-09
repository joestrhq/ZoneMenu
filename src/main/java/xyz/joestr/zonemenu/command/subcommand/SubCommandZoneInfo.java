package xyz.joestr.zonemenu.command.subcommand;

import com.sk89q.worldguard.WorldGuard;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import xyz.joestr.zonemenu.ZoneMenu;

/**
 * Class which handles subcommand "info" of command "zone".
 *
 * @author joestr
 * @since ${project.version}
 * @version ${project.version}
 */
public class SubCommandZoneInfo {

    ZoneMenu zoneMenuPlugin = null;

    /**
     * Constrcutor for the null null null null null null null null     {@link xyz.joestr.zonemenu.command.subcommand.SubCommandZoneInfo
     * SubCommandZoneInfo} class.
     *
     * @param zoneMenuPlugin A {@link xyz.joestr.zonemenu.ZoneMenu ZoneMenu}.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    public SubCommandZoneInfo(ZoneMenu zoneMenuPlugin) {

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

        // If arguments' length does not equals 3 ...
        if (arguments.length != 2) {

            // ... wrong usage of "/zone info <Zone>".
            // Send the player a message.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message")).replace("{0}",
                    "/zone info <Zone>")));

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

            // Initialise new region with null
            ProtectedRegion protectedRegion = null;

            // Loop through all regions ...
            for (ProtectedRegion protectedRegion_ : t) {

                // ... and if the region ID equals the second argument (<Zone>) ...
                if (protectedRegion_.getId().replace("+", "#").replace("-", ".").equalsIgnoreCase(arguments[1])) {

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

            // Grab some infos
            DefaultDomain domainOwners = protectedRegion.getOwners();
            DefaultDomain domainMembers = protectedRegion.getMembers();
            int minimumX = protectedRegion.getMinimumPoint().getBlockX();
            int minimumZ = protectedRegion.getMinimumPoint().getBlockZ();
            int maximumX = protectedRegion.getMaximumPoint().getBlockX();
            int maximumZ = protectedRegion.getMaximumPoint().getBlockZ();
            int area = (this.zoneMenuPlugin.difference(minimumX, maximumX) + 1)
                * (this.zoneMenuPlugin.difference(minimumZ, maximumZ) + 1);

            // Send infos to the player.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_id")).replace("{id}",
                    protectedRegion.getId())));

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_priority")
                + protectedRegion.getPriority()));

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_parent")
                + (protectedRegion.getParent() == null ? "" : protectedRegion.getParent().getId())));

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_owners")
                + domainOwners
                    .toPlayersString(WorldGuard.getInstance().getProfileCache()).replace("*", "")));

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_members")
                + domainMembers
                    .toPlayersString(WorldGuard.getInstance().getProfileCache()).replace("*", "")));

            Iterator<Entry<Flag<?>, Object>> iterator = protectedRegion.getFlags().entrySet().iterator();

            while (iterator.hasNext()) {

                Entry<Flag<?>, Object> entry_ = iterator.next();

                player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_flag"))
                        .replace("{0}", entry_.getKey().getName())
                        .replace("{1}", entry_.getValue().toString())));
            }

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_start"))
                    .replace("{0}", Integer.toString(minimumX)).replace("{1}", Integer.toString(minimumZ))));

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_end"))
                    .replace("{0}", Integer.toString(maximumX)).replace("{1}", Integer.toString(maximumZ))));

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_info_area")).replace("{0}",
                    Integer.toString(area))));
        });
    }
}
