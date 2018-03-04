package xyz.joestr.zonemenu;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.joestr.zonemenu.util.ActionBar;
import xyz.joestr.zonemenu.util.YMLDelegate;
import xyz.joestr.zonemenu.command.ZoneCommand;
import xyz.joestr.zonemenu.event.PlayerChangedWorld;
import xyz.joestr.zonemenu.event.PlayerQuit;
import xyz.joestr.zonemenu.event.PlayerInteract;
import xyz.joestr.zonemenu.tabcomplete.ZoneTabComplete;

public class ZoneMenu extends JavaPlugin implements Listener {

	public YMLDelegate config = new YMLDelegate(this, "config", "config.yml");
	public YMLDelegate id = new YMLDelegate(this, "id", "id.yml");

	public ArrayList<YMLDelegate> ymldelegates = new ArrayList<YMLDelegate>();

	public WorldEditPlugin worldeditplugin;
	public WorldGuardPlugin worldguardplugin;

	public HashMap<String, Location> findlocations = new HashMap<String, Location>();
	public HashMap<String, World> worlds = new HashMap<String, World>();
	public HashMap<String, Location> firstlocations = new HashMap<String, Location>();
	public HashMap<String, Location> secondlocations = new HashMap<String, Location>();
	public HashMap<String, String> tool = new HashMap<String, String>();
	// public HashMap<String, Object> Config = new HashMap<String, Object>();
	public HashMap<String, Location> corner1 = new HashMap<String, Location>();
	public HashMap<String, Location> corner2 = new HashMap<String, Location>();
	public HashMap<String, Location> corner3 = new HashMap<String, Location>();
	public HashMap<String, Location> corner4 = new HashMap<String, Location>();

	public void onEnable() {

		Bukkit.getServer().getConsoleSender().sendMessage("[ZoneMenu] Version: " + Bukkit.getServer().getVersion());
		Bukkit.getServer().getConsoleSender()
				.sendMessage("[ZoneMenu] Bukkit Version: " + Bukkit.getServer().getBukkitVersion());

		ymldelegates.add(config);
		ymldelegates.add(id);

		for (YMLDelegate yd : ymldelegates) {
			if (!yd.Exist()) {
				yd.Create();
			}
			yd.Load();
		}

		new PlayerInteract(this);
		new PlayerQuit(this);
		new PlayerChangedWorld(this);

		this.getCommand("zone").setExecutor(new ZoneCommand(this));
		this.getCommand("zone").setTabCompleter(new ZoneTabComplete(this));

		worldeditplugin = getWorldEdit();
		worldguardplugin = getWorldGuard();

		for (YMLDelegate yd : ymldelegates) {
			if (!yd.EntryCheck()) {
				yd.Save();
			}
		}

		// Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GREEN +
		// "[ZoneMenu] Activated.");
	}

	public void onDisable() {

		// Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GOLD +
		// "[ZoneMenu] Deactivated.");
	}
	
	// Get Worldedit here
	public WorldEditPlugin getWorldEdit() {

		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

		if ((plugin == null) || (!(plugin instanceof WorldEditPlugin))) {

			Bukkit.getServer().getConsoleSender()
					.sendMessage("[ZoneMenu] WorldEdit not initialized. Disabling plugin.");
			Bukkit.getPluginManager().disablePlugin(this);
			return null;
		}

		return (WorldEditPlugin) plugin;
	}
	
