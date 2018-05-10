package xyz.joestr.zonemenu.command.subcommand;

import java.io.IOException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;

import xyz.joestr.zonemenu.ZoneMenu;

public class SubCommandZoneCreate {

	ZoneMenu plugin = null;

	public SubCommandZoneCreate(ZoneMenu plugin) {
		this.plugin = plugin;
	}

	public void process(Player player) {

		plugin.futuristicRegionProcessing(player, true, (List<ProtectedRegion> t, Throwable u) -> {
			
			// Initialise new region
			ProtectedRegion protectedregion = null;
			
			if (!t.isEmpty()) {
				protectedregion = t.get(0);
			}
			
			if (protectedregion != null) {

				player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
				player.sendMessage(
						plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_create_already_have")));

				return;
			}

			// Grab players worldedit selection
			Selection selectedregion = plugin.worldEditPlugin.getSelection(player);

			// Check if selection is valid
			if (selectedregion == null) {

				player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
				player.sendMessage(
						plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_create_not_signed")));

				return;
			}

			// Check if selected area is smaller than the specified maximum area in the
			// config file
			if (selectedregion.getWidth() * selectedregion.getLength() < Integer
					.parseInt(plugin.configDelegate.getMap().get("zone_create_area_min").toString())) {
				player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
				player.sendMessage(
						plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_create_area_under"))
								.replace("{0}", plugin.configDelegate.getMap().get("zone_create_area_min").toString()));
				return;
			}

			// Check if selected area is larger than the specified minimum area in the
			// config file
			if (selectedregion.getWidth() * selectedregion.getLength() > Integer
					.parseInt(plugin.configDelegate.getMap().get("zone_create_area_max").toString())) {

				player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
				player.sendMessage(
						plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_create_area_over"))
								.replace("{0}", (String) plugin.configDelegate.getMap().get("zone_create_area_max")));
				return;
			}

			// Grab some values to work with
			Location min = selectedregion.getMinimumPoint();
			Location max = selectedregion.getMaximumPoint();
			double first_x = min.getX();
			double first_y = min.getY();
			double first_z = min.getZ();
			double second_x = max.getX();
			double second_y = max.getY();
			double second_z = max.getZ();

			// Create a new WorldGuard region
			ProtectedCuboidRegion protectedcuboidregion = new ProtectedCuboidRegion(
					(String) plugin.idDelegate.getMap().get("zone_id")
							+ plugin.idDelegate.getMap().get("zone_id_counter").toString(),
					new BlockVector(first_x, first_y, first_z), new BlockVector(second_x, second_y, second_z));

			// Increment the region id counter
			plugin.idDelegate.getMap().put("zone_id_counter",
					Integer.parseInt(plugin.idDelegate.getMap().get("zone_id_counter").toString()) + 1);
			plugin.idDelegate.Save();

			// Check if region overlaps with unowned regions
			if (plugin.worldGuardPlugin.getRegionManager(player.getWorld()).overlapsUnownedRegion(protectedcuboidregion,
					plugin.worldGuardPlugin.wrapPlayer(player))) {

				player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
				player.sendMessage(plugin.colorCode('&',
						(String) plugin.configDelegate.getMap().get("zone_create_overlaps_unowned")));
				return;
			}

			// Check if Worldguards profileservice contains players name
			ProfileService ps = plugin.worldGuardPlugin.getProfileService();
			try {
				ps.findByName(player.getName());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Create a new domain
			DefaultDomain domain = new DefaultDomain();
			// Wrap player and add it to the domain
			domain.addPlayer(plugin.worldGuardPlugin.wrapPlayer(player));
			// Apply the domain to owners
			protectedcuboidregion.setOwners(domain);
			// Set the priority to the specified value in the config file
			protectedcuboidregion
					.setPriority(Integer.parseInt(plugin.configDelegate.getMap().get("zone_create_priority").toString()));

			// Some flags
			/*
			 * ProtectRegion.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
			 * ProtectRegion.setFlag(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE,
			 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.TNT,
			 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.FIRE_SPREAD,
			 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.OTHER_EXPLOSION,
			 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.ENDER_BUILD,
			 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.GHAST_FIREBALL,
			 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.LAVA_FIRE,
			 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.PVP,
			 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.MOB_DAMAGE,
			 * StateFlag.State.DENY); ProtectRegion.setFlag(DefaultFlag.MOB_SPAWNING,
			 * StateFlag.State.DENY);
			 */

			// Finally, add the region to worlds region manager
			plugin.worldGuardPlugin.getRegionManager(player.getWorld()).addRegion(protectedcuboidregion);

			// Send player a message
			player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("head")));
			player.sendMessage(plugin.colorCode('&', (String) plugin.configDelegate.getMap().get("zone_create")));

			// Clean up user
			plugin.tool.remove(player);
			plugin.findLocations.remove(player);
			plugin.worlds.remove(player);
			plugin.selectedFirstLocations.remove(player);
			plugin.selectedSecondLocations.remove(player);
			plugin.worldEditPlugin.getSession(player)
					.getRegionSelector(plugin.worldEditPlugin.getSession(player).getSelectionWorld()).clear();

			// Reset Beacons
			plugin.resetBeaconCorner(player, plugin.beaconCorner1);
			plugin.resetBeaconCorner(player, plugin.beaconCorner2);
			plugin.resetBeaconCorner(player, plugin.beaconCorner3);
			plugin.resetBeaconCorner(player, plugin.beaconCorner4);
		});
	}
}
