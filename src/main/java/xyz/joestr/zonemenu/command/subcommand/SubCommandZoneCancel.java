package xyz.joestr.zonemenu.command.subcommand;

import com.sk89q.worldedit.IncompleteRegionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

import xyz.joestr.zonemenu.ZoneMenu;

public class SubCommandZoneCancel {

    ZoneMenu zoneMenuPlugin = null;

    public SubCommandZoneCancel(ZoneMenu zoneMenuPlugin) {

        this.zoneMenuPlugin = zoneMenuPlugin;
    }

    public void process(Player player, String[] arguments) {

        // If arguments' length does not equals 1 ...
        if (arguments.length != 1) {

            // ... wrong usage of "/zone cancel".
            // Send the player a message.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message")).replace("{0}",
                    "/zone cancel")));

            return;
        }

        // If player is not in the map ...
        if (!this.zoneMenuPlugin.zoneMenuPlayers.containsKey(player)) {

            // ... zone creation is not running.
            // Send the player a message.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_cancel_not_running"))));

            return;
        }

        // If the ToolType equals null ...
        if (this.zoneMenuPlugin.zoneMenuPlayers.get(player).getToolType() == null) {

            // ... zone creation is not running.
            // Send the player a message.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_cancel_not_running"))));

            return;
        }

        // Clear up player.
        this.zoneMenuPlugin.clearUpZoneMenuPlayer(player);

        // Send the player a message
        player.sendMessage(
            this.zoneMenuPlugin.colorCode('&', ((String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix"))
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_cancel"))));
    }
}