	// Get worldguard here
	public WorldGuardPlugin getWorldGuard() {

		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin))) {

			Bukkit.getServer().getConsoleSender()
					.sendMessage("[ZoneMenu] WorldGuard not initialized. Disabling plugin.");
			Bukkit.getPluginManager().disablePlugin(this);
			return null;
		}

		return (WorldGuardPlugin) plugin;
	}
	
	// <Alternative colorcode> -> §
	public String colorCode(String s, String t) {
		return t.replace(s, "§");
	}
	
	// § -> <Alternative colorcode>
	public String alternaticeColorCode(String s, String t) {
		return t.replace("§", s);
	}

	// Reset a shown beacon via a blockchange
	@SuppressWarnings({ "deprecation" })
	public void resetCorner(Player player, Map<String, Location> map) {
		
		if (!map.containsKey(player.getName())) {
			return;
		}

		// Location l = map.get(player.getName());
		// map.get(player.getName()).getWorld().refreshChunk(l.getBlockX(),
		// l.getBlockZ());

		int x = map.get(player.getName()).getBlockX();
		int y = 0;
		int z = map.get(player.getName()).getBlockZ();
		World world = map.get(player.getName()).getWorld();

		player.sendBlockChange(world.getBlockAt(x, 1, z).getLocation(), world.getBlockAt(x, 1, z).getType(),
				world.getBlockAt(x, 1, z).getData());

		for (int i = 2; i <= 255; i++) {

			if (world.getBlockAt(x, i, z).getType() != Material.AIR) {

				player.sendBlockChange(world.getBlockAt(x, i, z).getLocation(), world.getBlockAt(x, i, z).getType(),
						world.getBlockAt(x, i, z).getData());
			}
		}

		for (int xPoint = x - 1; xPoint <= x + 1; xPoint++) {

			for (int zPoint = z - 1; zPoint <= z + 1; zPoint++) {

				player.sendBlockChange(world.getBlockAt(xPoint, y, zPoint).getLocation(),
						world.getBlockAt(x, y, z).getType(), world.getBlockAt(x, y, z).getData());
			}
		}
	}
	
	// Show player a beacon (blockchange)
	// From: https://bukkit.org/threads/lib-beacon-creator.179399/ (modified)
	// --- start
	@SuppressWarnings("deprecation")
	public void createBeaconCorner(Location location, Player player, Map<String, Location> map, byte glasscolor) {

		int x = location.getBlockX();
		int y = 0;
		int z = location.getBlockZ();
		World world = location.getWorld();

		/*
		 * 3x3 area on layer 0 1 Beacon at layer 1 1 Stained glass to passthrough blocks
		 */

		for (int i = 2; i <= 255; i++) {

			if (world.getBlockAt(x, i, z).getType() != Material.AIR) {

				player.sendBlockChange(world.getBlockAt(x, i, z).getLocation(), Material.STAINED_GLASS, glasscolor);
			}
		}

		for (int xPoint = x - 1; xPoint <= x + 1; xPoint++) {

			for (int zPoint = z - 1; zPoint <= z + 1; zPoint++) {

				player.sendBlockChange(world.getBlockAt(xPoint, y, zPoint).getLocation(), Material.DIAMOND_BLOCK,
						(byte) 0);
			}
		}

		laterSet(world.getBlockAt(x, 1, z).getLocation(), player, Material.BEACON, (byte) 0);
		// player.sendBlockChange(world.getBlockAt(x, 1, z).getLocation(),
		// Material.BEACON, (byte)0);

		map.put(player.getName(), location);
	}
	// --- end

	@SuppressWarnings("deprecation")
	// Send a blockchange with a slight delay (10 ticks)
	public void laterSet(final Location location, final Player player, final Material material, final byte data) {

		// 10 ticks delay (just for safety)
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			public void run() {
				player.sendBlockChange(location, material, data);
			}
		}, 10L);
	}
	
	// Calculate a difference between two numbers
	public int difference(int num1, int num2) {
		return num1 > num2 ? num1 - num2 : num2 - num1;
	}
	
	// Get a players region
	public ProtectedRegion getRegion(Player player) throws InterruptedException, ExecutionException {

		return this.getRegion(player, true);
	}
	
	// Get a players region an control whether you like to show find a message during search or not
	public ProtectedRegion getRegion(final Player player, boolean showmessage)
			throws InterruptedException, ExecutionException {

		if (showmessage) {
			ActionBar.sendActionBar(player,
					this.colorCode("&", (String) this.config.getMap().get("zone_wait_message")));
		}

		ProtectedRegion p = null;
		ExecutorService executor = Executors.newFixedThreadPool(1);

		FutureTask<ProtectedRegion> futureTask = new FutureTask<ProtectedRegion>(new Callable<ProtectedRegion>() {

			public ProtectedRegion call() {

				ProtectedRegion p = null;

				for (String s : worldguardplugin.getRegionManager(player.getWorld()).getRegions().keySet()) {

					if (worldguardplugin.getRegionManager(player.getWorld()).getRegions().get(s)
							.isOwner(worldguardplugin.wrapPlayer(player))) {

						p = worldguardplugin.getRegionManager(player.getWorld()).getRegions().get(s);
					}
				}

				return p;
			}
		});

		executor.execute(futureTask);

		while (!futureTask.isDone()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		p = futureTask.get();
		executor.shutdown();

		return p;
	}
	
	// Get a list of players regions an control if you want him to show a message during search or not
	public List<ProtectedRegion> getRegions(final Player player, Boolean showmessage)
			throws InterruptedException, ExecutionException {

		if (showmessage) {
			ActionBar.sendActionBar(player,
					this.colorCode("&", (String) this.config.getMap().get("zone_wait_message")));
		}

		List<ProtectedRegion> lp = new ArrayList<ProtectedRegion>();
		ExecutorService executor = Executors.newFixedThreadPool(1);

		FutureTask<List<ProtectedRegion>> futureTask = new FutureTask<List<ProtectedRegion>>(
				new Callable<List<ProtectedRegion>>() {

					public List<ProtectedRegion> call() {

						List<ProtectedRegion> lp = new ArrayList<ProtectedRegion>();

						for (String s : worldguardplugin.getRegionManager(player.getWorld()).getRegions().keySet()) {

							if (worldguardplugin.getRegionManager(player.getWorld()).getRegions().get(s)
									.isOwner(worldguardplugin.wrapPlayer(player))) {

								lp.add(worldguardplugin.getRegionManager(player.getWorld()).getRegions().get(s));
							}
						}

						return lp;
					}
				});

		executor.execute(futureTask);

		while (!futureTask.isDone()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		lp = futureTask.get();
		executor.shutdown();

		return lp;
	}
}
