package xyz.joestr.zonemenu.event;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import xyz.joestr.zonemenu.ZoneMenu;
import xyz.joestr.zonemenu.enumeration.ZoneMenuTool;

public class PlayerInteract implements Listener {

	private ZoneMenu plugin;

	public PlayerInteract(ZoneMenu zonemenu) {

		this.plugin = zonemenu;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		
		// Grab player form the event
		Player player = event.getPlayer();

		// Check if item in main Hand is a stick
		if ((player.getInventory().getItemInMainHand().getType() == Material.STICK)
				&& (this.plugin.tool.get(player) == ZoneMenuTool.FIND)) {
			// Initiliaze message string
			String find1 = "";
			String find2 = "";
			
			// Check event action
			if ((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
				// Check if locations contains the player
				if (plugin.findLocations.containsKey(player)) {
					// Check if this location is equal to the stored one
					if (((Location) plugin.findLocations.get(player))
							.equals(event.getClickedBlock().getLocation())) {
						event.setCancelled(true);
						return;
					}
				}
				
				// Put player an location into a map
				this.plugin.findLocations.put(player, event.getClickedBlock().getLocation());
				
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
				
				// Send player a actionbar message
				this.plugin.sendActionBarToPlayer(player, this.plugin.colorCode('&', find1 + find2));
			}
		}

		if ((player.getInventory().getItemInMainHand().getType() == Material.STICK)
				&& (this.plugin.tool.get(player) == ZoneMenuTool.SIGN)) {
			
			// Initiliaze message
			String sign1 = "";
			
			// Check if event action is left-click
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				// Check if firstlocation-mao contains players name
				if (this.plugin.selectedFirstLocations.containsKey(player)) {
					// Check if locations are the same
					if (((Location) plugin.selectedFirstLocations.get(player))
							.equals(event.getClickedBlock().getLocation())) {
						event.setCancelled(true);
						return;
					}
				}
				
				// Put players world and location into maps
				this.plugin.worlds.put(player, player.getWorld());
				this.plugin.selectedFirstLocations.put(player, event.getClickedBlock().getLocation());
				
				// Cancel the event
				event.setCancelled(true);
				
				// Grab some values to work with
				World playerworld = (World) this.plugin.worlds.get(player);
				Location playerpos1 = (Location) this.plugin.selectedFirstLocations.get(player);
				Location playerpos2 = (Location) this.plugin.selectedSecondLocations.get(player);
				playerpos1.setY(0);
				
				// Reset old beacon
				this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner1);
				// Create new beacon
				this.plugin.createBeaconCorner(playerpos1, player, this.plugin.beaconCorner1, (byte) 10);

				// If all needed variables are set
				if ((playerworld != null) && (playerpos1 != null) && (playerpos2 != null)) {
					// Reset beacons and create new ones
					this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner2);
					this.plugin.createBeaconCorner(playerpos2, player, this.plugin.beaconCorner2, (byte) 2);
					Location loc = playerpos1.clone();
					loc.setX(playerpos2.getX());
					this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner3);
					this.plugin.createBeaconCorner(loc, player, this.plugin.beaconCorner3, (byte) 0);
					loc = playerpos1.clone();
					loc.setZ(playerpos2.getZ());
					this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner4);
					this.plugin.createBeaconCorner(loc, player, this.plugin.beaconCorner4, (byte) 0);
					
					// Make a worldedit selection
					CuboidSelection cs = new CuboidSelection(playerworld, playerpos1, playerpos2);
					this.plugin.getWorldEditPlugin().setSelection(player, cs);
					
					// Set actionbar message
					sign1 = (String) plugin.configDelegate.getMap().get("event_sign_first")
							+ (String) ((String) plugin.configDelegate.getMap().get("event_sign_area")).replace("{0}",
									Integer.toString(this.plugin.getWorldEditPlugin().getSelection(player).getLength()
											* this.plugin.getWorldEditPlugin().getSelection(player).getWidth()));
				} else {
					// Set actionbar message
					sign1 = (String) plugin.configDelegate.getMap().get("event_sign_first");
				}
				
				// Send actiobar message to the player
				plugin.sendActionBarToPlayer(player, this.plugin.colorCode('&', sign1));
			} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				
				// Exact same behaviour as above, except
				// fistlocations <-> secondlocations
				
				if (plugin.selectedSecondLocations.containsKey(player)) {
					if (((Location) plugin.selectedSecondLocations.get(player))
							.equals(event.getClickedBlock().getLocation())) {
						event.setCancelled(true);
						return;
					}
				}

				this.plugin.worlds.put(player, player.getWorld());
				this.plugin.selectedSecondLocations.put(player, event.getClickedBlock().getLocation());
				event.setCancelled(true);
				World playerworld = (World) this.plugin.worlds.get(player);
				Location playerpos1 = (Location) this.plugin.selectedFirstLocations.get(player);
				Location playerpos2 = (Location) this.plugin.selectedSecondLocations.get(player);
				playerpos2.setY(255);
				// playerpos2.setY(player.getWorld().getMaxHeight());
				this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner2);
				this.plugin.createBeaconCorner(playerpos2, player, this.plugin.beaconCorner2, (byte) 2);

				if ((playerworld != null) && (playerpos1 != null) && (playerpos2 != null)) {
					this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner1);
					this.plugin.createBeaconCorner(playerpos1, player, this.plugin.beaconCorner1, (byte) 10);
					Location loc = playerpos1.clone();
					loc.setX(playerpos2.getX());
					this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner3);
					this.plugin.createBeaconCorner(loc, player, this.plugin.beaconCorner3, (byte) 0);
					loc = playerpos1.clone();
					loc.setZ(playerpos2.getZ());
					this.plugin.resetBeaconCorner(player, this.plugin.beaconCorner4);
					this.plugin.createBeaconCorner(loc, player, this.plugin.beaconCorner4, (byte) 0);
					CuboidSelection cs = new CuboidSelection(playerworld, playerpos1, playerpos2);
					this.plugin.getWorldEditPlugin().setSelection(player, cs);
					sign1 = (String) plugin.configDelegate.getMap().get("event_sign_second")
							+ (String) ((String) plugin.configDelegate.getMap().get("event_sign_area")).replace("{0}",
									Integer.toString(this.plugin.getWorldEditPlugin().getSelection(player).getLength()
											* this.plugin.getWorldEditPlugin().getSelection(player).getWidth()));
				} else {

					sign1 = (String) plugin.configDelegate.getMap().get("event_sign_second");
				}

				plugin.sendActionBarToPlayer(player, this.plugin.colorCode('&', sign1));
			}
		}
	}
}