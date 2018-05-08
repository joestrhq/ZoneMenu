package xyz.joestr.zonemenu;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.joestr.zonemenu.util.YMLDelegate;
import xyz.joestr.zonemenu.command.ZoneCommand;
import xyz.joestr.zonemenu.enumeration.ZoneMenuTool;
import xyz.joestr.zonemenu.event.PlayerChangedWorld;
import xyz.joestr.zonemenu.event.PlayerQuit;
import xyz.joestr.zonemenu.event.PlayerInteract;
import xyz.joestr.zonemenu.tabcomplete.ZoneTabComplete;

/**
 * ZoneMenu class
 * @author Joel
 * @since build_1
 * @version build_7_pre_2
 */
public class ZoneMenu extends JavaPlugin implements Listener {

	// This represents the config file
	public YMLDelegate configDelegate = new YMLDelegate(this, "config", "config.yml");
	
	// This files contains the entries for the region IDs
	public YMLDelegate idDelegate = new YMLDelegate(this, "id", "id.yml");
	
	// This list contains the two delegates above (for iterating)
	public List<YMLDelegate> ymlDelegates = new ArrayList<>();

	// WorldEdit
	public WorldEditPlugin worldEditPlugin;
	
	// WorldGuard
	public WorldGuardPlugin worldGuardPlugin;

	// Map which contains players and a locations
	public Map<Player, Location> findLocations = new HashMap<>();
	
	// Map which contains players and worlds
	public Map<Player, World> worlds = new HashMap<>();
	
	// Map which contains players and their first selected locations
	public Map<Player, Location> selectedFirstLocations = new HashMap<>();
	
	// Map which contains players and their second selected lcoations
	public Map<Player, Location> selectedSecondLocations = new HashMap<>();
	
	// Map which cotnains players and their currently bound tools
	public Map<Player, ZoneMenuTool> tool = new HashMap<>();
	
	// Map which contains players and the locations where a beacon is shown to them
	public Map<Player, Location> beaconCorner1 = new HashMap<>();
	public Map<Player, Location> beaconCorner2 = new HashMap<>();
	public Map<Player, Location> beaconCorner3 = new HashMap<>();
	public Map<Player, Location> beaconCorner4 = new HashMap<>();
	
	/**
	 * Plugin starts.
	 * @author joestr
	 * @since build_1
	 * @version build_7_pre_2
	 */
	public void onEnable() {
		
		Bukkit.getServer().getConsoleSender()
				.sendMessage("[ZoneMenu] Bukkit Version: " + Bukkit.getServer().getBukkitVersion());

		// Add the delegates to the list
		ymlDelegates.add(configDelegate);
		ymlDelegates.add(idDelegate);

		// Check the delegates for existence,
		// if not create them.
		// Finally load them.
		for (YMLDelegate yd : ymlDelegates) {
			if (!yd.Exist()) { yd.Create(); }
			yd.Load();
		}

		// Checl the delegaes for their entries.
		// If they lack some entries,
		// add the missing ones and save the file.
		for (YMLDelegate yd : ymlDelegates) {
			if (!yd.EntryCheck()) { yd.Save(); }
		}
		
		// Register command /zone
		this.getCommand("zone").setExecutor(new ZoneCommand(this));
		this.getCommand("zone").setTabCompleter(new ZoneTabComplete(this));
		
		// Register the events
		new PlayerInteract(this);
		new PlayerQuit(this);
		new PlayerChangedWorld(this);
		
		// Get WorldEdit
		this.worldEditPlugin = getWorldEditPlugin();
		this.worldGuardPlugin = getWorldGuardPlugin();
	}
	
	/**
	 * Plugin ends.
	 * @author joestr
	 * @since build_1
	 * @version build_7_pre_2
	 */
	public void onDisable() {
		Bukkit.getServer().getConsoleSender().sendMessage("[ZoneMenu] Deactivated.");
	}

