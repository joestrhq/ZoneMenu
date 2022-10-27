package at.joestr.zonemenu.event.playerinteract;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.configuration.LocaleHelper;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.ZoneMenuPlugin;
import at.joestr.zonemenu.configuration.CurrentEntries;
import at.joestr.zonemenu.util.ZoneMenuPlayer;
import at.joestr.zonemenu.util.ZoneMenuSignType;
import at.joestr.zonemenu.util.ZoneMenuToolType;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import java.util.Locale;
import java.util.function.BiFunction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Class which handles player interaction with blocks
 *
 * @author joestr
 * @since build_1
 * @version ${project.version}
 */
public class CreatePlayerInteract implements Listener {

  private ZoneMenuPlugin zoneMenuPlugin;
  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  public CreatePlayerInteract(ZoneMenuPlugin zonemenu) {

    this.zoneMenuPlugin = zonemenu;
    this.zoneMenuPlugin.getServer().getPluginManager().registerEvents(this, this.zoneMenuPlugin);
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {

    // Grab player from the event
    Player player = event.getPlayer();

    // If the player is not in the map ...
    if (!this.zoneMenuPlugin.zoneMenuPlayers.containsKey(player)) {

      // ... do not proceed.
      return;
    }

    // Grab the ZoneMenuPlayer
    ZoneMenuPlayer zoneMenuPlayer = this.zoneMenuPlugin.zoneMenuPlayers.get(player);

    // Using a stick? ToolType and SignType correct?
    if ((player.getInventory().getItemInMainHand().getType() != Material.STICK)
      || (this.zoneMenuPlugin.zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.SIGN)
      || (this.zoneMenuPlugin.zoneMenuPlayers.get(player).getSignType() != ZoneMenuSignType.ZONE)) {

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
      this.zoneMenuPlugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner1(), player);

      // set the 1st corner
      zoneMenuPlayer.setCreateCorner1(event.getClickedBlock().getLocation());

      // Create new beacon
      this.zoneMenuPlugin.zoneMenuCreateCorner.create(zoneMenuPlayer.getCreateCorner1(), player, (byte) 10);

      // If all needed variables are set
      if ((zoneMenuPlayer.getCreateWorld() != null) && (zoneMenuPlayer.getCreateCorner1() != null)
        && (zoneMenuPlayer.getCreateCorner2() != null)) {
        // Reset beacons and create new ones
        this.zoneMenuPlugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner2(), player);
        this.zoneMenuPlugin.zoneMenuCreateCorner.create(zoneMenuPlayer.getCreateCorner2(), player, (byte) 2);

        this.zoneMenuPlugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner3(), player);
        Location loc = zoneMenuPlayer.getCreateCorner1().clone();
        loc.setX(zoneMenuPlayer.getCreateCorner2().getX());
        zoneMenuPlayer.setCreateCorner3(loc);
        this.zoneMenuPlugin.zoneMenuCreateCorner.create(loc, player, (byte) 0);

        this.zoneMenuPlugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner4(), player);
        loc = zoneMenuPlayer.getCreateCorner1().clone();
        loc.setZ(zoneMenuPlayer.getCreateCorner2().getZ());
        zoneMenuPlayer.setCreateCorner4(loc);
        this.zoneMenuPlugin.zoneMenuCreateCorner.create(loc, player, (byte) 0);

        // Grab some values to work with
        World playerworld = zoneMenuPlayer.getCreateWorld();
        Location playerpos1 = zoneMenuPlayer.getCreateCorner1().clone();
        playerpos1.setY(0);
        Location playerpos2 = zoneMenuPlayer.getCreateCorner2().clone();
        playerpos2.setY(255);

        // Make a worldedit selection
        // Make a worldedit selection
        LocalSession session
          = WorldEdit
            .getInstance()
            .getSessionManager()
            .get(BukkitAdapter.adapt(player));

        com.sk89q.worldedit.world.World weWorld
          = BukkitAdapter.adapt(playerworld);

        session.setRegionSelector(
          weWorld,
          new CuboidRegionSelector(
            weWorld,
            BukkitAdapter.asBlockVector(playerpos1),
            BukkitAdapter.asBlockVector(playerpos2)
          )
        );

        session.dispatchCUISelection(BukkitAdapter.adapt(player));

