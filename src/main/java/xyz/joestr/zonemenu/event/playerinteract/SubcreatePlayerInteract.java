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
public class SubcreatePlayerInteract implements Listener {

	private ZoneMenu plugin;

	public SubcreatePlayerInteract(ZoneMenu zonemenu) {

		this.plugin = zonemenu;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {

		// Grab player form the event
		Player player = event.getPlayer();

		if ((player.getInventory().getItemInMainHand().getType() != Material.STICK)
				|| (this.plugin.toolType.get(player) != ZoneMenuToolType.SIGN)
				|| (this.plugin.signType.get(player) != ZoneMenuSignType.SUBZONE)) {

			return;
		}

		// Initiliaze message
		String sign1 = "";

		// Check if event action is left-click
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			// Check if firstlocation-mao contains players name
			if (this.plugin.subcreateFirstLocations.containsKey(player)) {
				// Check if locations are the same
				if (((Location) plugin.subcreateFirstLocations.get(player))
						.equals(event.getClickedBlock().getLocation())) {
					event.setCancelled(true);
					return;
				}
			}

			// Put players world and location into maps
			this.plugin.subcreateWorlds.put(player, player.getWorld());
			this.plugin.subcreateFirstLocations.put(player, event.getClickedBlock().getLocation());

			// Cancel the event
			event.setCancelled(true);

			// Grab some values to work with
			World playerworld = (World) this.plugin.subcreateWorlds.get(player);
			Location playerpos1 = (Location) this.plugin.subcreateFirstLocations.get(player);
			Location playerpos2 = (Location) this.plugin.subcreateSecondLocations.get(player);

			// Reset old
			this.plugin.resetSubcreateCorner(player, this.plugin.subcreateCorner1);
			// Create new corner
			this.plugin.createSubcreateCroner(playerpos1, player, this.plugin.subcreateCorner1, Material.SEA_LANTERN,
					(byte) 0);

			// If all needed variables are set
			if ((playerworld != null) && (playerpos1 != null) && (playerpos2 != null)) {
				// Reset old
				this.plugin.resetSubcreateCorner(player, this.plugin.subcreateCorner2);
				// Create new corner
				this.plugin.createSubcreateCroner(playerpos2, player, this.plugin.subcreateCorner2, Material.GLOWSTONE,
						(byte) 0);
				;

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

			if (plugin.subcreateSecondLocations.containsKey(player)) {
				if (((Location) plugin.subcreateSecondLocations.get(player))
						.equals(event.getClickedBlock().getLocation())) {
					event.setCancelled(true);
					return;
				}
			}

			this.plugin.subcreateWorlds.put(player, player.getWorld());
			this.plugin.subcreateSecondLocations.put(player, event.getClickedBlock().getLocation());
			event.setCancelled(true);
			World playerworld = (World) this.plugin.subcreateWorlds.get(player);
			Location playerpos1 = (Location) this.plugin.subcreateFirstLocations.get(player);
			Location playerpos2 = (Location) this.plugin.subcreateSecondLocations.get(player);

			// Reset old
			this.plugin.resetSubcreateCorner(player, this.plugin.subcreateCorner2);
			// Create new corner
			this.plugin.createSubcreateCroner(playerpos2, player, this.plugin.subcreateCorner2, Material.GLOWSTONE,
					(byte) 0);

			if ((playerworld != null) && (playerpos1 != null) && (playerpos2 != null)) {
				// Reset old
				this.plugin.resetSubcreateCorner(player, this.plugin.subcreateCorner1);
				// Create new corner
				this.plugin.createSubcreateCroner(playerpos1, player, this.plugin.subcreateCorner1,
						Material.SEA_LANTERN, (byte) 0);

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