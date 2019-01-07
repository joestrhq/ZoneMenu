package xyz.joestr.zonemenu;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import xyz.joestr.zonemenu.command.CommandZone;
import xyz.joestr.zonemenu.event.PlayerChangedWorld;
import xyz.joestr.zonemenu.event.PlayerMove;
import xyz.joestr.zonemenu.event.PlayerQuit;
import xyz.joestr.zonemenu.event.playerinteract.CreatePlayerInteract;
import xyz.joestr.zonemenu.event.playerinteract.FindPlayerInteract;
import xyz.joestr.zonemenu.event.playerinteract.SubcreatePlayerInteract;
import xyz.joestr.zonemenu.tabcomplete.ZoneTabComplete;
import xyz.joestr.zonemenu.util.Updater;
import xyz.joestr.zonemenu.util.YMLDelegate;
import xyz.joestr.zonemenu.util.ZoneMenuCreateCorner;
import xyz.joestr.zonemenu.util.ZoneMenuPlayer;
import xyz.joestr.zonemenu.util.ZoneMenuSubcreateCorner;

/**
 * ZoneMenu class
 *
 * @author Joel
 * @since build_1
 * @version ${project.version}
 */
public class ZoneMenu extends JavaPlugin implements Listener {

    // This represents the config file
    public YMLDelegate configDelegate = new YMLDelegate(this, "config", "config.yml");

    // This files contains the entries for the region IDs
    public YMLDelegate idDelegate = new YMLDelegate(this, "id", "id.yml");

    // This list contains the two delegates above (for iterating)
    public List<YMLDelegate> ymlDelegates = new ArrayList<>();

    public ZoneMenuCreateCorner zoneMenuCreateCorner = null;

    public ZoneMenuSubcreateCorner zoneMenuSubcreateCorner = null;

    // WorldEdit
    public WorldEditPlugin worldEditPlugin;

    // WorldGuard
    public WorldGuardPlugin worldGuardPlugin;

    // Map which contains players an their ZoneMenuPlayers
    public Map<Player, ZoneMenuPlayer> zoneMenuPlayers = new HashMap<>();

    // Updater
    public Updater updater = new Updater("${project.ciManagement.url}lastSuccessfulBuild/artifact/target/maven-archiver/pom.properties", "${project.version}");

    /**
     * Plugin starts.
     *
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    @Override
    public void onEnable() {

        // Add the delegates to the list
        ymlDelegates.add(configDelegate);
        ymlDelegates.add(idDelegate);

        // Check the delegates for existence,
        // if not create them.
        // Finally load them.
        ymlDelegates.stream().map((yd) -> {
            if (!yd.Exist()) {

                yd.Create();
            }
            return yd;
        }).forEachOrdered((yd) -> {
            yd.Load();
        });

        // Check the delegaes for their entries.
        // If they lack some entries,
        // add the missing ones and save the file.
        ymlDelegates.stream().filter((yd) -> (!yd.EntryCheck())).forEachOrdered((yd) -> {
            yd.Save();
        });

        this.zoneMenuCreateCorner = new ZoneMenuCreateCorner(this);
        this.zoneMenuSubcreateCorner = new ZoneMenuSubcreateCorner(this);

        // Register command /zone
        this.getCommand("zone").setExecutor(new CommandZone(this));
        this.getCommand("zone").setTabCompleter(new ZoneTabComplete(this));

        // Register the events
        new FindPlayerInteract(this);
        new CreatePlayerInteract(this);
        new SubcreatePlayerInteract(this);
        new PlayerQuit(this);
        new PlayerChangedWorld(this);
        new PlayerMove(this);

        // Get WorldEdit
        this.worldEditPlugin = this.getWorldEditPlugin();
        this.worldGuardPlugin = this.getWorldGuardPlugin();
    }

    /**
     * Plugin ends.
     *
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
     */
    @Override
    public void onDisable() {

        this.getLogger().log(Level.INFO, "[ZoneMenu] Deactivated.");
    }

