package xyz.joestr.zonemenu.command.subcommand;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                .replace("{0}", "/zone unselect"));

            return;
        }

        WorldEdit
            .getInstance()
            .getSessionManager()
            .get(BukkitAdapter.adapt(player))
            .getRegionSelector(BukkitAdapter.adapt(player.getWorld()))
            .clear();

        return;
    }
}
