package xyz.joestr.zonemenu.command.subcommand;

import org.bukkit.entity.Player;

import xyz.joestr.zonemenu.ZoneMenu;

/**
 * Class which handles subcommand "cancel" of command "zone".
 * 
 * @author joestr
 * @since ${project.version}
 * @version ${project.version}
 */
public class SubCommandZoneCancel {

    ZoneMenu zoneMenuPlugin = null;

    /**
     * Constrcutor for the
     * {@link xyz.joestr.zonemenu.command.subcommand.SubCommandZoneCancel
     * SubCommandZoneCancel} class.
     * 
     * @param zoneMenuPlugin
     *            A {@link xyz.joestr.zonemenu.ZoneMenu ZoneMenu}.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    public SubCommandZoneCancel(ZoneMenu zoneMenuPlugin) {

        this.zoneMenuPlugin = zoneMenuPlugin;
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