    /**
     * Gets the WorldEdit plugin.
     *
     * @since ${project.version}
     * @version ${project.version}
     * @return {@linkplain WorldEditPlugin} WorldEdit plugin
     */
    public WorldEditPlugin getWorldEditPlugin() {

        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldEdit");

        if ((plugin == null) || !(plugin instanceof WorldEditPlugin)) {

            this.getLogger().log(Level.SEVERE, "[ZoneMenu] WorldEdit not initialized. Disabling plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
            return null;
        }

        return (WorldEditPlugin) plugin;
    }

    /**
     * Gets the WorldGuard plugin.
     *
     * @since build_1
     * @version ${project.version}
     * @return {@linkplain WorldGuardPlugin} WorldGuard plugin
     */
    public WorldGuardPlugin getWorldGuardPlugin() {

        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");

        if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin))) {

            this.getLogger().log(Level.SEVERE, "[ZoneMenu] WorldGuard not initialized. Disabling plugin.");
            this.getServer().getPluginManager().disablePlugin(this);

            return null;
        }

        return (WorldGuardPlugin) plugin;
    }

    /**
     * Calls {@linkplain ChatColor#translateAlternateColorCodes(char, String)}
     * with the parameters {@code alternativeCode} and {@code stringToInspect}.
     *
     * @since build_7_pre_2
     * @version ${project.version}
     * @param alternativeCode {@linkplain char} Alternative color code
     * @param stringToInspect {@linkplain String} String to inspect
     * @return {@linkplain String} Modified string
     */
    public String colorCode(char alternativeCode, String stringToInspect) {

        return ChatColor.translateAlternateColorCodes(alternativeCode, stringToInspect);
    }

    /**
     * Replaces all occurrences of {@linkplain ChatColor#COLOR_CHAR} with
     * paramter {@code alternativeCode}.
     *
     * @since build_7_pre_2
     * @version ${project.version}
     * @param alternativeCode {@linkplain char} Alternative color code
     * @param stringToInspect {@linkplain String} String to inspect
     * @return {@linkplain String} Modified string
     */
    public String alternativeColorCode(char alternativeCode, String stringToInspect) {

        return stringToInspect.replace(ChatColor.COLOR_CHAR, alternativeCode);
    }

    /**
     * Sends a message to a players action bar.
     *
     * @author joestr
     * @since build_7_pre_2
     * @version ${project.version}
     * @param player {@linkplain Player} A player
     * @param message {@linkplain String} A message
     */
    public void sendActionBarToPlayer(Player player, String message) {

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    /**
     * Calculates the difference beetween two given numbers.
     *
     * @author joestr
     * @since build_7_pre_2
     * @version ${project.version}
     * @param num1 {@linkplain Integer} First number
     * @param num2 {@linkplain Integer} Second number
     * @return {@linkplain Integer} Difference beetwen {@code num1} and
     * {@code num2}
     */
    public int difference(int num1, int num2) {

        return num1 > num2 ? num1 - num2 : num2 - num1;
    }

    /**
     * Calls {@linkplain #getRegion(Player, boolean)} with second parameter to
     * be {@code true}.
     *
     * @author joestr
     * @since build_7_pre_2
     * @version ${project.version}
     * @param player {@linkplain Player} A player
     * @return {@linkplain ProtectedRegion} Region of a player
     */
    public ProtectedRegion getRegion(Player player) {

        return this.getRegion(player, true);
    }

    /**
     * Calls {@linkplain #getRegions(Player, boolean)} with parameters and
     * returns the first element or {@code null}.
     *
     * @author joestr
     * @since build_7_pre_2
     * @version ${project.version}
     * @param player {@linkplain Player} A player
     * @param showMessage {@linkplain boolean} Show a message
     * @return {@linkplain ProtectedRegion} Region of a player
     */
    public ProtectedRegion getRegion(final Player player, boolean showMessage) {

        List<ProtectedRegion> lp = getRegions(player, showMessage);

        if (lp.isEmpty()) {

            return null;
        } else {

            return lp.get(0);
        }
    }

    /**
     * Get a list of players regions an control whether you like to show a
     * message or not.
     *
     * @author joestr
     * @since build_7_pre_2
     * @version ${project.version}
     * @param player {@linkplain Player} A player
     * @param showMessage {@linkplain boolean} Show a message
     * @return {@linkplain List}<{@linkplain ProtectedRegion}> List of regions
     * of a player
     */
    public List<ProtectedRegion> getRegions(final Player player, final Boolean showMessage) {

        if (showMessage) {

            this.sendActionBarToPlayer(player,
                this.colorCode('&', (String) this.configDelegate.getMap().get("zone_wait_message")));
        }

        List<ProtectedRegion> protectedRegions = new ArrayList<>();
        RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regeionManager = rc.get(BukkitAdapter.adapt(player.getWorld()));

        for (String string : regeionManager.getRegions().keySet()) {

            if (regeionManager.getRegions().get(string).isOwner(worldGuardPlugin.wrapPlayer(player))) {

                protectedRegions.add(regeionManager.getRegions().get(string));
            }
        }

        return protectedRegions;
    }

    /**
     * Gets all regions belonging to a player in a {@link CompletableFuture} and
     * gives it to a provided {@link BiConsumer}
     *
     * @param player {@linkplain Player} Player to search
     * @param showMessage {@linkplain String} Show a message during the search
     * @param biConsumer {@linkplain BiConsumer}<{@linkplain
     * List}<{@linkplain ProtectedRegion}>, {@linkplain Throwable}> Gets
     * executed after the search
     */
    public void futuristicRegionProcessing(final Player player, final boolean showMessage,
        BiConsumer<List<ProtectedRegion>, Throwable> biConsumer) {

        if (showMessage) {

            this.sendActionBarToPlayer(player,
                this.colorCode('&', (String) this.configDelegate.getMap().get("wait_message")));
        }

        //ProfileCache pc = WorldGuard.getInstance().getProfileCache();
        //ProfileService ps = WorldGuard.getInstance().getProfileService();
        CompletableFuture.supplyAsync(() -> {

            RegionManager regionManager
                = WorldGuard
                    .getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .get(BukkitAdapter.adapt(player.getWorld()));

            List<ProtectedRegion> protectedRegions = new ArrayList<>();

            for (String string : regionManager.getRegions().keySet()) {

                if (regionManager.getRegion(string).isOwner(worldGuardPlugin.wrapPlayer(player))) {
                    protectedRegions.add(regionManager.getRegions().get(string));
                }
            }

            return protectedRegions;
        }).whenCompleteAsync(biConsumer);
    }

    ;

    public void clearUpZoneMenuPlayer(Player player) {

        if (!this.zoneMenuPlayers.containsKey(player)) {
            return;
        }

        ZoneMenuPlayer zoneMenuPlayer = this.zoneMenuPlayers.get(player);

        LocalSession session
            = WorldEdit
                .getInstance()
                .getSessionManager()
                .get(BukkitAdapter.adapt(player));

        com.sk89q.worldedit.world.World weWorld
            = BukkitAdapter.adapt(player.getWorld());

        session.getRegionSelector(weWorld).clear();

        this.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner1(), player);
        this.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner2(), player);
        this.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner3(), player);
        this.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner4(), player);

        this.zoneMenuSubcreateCorner.reset(zoneMenuPlayer.getSubcreateCorner1(), player);
        this.zoneMenuSubcreateCorner.reset(zoneMenuPlayer.getSubcreateCorner2(), player);

        zoneMenuPlayer.getZoneFindBossbar().removePlayer(player);

        this.zoneMenuPlayers.remove(player);
    }

    public Region getPlayerSelection(Player player) {

        Region selectedRegion = null;

        LocalSession session
            = WorldEdit
                .getInstance()
                .getSessionManager()
                .get(BukkitAdapter.adapt(player));

        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(player.getWorld());

        try {
            selectedRegion = session.getRegionSelector(weWorld).getRegion();
        } catch (IncompleteRegionException ex) {

        }

        return selectedRegion;
    }
}
