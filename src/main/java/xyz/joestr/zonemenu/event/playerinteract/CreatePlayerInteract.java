package xyz.joestr.zonemenu.event.playerinteract;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import xyz.joestr.zonemenu.ZoneMenu;
import xyz.joestr.zonemenu.enumeration.ZoneMenuSignType;
import xyz.joestr.zonemenu.enumeration.ZoneMenuToolType;

/**
 * Class which handles player interaction with blocks
 * 
 * @author joestr
 * @since build_1
 * @version ${project.version}
 */
public class CreatePlayerInteract implements Listener {

	private ZoneMenu plugin;

	public CreatePlayerInteract(ZoneMenu zonemenu) {

		this.plugin = zonemenu;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {

		// Grab player form the event
		Player player = event.getPlayer();

		if ((player.getInventory().getItemInMainHand().getType() != Material.STICK)
				|| (this.plugin.toolType.get(player) != ZoneMenuToolType.SIGN)
				|| (this.plugin.signType.get(player) != ZoneMenuSignType.ZONE)) {

			return;
		}

		// Initiliaze message
		String sign1 = "";

		// Check if event action is left-click
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			// Check if firstlocation-map contains players name
			if (this.plugin.createFirstLocations.containsKey(player)) {
				// Check if locations are the same
				if (((Location) plugin.createFirstLocations.get(player))
						.equals(event.getClickedBlock().getLocation())) {
					event.setCancelled(true);
					return;
				}
			}

			// Put players world and location into maps
			this.plugin.createWorlds.put(player, player.getWorld());
			this.plugin.createFirstLocations.put(player, event.getClickedBlock().getLocation());

			// Cancel the event
			event.setCancelled(true);

			// Grab some values to work with
			World playerworld = (World) this.plugin.createWorlds.get(player);
			Location playerpos1 = (Location) this.plugin.createFirstLocations.get(player);
			Location playerpos2 = (Location) this.plugin.createSecondLocations.get(player);
			playerpos1.setY(0);

			// Reset old beacon
			this.plugin.resetBeaconCorner(player, this.plugin.createCorner1);
			// Create new beacon
			this.plugin.createBeaconCorner(playerpos1, player, this.plugin.createCorner1, (byte) 10);

			// If all needed variables are set
			if ((playerworld != null) && (playerpos1 != null) && (playerpos2 != null)) {
				// Reset beacons and create new ones
				this.plugin.resetBeaconCorner(player, this.plugin.createCorner2);
				this.plugin.createBeaconCorner(playerpos2, player, this.plugin.createCorner2, (byte) 2);
				Location loc = playerpos1.clone();
				loc.setX(playerpos2.getX());
				this.plugin.resetBeaconCorner(player, this.plugin.createCorner3);
				this.plugin.createBeaconCorner(loc, player, this.plugin.createCorner3, (byte) 0);
				loc = playerpos1.clone();
				loc.setZ(playerpos2.getZ());
				this.plugin.resetBeaconCorner(player, this.plugin.createCorner4);
				this.plugin.createBeaconCorner(loc, player, this.plugin.createCorner4, (byte) 0);

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
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			// Exact same behaviour as above, except
			// fistlocations <-> secondlocations

			if (plugin.createSecondLocations.containsKey(player)) {
				if (((Location) plugin.createSecondLocations.get(player))
						.equals(event.getClickedBlock().getLocation())) {
					event.setCancelled(true);
					return;
				}
			}

			this.plugin.createWorlds.put(player, player.getWorld());
			this.plugin.createSecondLocations.put(player, event.getClickedBlock().getLocation());
			event.setCancelled(true);
			World playerworld = (World) this.plugin.createWorlds.get(player);
			Location playerpos1 = (Location) this.plugin.createFirstLocations.get(player);
			Location playerpos2 = (Location) this.plugin.createSecondLocations.get(player);
			playerpos2.setY(255);
			// playerpos2.setY(player.getWorld().getMaxHeight());

			this.plugin.resetBeaconCorner(player, this.plugin.createCorner2);
			this.plugin.createBeaconCorner(playerpos2, player, this.plugin.createCorner2, (byte) 2);

			if ((playerworld != null) && (playerpos1 != null) && (playerpos2 != null)) {
				this.plugin.resetBeaconCorner(player, this.plugin.createCorner1);
				this.plugin.createBeaconCorner(playerpos1, player, this.plugin.createCorner1, (byte) 10);
				Location loc = playerpos1.clone();
				loc.setX(playerpos2.getX());
				this.plugin.resetBeaconCorner(player, this.plugin.createCorner3);
				this.plugin.createBeaconCorner(loc, player, this.plugin.createCorner3, (byte) 0);
				loc = playerpos1.clone();
				loc.setZ(playerpos2.getZ());
				this.plugin.resetBeaconCorner(player, this.plugin.createCorner4);
				this.plugin.createBeaconCorner(loc, player, this.plugin.createCorner4, (byte) 0);
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