package at.joestr.zonemenu.command.subcommand;

import org.bukkit.entity.Player;

import at.joestr.zonemenu.ZoneMenu;

public class SubCommandZoneUpdate {

    ZoneMenu zoneMenuPlugin = null;

    public SubCommandZoneUpdate(ZoneMenu zoneMenuPlugin) {

        this.zoneMenuPlugin = zoneMenuPlugin;
    }

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