        String signFirst = new MessageHelper(languageResolverFunction)
          .path(CurrentEntries.LANG_EVT_SIGN_FIRST.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .rawString();

        String areaValue = Integer.toString(
          this.zoneMenuPlugin.getPlayerSelection(player).getLength()
          * this.zoneMenuPlugin.getPlayerSelection(player).getWidth());

        String signArea = new MessageHelper(languageResolverFunction)
          .path(CurrentEntries.LANG_EVT_SIGN_AREA.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .modify((message) -> message.replace("%area", areaValue))
          .string();

        sign1 = signFirst + signArea;
      } else {
        String signFirst = new MessageHelper(languageResolverFunction)
          .path(CurrentEntries.LANG_EVT_SIGN_FIRST.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .rawString();

        sign1 = signFirst;
      }

      // Send actiobar message to the player
      zoneMenuPlugin.sendActionBarToPlayer(player, this.zoneMenuPlugin.colorCode('&', sign1));
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
      this.zoneMenuPlugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner2(), player);

      // Set the 2nd corner
      zoneMenuPlayer.setCreateCorner2(event.getClickedBlock().getLocation());

      // Create new beacon
      this.zoneMenuPlugin.zoneMenuCreateCorner.create(zoneMenuPlayer.getCreateCorner2(), player, (byte) 2);

      // If all needed variables are set
      if ((zoneMenuPlayer.getCreateWorld() != null) && (zoneMenuPlayer.getCreateCorner1() != null)
        && (zoneMenuPlayer.getCreateCorner2() != null)) {
        // Reset beacons and create new ones
        this.zoneMenuPlugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner1(), player);
        this.zoneMenuPlugin.zoneMenuCreateCorner.create(zoneMenuPlayer.getCreateCorner1(), player, (byte) 10);

        this.zoneMenuPlugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner3(), player);
        Location loc = zoneMenuPlayer.getCreateCorner1().clone();
        loc.setX(zoneMenuPlayer.getCreateCorner2().getX());
        zoneMenuPlayer.setCreateCorner3(loc);
        this.zoneMenuPlugin.zoneMenuCreateCorner.create(loc, player, (byte) 0);

        this.zoneMenuPlugin.zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner4(), player);
        loc = zoneMenuPlayer.getCreateCorner1().clone();
        loc.setZ(zoneMenuPlayer.getCreateCorner2().getZ());
        zoneMenuPlayer.setCreateCorner4(loc);
        this.zoneMenuPlugin.zoneMenuCreateCorner.create(loc, player, (byte) 0);

        // Grab some values to work with
        World playerworld = zoneMenuPlayer.getCreateWorld();
        Location playerpos1 = zoneMenuPlayer.getCreateCorner1().clone();
        playerpos1.setY(0);
        Location playerpos2 = zoneMenuPlayer.getCreateCorner2().clone();
        playerpos2.setY(255);

        // Make a worldedit selection
        LocalSession session
          = WorldEdit
            .getInstance()
            .getSessionManager()
            .get(BukkitAdapter.adapt(player));

        com.sk89q.worldedit.world.World weWorld
          = BukkitAdapter.adapt(playerworld);

        session.setRegionSelector(
          weWorld,
          new CuboidRegionSelector(
            weWorld,
            BukkitAdapter.asBlockVector(playerpos1),
            BukkitAdapter.asBlockVector(playerpos2)
          )
        );

        session.dispatchCUISelection(BukkitAdapter.adapt(player));

        String signSecond = new MessageHelper(languageResolverFunction)
          .path(CurrentEntries.LANG_EVT_SIGN_SECOND.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .rawString();

        String areaValue = Integer.toString(
          this.zoneMenuPlugin.getPlayerSelection(player).getLength()
          * this.zoneMenuPlugin.getPlayerSelection(player).getWidth());

        String signArea = new MessageHelper(languageResolverFunction)
          .path(CurrentEntries.LANG_EVT_SIGN_AREA.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .modify((message) -> message.replace("%area", areaValue))
          .string();

        sign1 = signSecond + signArea;
      } else {
        String signSecond = new MessageHelper(languageResolverFunction)
          .path(CurrentEntries.LANG_EVT_SIGN_SECOND.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .rawString();

        sign1 = signSecond;
      }

      // Send actiobar message to the player
      zoneMenuPlugin.sendActionBarToPlayer(player, this.zoneMenuPlugin.colorCode('&', sign1));
    }
  }
}
