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
import xyz.joestr.zonemenu.util.ZoneMenuPlayer;
import xyz.joestr.zonemenu.util.ZoneMenuSignType;
import xyz.joestr.zonemenu.util.ZoneMenuToolType;

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

        // Grab player from the event
        Player player = event.getPlayer();

        // If the player is not in the map ...
        if (!this.plugin.zoneMenuPlayers.containsKey(player)) {

            // ... do not proceed.
            return;
        }

        // Grab the ZoneMenuPlayer
        ZoneMenuPlayer zoneMenuPlayer = this.plugin.zoneMenuPlayers.get(player);

        // Using a stick? ToolType and SignType correct?
        if ((player.getInventory().getItemInMainHand().getType() != Material.STICK)
                || (this.plugin.zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.SIGN)
                || (this.plugin.zoneMenuPlayers.get(player).getSignType() != ZoneMenuSignType.ZONE)) {

            return;
        }

        // Initiliaze message
        String sign1 = "";

        // Check if event action is left-click
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // Check if locations are the same
            if (event.getClickedBlock().getLocation().equals(zoneMenuPlayer.getCreateCorner1())) {
                event.setCancelled(true);
                return;
            }

            // Put players world and location into maps

            // Cancel the event
            event.setCancelled(true);

            // Set the world
            zoneMenuPlayer.setCreateWorld(event.getClickedBlock().getLocation().getWorld());

            // Reset old beacon
            this.plugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner1(), player);

            // set the 1st corner
            zoneMenuPlayer.setCreateCorner1(event.getClickedBlock().getLocation());

            // Create new beacon
            this.plugin.zoneMenuCreateCorner.create(zoneMenuPlayer.getCreateCorner1(), player, (byte) 10);

            // If all needed variables are set
            if ((zoneMenuPlayer.getCreateWorld() != null) && (zoneMenuPlayer.getCreateCorner1() != null)
                    && (zoneMenuPlayer.getCreateCorner2() != null)) {
                // Reset beacons and create new ones
                this.plugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner2(), player);
                this.plugin.zoneMenuCreateCorner.create(zoneMenuPlayer.getCreateCorner2(), player, (byte) 2);

                this.plugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner3(), player);
                Location loc = zoneMenuPlayer.getCreateCorner1().clone();
                loc.setX(zoneMenuPlayer.getCreateCorner2().getX());
                zoneMenuPlayer.setCreateCorner3(loc);
                this.plugin.zoneMenuCreateCorner.create(loc, player, (byte) 0);

                this.plugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner4(), player);
                loc = zoneMenuPlayer.getCreateCorner1().clone();
                loc.setZ(zoneMenuPlayer.getCreateCorner2().getZ());
                zoneMenuPlayer.setCreateCorner4(loc);
                this.plugin.zoneMenuCreateCorner.create(loc, player, (byte) 0);

                // Grab some values to work with
                World playerworld = zoneMenuPlayer.getCreateWorld();
                Location playerpos1 = zoneMenuPlayer.getCreateCorner1().clone();
                playerpos1.setY(0);
                Location playerpos2 = zoneMenuPlayer.getCreateCorner2().clone();
                playerpos2.setY(255);

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
            // Check if locations are the same
            if (event.getClickedBlock().getLocation().equals(zoneMenuPlayer.getCreateCorner2())) {
                event.setCancelled(true);
                return;
            }

            // Put players world and location into maps

            // Cancel the event
            event.setCancelled(true);

            // Set the world
            zoneMenuPlayer.setCreateWorld(event.getClickedBlock().getLocation().getWorld());

            // Reset old beacon
            this.plugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner2(), player);

            // Set the 2nd corner
            zoneMenuPlayer.setCreateCorner2(event.getClickedBlock().getLocation());

            // Create new beacon
            this.plugin.zoneMenuCreateCorner.create(zoneMenuPlayer.getCreateCorner2(), player, (byte) 2);

            // If all needed variables are set
            if ((zoneMenuPlayer.getCreateWorld() != null) && (zoneMenuPlayer.getCreateCorner1() != null)
                    && (zoneMenuPlayer.getCreateCorner2() != null)) {
                // Reset beacons and create new ones
                this.plugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner1(), player);
                this.plugin.zoneMenuCreateCorner.create(zoneMenuPlayer.getCreateCorner1(), player, (byte) 10);

                this.plugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner3(), player);
                Location loc = zoneMenuPlayer.getCreateCorner1().clone();
                loc.setX(zoneMenuPlayer.getCreateCorner2().getX());
                zoneMenuPlayer.setCreateCorner3(loc);
                this.plugin.zoneMenuCreateCorner.create(loc, player, (byte) 0);

                this.plugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner4(), player);
                loc = zoneMenuPlayer.getCreateCorner1().clone();
                loc.setZ(zoneMenuPlayer.getCreateCorner2().getZ());
                zoneMenuPlayer.setCreateCorner4(loc);
                this.plugin.zoneMenuCreateCorner.create(loc, player, (byte) 0);

                // Grab some values to work with
                World playerworld = zoneMenuPlayer.getCreateWorld();
                Location playerpos1 = zoneMenuPlayer.getCreateCorner1().clone();
                playerpos1.setY(0);
                Location playerpos2 = zoneMenuPlayer.getCreateCorner2().clone();
                playerpos2.setY(255);

                // Make a worldedit selection
                CuboidSelection cs = new CuboidSelection(playerworld, playerpos1, playerpos2);
                this.plugin.getWorldEditPlugin().setSelection(player, cs);

                // Set actionbar message
                sign1 = (String) plugin.configDelegate.getMap().get("event_sign_second")
                        + (String) ((String) plugin.configDelegate.getMap().get("event_sign_area")).replace("{0}",
                                Integer.toString(this.plugin.getWorldEditPlugin().getSelection(player).getLength()
                                        * this.plugin.getWorldEditPlugin().getSelection(player).getWidth()));
            } else {
                // Set actionbar message
                sign1 = (String) plugin.configDelegate.getMap().get("event_sign_second");
            }

            // Send actiobar message to the player
            plugin.sendActionBarToPlayer(player, this.plugin.colorCode('&', sign1));
        }
    }
}