	/**
	 * Gets the WorldEdit plugin.
	 * @since build_1
	 * @version build_7_pre_2
	 * @return {@linkplain WorldEditPlugin} WorldEdit plugin
	 */
	public WorldEditPlugin getWorldEditPlugin() {

		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

		if ((plugin == null) || !(plugin instanceof WorldEditPlugin)) {

			Bukkit.getServer().getConsoleSender().sendMessage(
					"[ZoneMenu] WorldEdit not initialized. Disabling plugin."
			);
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return null;
		}

		return (WorldEditPlugin) plugin;
	}

	/**
	 * Gets the WorldGuard plugin.
	 * @since build_1
	 * @version build_7_pre_2
	 * @return {@linkplain WorldGuardPlugin} WorldGuard plugin
	 */
	public WorldGuardPlugin getWorldGuardPlugin() {

		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin))) {

			Bukkit.getServer().getConsoleSender().sendMessage(
					"[ZoneMenu] WorldGuard not initialized. Disabling plugin."
			);
			Bukkit.getPluginManager().disablePlugin(this);
			return null;
		}

		return (WorldGuardPlugin) plugin;
	}

	/**
	 * Replaces all occourances of paramter {@code s} with '&'.
	 * @since build_1
	 * @version build_7_pre_2
	 * @param s {@linkplain String} 
	 * @return {@linkplain String} Modified string
	 * @deprecated The future of the '§' as a color code character is not safe.
	 * <br />
	 * Use {@link #colorCode(char, String)} instead.
	 */
	public String colorCode(String s, String t) {
		return t.replace(s, "§");
	}
	
	/**
	 * Calls {@linkplain ChatColor#translateAlternateColorCodes(char, String)} with the parameters {@code alternativeCode} and {@code stringToInspect}.
	 * @since build_7_pre_2
	 * @version build_7_pre_2
	 * @param alternativeCode {@linkplain char} Alternative color code
	 * @param stringToInspect {@linkplain String} String to inspect
	 * @return {@linkplain String} Modified string
	 */
	public String colorCode(char alternativeCode, String stringToInspect) {
		return ChatColor.translateAlternateColorCodes(alternativeCode, stringToInspect);
	}
	
	/**
	 * Replaces all occurrences of '§' with paramter {@code s}.
	 * @since build_1
	 * @version build_7_pre_2
	 * @param s {@linkplain String} 
	 * @return {@linkplain String} Modified string
	 * @deprecated The future of the '§' as a color code character is not safe.
	 * <br />
	 * Use {@link #alternativeColorCode(char, String)} instead.
	 */
	public String alternaticeColorCode(String s, String t) {
		return t.replace("§", s);
	}
	
	/**
	 * Replaces all occurrences of {@linkplain ChatColor#COLOR_CHAR} with paramter {@code alternativeCode}.
	 * @since build_7_pre_2
	 * @version build_7_pre_2
	 * @param alternativeCode {@linkplain char} Alternative color code
	 * @param stringToInspect {@linkplain String} String to inspect
	 * @return {@linkplain String} Modified string
	 */
	public String alternaticeColorCode(char alternativeCode, String stringToInspect) {
		return stringToInspect.replace(ChatColor.COLOR_CHAR, alternativeCode);
	}
	
	/**
	 * Sends a message to a players action bar.
	 * @author joestr
	 * @since build_7_pre_2
	 * @version build_7_pre_2
	 * @param player {@linkplain Player} A player
	 * @param message {@linkplain String} A message
	 */
	public void sendActionBarToPlayer(Player player, String message) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
	}

	/**
	 * Resets a beacon shown to a player via a blockchange.
	 * @author joestr
	 * @since build_7_pre_2
	 * @version build_7_pre_2
	 * @param playerLocationMap {@linkplain Map}<{@linkplain Player}, {@linkplain Location}> A map which contains players an locations
	 * @param player {@linkplain Player} A player
	 */
	@SuppressWarnings({ "deprecation" })
	public void resetBeaconCorner(Player player, Map<Player, Location> playerLocationMap) {

		if (!playerLocationMap.containsKey(player)) {
			return;
		}

		int x = playerLocationMap.get(player).getBlockX();
		int y = 0;
		int z = playerLocationMap.get(player).getBlockZ();
		World world = playerLocationMap.get(player).getWorld();

		player.sendBlockChange(
				world.getBlockAt(x, 1, z).getLocation(),
				world.getBlockAt(x, 1, z).getType(),
				world.getBlockAt(x, 1, z).getData()
		);

		for (int i = 2; i <= 255; i++) {
			if (world.getBlockAt(x, i, z).getType() != Material.AIR) {
				player.sendBlockChange(
						world.getBlockAt(x, i, z).getLocation(),
						world.getBlockAt(x, i, z).getType(),
						world.getBlockAt(x, i, z).getData()
				);
			}
		}
		
		for (int xPoint = x - 1; xPoint <= x + 1; xPoint++) {
			for (int zPoint = z - 1; zPoint <= z + 1; zPoint++) {
				player.sendBlockChange(
						world.getBlockAt(xPoint, y, zPoint).getLocation(),
						world.getBlockAt(x, y, z).getType(),
						world.getBlockAt(x, y, z).getData()
				);
			}
		}
	}

	/**
	 * Creates a beacon shown to a player via a blockchange.
	 * @author ?
	 * @since build_7_pre_2
	 * @version build_7_pre_2
	 * @param location {@linkplain Location} Where the beacon should be located
	 * @param player {@linkplain Player} Player to send the blockhange
	 * @param playerLocationMap {@linkplain Map}<{@linkplain Player}, {@linkplain Location}> A map which contains players an locations
	 * @param glassColor {@linkplain byte} Which color the glass should have
	 */
	// Show player a beacon (blockchange)
	// From: https://bukkit.org/threads/lib-beacon-creator.179399/ (modified)
	// --- start
	@SuppressWarnings("deprecation")
	public void createBeaconCorner(Location location, Player player, Map<Player, Location> playerLocationMap, byte glassColor) {
		
		playerLocationMap.put(player, location);
		
		int x = location.getBlockX();
		int y = 0;
		int z = location.getBlockZ();
		World world = location.getWorld();
		
		Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(
				this,
				() -> {
					/*
					 * 3x3 area on layer 0 1 Beacon at layer 1 1 Stained glass to passthrough blocks
					 */

					for (int xPoint = x - 1; xPoint <= x + 1; xPoint++) {
						for (int zPoint = z - 1; zPoint <= z + 1; zPoint++) {
							player.sendBlockChange(
									world.getBlockAt(xPoint, y, zPoint).getLocation(),
									Material.DIAMOND_BLOCK,
									(byte) 0
							);
						}
					}
					
					player.sendBlockChange(
							world.getBlockAt(x, 1, z).getLocation(),
							Material.BEACON,
							(byte) 0
					);
					
					int highestYPoint = 0;
					for (int yPoint = 2; yPoint <= 255; yPoint++) {
						if (world.getBlockAt(x, yPoint, z).getType() != Material.AIR) {
							highestYPoint = yPoint;
							player.sendBlockChange(
									world.getBlockAt(x, yPoint, z).getLocation(),
									Material.STAINED_GLASS,
									glassColor
							);
						}
					}
					
					player.sendBlockChange(
							world.getBlockAt(x, highestYPoint, z).getLocation(),
							Material.STAINED_GLASS,
							glassColor
					);
				},
				10L
		);
	}
	// --- end

	/**
	 * Send a blockchange with a little delay.
	 * @deprecated Now implemented in {@linkplain #createBeaconCorner(Location, Player, Map, byte)}.
	 * @author joestr
	 * @since build_7_pre_2
	 * @version build_7_pre_2
	 * @param location {@linkplain Location} Where the beacon should be located
	 * @param player {@linkplain Player} Player to send the blockhange
	 * @param playerLocationMap {@linkplain Material} The material to show
	 * @param date {@linkplain byte} Bytedata of the block
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	private void laterSet(final Location location, final Player player, final Material material, final byte data) {

		// 10 ticks delay (just for safety)
		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(
				this,
				() -> {
					player.sendBlockChange(location, material, data);
				},
				10L
		);
	}

	/**
	 * Calculates the difference beetween two given numbers.
	 * @author joestr
	 * @since build_7_pre_2
	 * @version build_7_pre_2
	 * @param num1 {@linkplain Integer} First number
	 * @param num2 {@linkplain Integer} Second number
	 * @return {@linkplain Integer} Difference beetwen {@code num1} and {@code num2}
	 */
	public int difference(int num1, int num2) {
		return num1 > num2 ? num1 - num2 : num2 - num1;
	}

	/**
	 * Calls {@linkplain #getRegion(Player, boolean)} with second parameter to be {@code true}.
	 * @author joestr
	 * @since build_7_pre_2
	 * @version build_7_pre_2
	 * @param player {@linkplain Player} A player
	 * @return {@linkplain ProtectedRegion} Region of a player
	 */
	public ProtectedRegion getRegion(Player player) {
		return this.getRegion(player, true);
	}
	
	/**
	 * Calls {@linkplain #getRegions(Player, boolean)} with parameters and returns the first element or {@code null}.
	 * @author joestr
	 * @since build_7_pre_2
	 * @version build_7_pre_2
	 * @param player {@linkplain Player} A player
	 * @param player {@linkplain boolean} Show a message
	 * @return {@linkplain ProtectedRegion} Region of a player
	 */
	public ProtectedRegion getRegion(final Player player, boolean showMessage) {

		List<ProtectedRegion> lp = getRegions(player, showMessage);
		
		if(lp.isEmpty()) {
			return null;
		} else {
			return lp.get(0);
		}
	}
	
	/**
	 * Get a list of players regions an control whether you like to show a message or not.
	 * @author joestr
	 * @since build_7_pre_2
	 * @version build_7_pre_2
	 * @param player {@linkplain Player} A player
	 * @param player {@linkplain boolean} Show a message
	 * @return {@linkplain List}<{@linkplain ProtectedRegion}> List of regions of a player
	 */
	public List<ProtectedRegion> getRegions(final Player player, final Boolean showMessage) {
		
		if (showMessage) {
			this.sendActionBarToPlayer(
					player,
					this.colorCode(
							"&",
							(String) this.configDelegate.getMap().get("zone_wait_message")
					)
			);
		}
		
		List<ProtectedRegion> lp = new ArrayList<ProtectedRegion>();
		RegionManager rm = worldGuardPlugin.getRegionManager(player.getWorld());
		
		for (String s : rm.getRegions().keySet()) {

			if (rm.getRegions().get(s).isOwner(worldGuardPlugin.wrapPlayer(player))) {
							
				lp.add(rm.getRegions().get(s));
			}
		}

		return lp;
	}
	
	public void futuristicRegionProcessing(final Player player, final boolean showMessage, final BiConsumer<List<ProtectedRegion>, Throwable> biconsumer) {
		
		if (showMessage) {
			this.sendActionBarToPlayer(
					player,
					this.colorCode(
							'&',
							(String) this.configDelegate.getMap().get("zone_wait_message")
					)
			);
		}
		
		CompletableFuture.supplyAsync(
				() -> {
					RegionManager rm = worldGuardPlugin.getRegionManager(player.getWorld());
					List<ProtectedRegion> lp = new ArrayList<ProtectedRegion>();
					
					for (String s : rm.getRegions().keySet()) {

						if (rm.getRegions().get(s).isOwner(worldGuardPlugin.wrapPlayer(player))) {
							
							lp.add(rm.getRegions().get(s));
						}
					}
					
					return lp;
				}
		).whenComplete(
				biconsumer
		);
	}
}
