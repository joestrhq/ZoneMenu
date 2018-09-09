package xyz.joestr.zonemenu.command.subcommand;

import org.bukkit.entity.Player;

import xyz.joestr.zonemenu.ZoneMenu;

public class SubCommandZoneUnselect {

    ZoneMenu zoneMenuPlugin = null;

    public SubCommandZoneUnselect(ZoneMenu zoneMenuPlugin) {
        this.zoneMenuPlugin = zoneMenuPlugin;
    }

    public void process(Player player, String[] args) {

        if (args.length != 1) {

            // Wrong usage of the "/zone unselect" command
            player.sendMessage(this.zoneMenuPlugin
                    .colorCode('&', (String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message"))
                    .replace("{0}", "/zone list"));

            return;
        }

        if (this.zoneMenuPlugin.worldEditPlugin.getSelection(player) != null) {

            this.zoneMenuPlugin.worldEditPlugin.getSelection(player).getRegionSelector().clear();
        }

        return;
    }
}
