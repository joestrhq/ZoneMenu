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
				&& (this.plugin.tool.get(player.getName()) == "find")) {
			
			// Initiliaze message string
			String find1 = "";
			String find2 = "";
			
			// Check event action
			if ((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {

				// Check if locations contains the player
				if (plugin.findlocations.containsKey(player.getName())) {
					
					// Check if this location is equal to the stored one
					if (((Location) plugin.findlocations.get(player.getName()))
							.equals(event.getClickedBlock().getLocation())) {
						event.setCancelled(true);
						return;
					}
				}
				
				// Put player an location into a map
				this.plugin.findlocations.put(player.getName(), event.getClickedBlock().getLocation());
				
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

					find1 = (String) plugin.config.getMap().get("event_find_no");
				
				// Check for multiple regions
				} else if (find2.contains(",")) {

					find1 = (String) plugin.config.getMap().get("event_find_multi");
				
				// Only one found
				} else {

					find1 = (String) plugin.config.getMap().get("event_find");
				}
				
				// Send player a actionbar message
				plugin.sendActionBarToPlayer(player, this.plugin.colorCode("&", find1 + find2));
			}
		}

		if ((player.getInventory().getItemInMainHand().getType() == Material.STICK)
				&& (this.plugin.tool.get(player.getName()) == "sign")) {
			
			// Initiliaze message
			String sign1 = "";
			
			// Check if event action is left-click
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				
				// Check if firstlocation-mao contains players name
				if (plugin.firstlocations.containsKey(player.getName())) {
					
					// Check if locations are the same
					if (((Location) plugin.firstlocations.get(player.getName()))
							.equals(event.getClickedBlock().getLocation())) {
						event.setCancelled(true);
						return;
					}
				}
				
				// Put players world and location into maps
				this.plugin.worlds.put(player.getName(), player.getWorld());
				this.plugin.firstlocations.put(player.getName(), event.getClickedBlock().getLocation());
				
				// Cancel the event
				event.setCancelled(true);
				
				// Grab some values to work with
				World playerworld = (World) this.plugin.worlds.get(player.getName());
				Location playerpos1 = (Location) this.plugin.firstlocations.get(player.getName());
				Location playerpos2 = (Location) this.plugin.secondlocations.get(player.getName());
				playerpos1.setY(0);
				
				// Reset old beacon
				this.plugin.resetCorner(player, this.plugin.corner1);
				// Create new beacon
				this.plugin.createBeaconCorner(playerpos1, player, this.plugin.corner1, (byte) 10);
				// Set block with a slight delay
				this.plugin.laterSet(event.getClickedBlock().getLocation(), player, Material.STAINED_GLASS, (byte) 10);

				// If all needed variables are set
				if ((playerworld != null) && (playerpos1 != null) && (playerpos2 != null)) {
					
					// Reset beacons and create new ones
					this.plugin.resetCorner(player, this.plugin.corner2);
					this.plugin.createBeaconCorner(playerpos2, player, this.plugin.corner2, (byte) 2);
					Location loc = playerpos1.clone();
					loc.setX(playerpos2.getX());
					this.plugin.resetCorner(player, this.plugin.corner3);
					this.plugin.createBeaconCorner(loc, player, this.plugin.corner3, (byte) 0);
					loc = playerpos1.clone();
					loc.setZ(playerpos2.getZ());
					this.plugin.resetCorner(player, this.plugin.corner4);
					this.plugin.createBeaconCorner(loc, player, this.plugin.corner4, (byte) 0);
					
					// Make a worldedit selection
					CuboidSelection cs = new CuboidSelection(playerworld, playerpos1, playerpos2);
					this.plugin.getWorldEdit().setSelection(player, cs);
					
					// Set actionbar message
					sign1 = (String) plugin.config.getMap().get("event_sign_first")
							+ (String) ((String) plugin.config.getMap().get("event_sign_area")).replace("{0}",
									Integer.toString(this.plugin.getWorldEdit().getSelection(player).getLength()
											* this.plugin.getWorldEdit().getSelection(player).getWidth()));
				} else {
					
					// Set actionbar message
					sign1 = (String) plugin.config.getMap().get("event_sign_first");
				}
				
				// Send actiobar message to the player
				plugin.sendActionBarToPlayer(player, this.plugin.colorCode("&", sign1));
			} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				
				// Exact same behaviour as above, except
				// fistlocations <-> secondlocations
				
				if (plugin.secondlocations.containsKey(player.getName())) {

					if (((Location) plugin.secondlocations.get(player.getName()))
							.equals(event.getClickedBlock().getLocation())) {
						event.setCancelled(true);
						return;
					}
				}

				this.plugin.worlds.put(player.getName(), player.getWorld());
				this.plugin.secondlocations.put(player.getName(), event.getClickedBlock().getLocation());
				event.setCancelled(true);
				World playerworld = (World) this.plugin.worlds.get(player.getName());
				Location playerpos1 = (Location) this.plugin.firstlocations.get(player.getName());
				Location playerpos2 = (Location) this.plugin.secondlocations.get(player.getName());
				playerpos2.setY(255);
				// playerpos2.setY(player.getWorld().getMaxHeight());

				this.plugin.resetCorner(player, this.plugin.corner2);
				this.plugin.createBeaconCorner(playerpos2, player, this.plugin.corner2, (byte) 2);
				this.plugin.laterSet(event.getClickedBlock().getLocation(), player, Material.STAINED_GLASS, (byte) 2);

				if ((playerworld != null) && (playerpos1 != null) && (playerpos2 != null)) {

					this.plugin.resetCorner(player, this.plugin.corner1);
					this.plugin.createBeaconCorner(playerpos1, player, this.plugin.corner1, (byte) 10);
					Location loc = playerpos1.clone();
					loc.setX(playerpos2.getX());
					this.plugin.resetCorner(player, this.plugin.corner3);
					this.plugin.createBeaconCorner(loc, player, this.plugin.corner3, (byte) 0);
					loc = playerpos1.clone();
					loc.setZ(playerpos2.getZ());
					this.plugin.resetCorner(player, this.plugin.corner4);
					this.plugin.createBeaconCorner(loc, player, this.plugin.corner4, (byte) 0);

					CuboidSelection cs = new CuboidSelection(playerworld, playerpos1, playerpos2);
					this.plugin.getWorldEdit().setSelection(player, cs);
					sign1 = (String) plugin.config.getMap().get("event_sign_second")
							+ (String) ((String) plugin.config.getMap().get("event_sign_area")).replace("{0}",
									Integer.toString(this.plugin.getWorldEdit().getSelection(player).getLength()
											* this.plugin.getWorldEdit().getSelection(player).getWidth()));
				} else {

					sign1 = (String) plugin.config.getMap().get("event_sign_second");
				}

				plugin.sendActionBarToPlayer(player, this.plugin.colorCode("&", sign1));
			}
		}
	}
}