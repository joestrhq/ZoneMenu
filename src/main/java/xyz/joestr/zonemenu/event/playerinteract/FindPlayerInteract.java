package xyz.joestr.zonemenu.event.playerinteract;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import xyz.joestr.zonemenu.ZoneMenu;
import xyz.joestr.zonemenu.util.ZoneMenuPlayer;
import xyz.joestr.zonemenu.util.ZoneMenuToolType;

/**
 * Class which handles player interaction with blocks
 * 
 * @author joestr
 * @since build_1
 * @version ${project.version}
 */
public class FindPlayerInteract implements Listener {

	private ZoneMenu plugin;

	public FindPlayerInteract(ZoneMenu zonemenu) {

		this.plugin = zonemenu;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {

		// Grab player form the event
		Player player = event.getPlayer();

		// If the player is not in the map ...
		if(!this.plugin.zoneMenuPlayers.containsKey(player)) {
			
			// .. do not proceed.
			return;
		}
		
		// Grab the ZoneMenuPlayer
		ZoneMenuPlayer zoneMenuPlayer = this.plugin.zoneMenuPlayers.get(player);
		
		// Using a stick? ToolType correct?
		if ((player.getInventory().getItemInMainHand().getType() != Material.STICK)
				|| (this.plugin.zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.FIND)) {

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
			ApplicableRegionSet regiononloc = WGBukkit.getRegionManager(player.getWorld())
					.getApplicableRegions(event.getClickedBlock().getLocation());

			// Add regions to the string
			for (ProtectedRegion region : regiononloc) {
				if (find2 != "") {
					find2 = find2 + ", ";
				}
				find2 = find2 + region.getId();
			}

			// Check if no regions were found
			if (find2 == "") {
				find1 = (String) this.plugin.configDelegate.getMap().get("event_find_no");
				// Check for multiple regions
			} else if (find2.contains(",")) {
				find1 = (String) this.plugin.configDelegate.getMap().get("event_find_multi");
				// Only one found
			} else {
				find1 = (String) this.plugin.configDelegate.getMap().get("event_find");
			}

			find1 = find1.replace("{ids}", find2);
			
			// Send player a actionbar message
			this.plugin.sendActionBarToPlayer(player, this.plugin.colorCode('&', find1));
		}
	}
}