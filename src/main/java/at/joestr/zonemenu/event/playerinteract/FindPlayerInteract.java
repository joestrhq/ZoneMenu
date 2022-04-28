package at.joestr.zonemenu.event.playerinteract;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import at.joestr.zonemenu.ZoneMenu;
import at.joestr.zonemenu.util.ZoneMenuPlayer;
import at.joestr.zonemenu.util.ZoneMenuToolType;

/**
 * Class which handles player interaction with blocks
 *
 * @author joestr
 * @since build_1
 * @version ${project.version}
 */
public class FindPlayerInteract implements Listener {

    private ZoneMenu zoneMenuPlugin;

    public FindPlayerInteract(ZoneMenu zonemenu) {

        this.zoneMenuPlugin = zonemenu;
        this.zoneMenuPlugin.getServer().getPluginManager().registerEvents(this, this.zoneMenuPlugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        // Grab player form the event
        Player player = event.getPlayer();

        // If the player is not in the map ...
        if (!this.zoneMenuPlugin.zoneMenuPlayers.containsKey(player)) {

            // .. do not proceed.
            return;
        }

        // Grab the ZoneMenuPlayer
        ZoneMenuPlayer zoneMenuPlayer = this.zoneMenuPlugin.zoneMenuPlayers.get(player);

        // Using a stick? ToolType correct?
        if ((player.getInventory().getItemInMainHand().getType() != Material.STICK)
            || (this.zoneMenuPlugin.zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.FIND)) {

            return;
        }

        // Initiliaze message string
        String find1 = "";
        String find2 = "";

        // Check event action
        if ((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            // Check if this location is equal to the stored one
            if (event.getClickedBlock().getLocation().equals(zoneMenuPlayer.getFindLocation())) {
                event.setCancelled(true);
                return;
            }

            // Put player an location into a map
            zoneMenuPlayer.setFindLocation(event.getClickedBlock().getLocation());

            // Cancel the event
            event.setCancelled(true);

            // Get regions on clicked location
            ApplicableRegionSet regiononloc = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()))
                .getApplicableRegions(
                    BlockVector3.at(
                        event.getClickedBlock().getLocation().getBlockX(),
                        event.getClickedBlock().getLocation().getBlockY(),
                        event.getClickedBlock().getLocation().getBlockZ()
                    )
                );

            // Add regions to the string
            for (ProtectedRegion region : regiononloc) {
                if (find2 != "") {
                    find2 = find2 + ", ";
                }
                find2 = find2 + region.getId().replace("+", "#").replace("-", ".");
            }

            // Check if no regions were found
            if (find2 == "") {
                find1 = (String) this.zoneMenuPlugin.configDelegate.getMap().get("event_find_no");
                // Check for multiple regions
            } else if (find2.contains(",")) {
                find1 = (String) this.zoneMenuPlugin.configDelegate.getMap().get("event_find_multi");
                // Only one found
            } else {
                find1 = (String) this.zoneMenuPlugin.configDelegate.getMap().get("event_find");
            }

            find1 = find1.replace("{ids}", find2);

            // Send player a actionbar message
            this.zoneMenuPlugin.sendActionBarToPlayer(player, this.zoneMenuPlugin.colorCode('&', find1));
        }
    }
}
