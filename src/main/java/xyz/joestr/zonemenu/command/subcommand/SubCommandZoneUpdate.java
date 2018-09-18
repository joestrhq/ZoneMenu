package xyz.joestr.zonemenu.command.subcommand;

import java.io.IOException;
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
public class SubCommandZoneUpdate {

    ZoneMenu zoneMenuPlugin = null;

    /**
     * Constrcutor for the
     * {@link xyz.joestr.zonemenu.command.subcommand.SubCommandZoneUpdate
     * SubCommandZoneInfo} class.
     * 
     * @param zoneMenuPlugin
     *            A {@link xyz.joestr.zonemenu.ZoneMenu ZoneMenu}.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    public SubCommandZoneUpdate(ZoneMenu plugin) {

        this.zoneMenuPlugin = plugin;
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

        // If arguments' length does not equals 3 ...
        if (arguments.length != 2) {

            // ... wrong usage of "/zone info <Zone>".

            // Send the player a message.
            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                    ((String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message")).replace("{0}",
                            "/zone update")));

            return;
        }

        boolean isUpdateAvailable = false;
        
        try {
            
            isUpdateAvailable = this.zoneMenuPlugin.updater.isUpdateAvailable();
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        
        if(isUpdateAvailable) {
            
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
}
