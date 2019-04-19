package xyz.joestr.zonemenu.command.subcommand;

import org.bukkit.entity.Player;

import xyz.joestr.zonemenu.ZoneMenu;

/**
 * Class which handles subcommand "info" of command "zone".
 *
 * @author joestr
 * @since ${project.version}
 * @version ${project.version}
 */
public class SubCommandZoneUpdate {

    ZoneMenu zoneMenuPlugin = null;

    /**
     * Constrcutor for the {@link xyz.joestr.zonemenu.command.subcommand.SubCommandZoneUpdate
     * SubCommandZoneInfo} class.
     *
     * @param zoneMenuPlugin A {@link xyz.joestr.zonemenu.ZoneMenu ZoneMenu}.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    public SubCommandZoneUpdate(ZoneMenu zoneMenuPlugin) {

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
        if (arguments.length != 1) {

            // ... wrong usage of "/zone info <Zone>".
            // Send the player a message.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                ((String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message")).replace("{0}",
                    "/zone update")));

            return;
        }

        this.zoneMenuPlugin.updater.asyncIsUpdateAvailable(
            (Boolean result, Throwable exception) -> {

                if (exception != null) {

                    // Send the player a message.
                    player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("update_error"))));

                    return;
                }

                if (result) {

                    // Send the player a message.
                    player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("update_available")).replace("{0}",
                            this.zoneMenuPlugin.updater.getUpdateURI())));

                    return;
                } else {

                    // Send the player a message.
                    player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                        ((String) this.zoneMenuPlugin.configDelegate.getMap().get("no_update_available"))));

                    return;
                }
            }
        );
    }
}
