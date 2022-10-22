package at.joestr.zonemenu.event.playerinteract;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.configuration.LocaleHelper;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.ZoneMenu;
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
 * @since ${project.version}
 * @version ${project.version}
 */
public class SubcreatePlayerInteract implements Listener {

  private ZoneMenu zoneMenuPlugin;
  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  public SubcreatePlayerInteract(ZoneMenu zonemenu) {

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

    ZoneMenuPlayer zoneMenuPlayer = this.zoneMenuPlugin.zoneMenuPlayers.get(player);

    if ((player.getInventory().getItemInMainHand().getType() != Material.STICK)
      || (this.zoneMenuPlugin.zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.SIGN)
      || (this.zoneMenuPlugin.zoneMenuPlayers.get(player).getSignType() != ZoneMenuSignType.SUBZONE)) {

      return;
    }

    // Initiliaze message
    String sign1 = "";

    // Check if event action is left-click
    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

      if (event.getClickedBlock().getLocation().equals(zoneMenuPlayer.getSubcreateCorner1())) {
        event.setCancelled(true);
        return;
      }

      zoneMenuPlayer.setSubcreateWorld(event.getClickedBlock().getLocation().getWorld());

      // Cancel the event
      event.setCancelled(true);

      // Reset corner
      this.zoneMenuPlugin.zoneMenuSubcreateCorner.reset(zoneMenuPlayer.getSubcreateCorner1(), player);

      // Set the 1st corner
      zoneMenuPlayer.setSubcreateCorner1(event.getClickedBlock().getLocation());

      // Create new corner
      this.zoneMenuPlugin.zoneMenuSubcreateCorner.create(zoneMenuPlayer.getSubcreateCorner1(), player, Material.GLOWSTONE,
        (byte) 0);

      // If all needed variables are set
      if ((zoneMenuPlayer.getSubcreateWorld() != null) && (zoneMenuPlayer.getSubcreateCorner1() != null)
        && (zoneMenuPlayer.getSubcreateCorner2() != null)) {
        // Reset beacons and create new ones
        this.zoneMenuPlugin.zoneMenuSubcreateCorner.reset(zoneMenuPlayer.getSubcreateCorner2(), player);
        this.zoneMenuPlugin.zoneMenuSubcreateCorner.create(zoneMenuPlayer.getSubcreateCorner2(), player,
          Material.SEA_LANTERN, (byte) 0);

        // Grab some values to work with
        World playerworld = zoneMenuPlayer.getSubcreateWorld();
        Location playerpos1 = zoneMenuPlayer.getSubcreateCorner1().clone();
        Location playerpos2 = zoneMenuPlayer.getSubcreateCorner2().clone();

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

      if (event.getClickedBlock().getLocation().equals(zoneMenuPlayer.getSubcreateCorner2())) {
        event.setCancelled(true);
        return;
      }

      zoneMenuPlayer.setSubcreateWorld(event.getClickedBlock().getLocation().getWorld());

      // Cancel the event
      event.setCancelled(true);

      // Reset corner
      this.zoneMenuPlugin.zoneMenuSubcreateCorner.reset(zoneMenuPlayer.getSubcreateCorner2(), player);

      // Set the 2nd corner
      zoneMenuPlayer.setSubcreateCorner2(event.getClickedBlock().getLocation());

      // Create the corner
      this.zoneMenuPlugin.zoneMenuSubcreateCorner.create(zoneMenuPlayer.getSubcreateCorner2(), player,
        Material.SEA_LANTERN, (byte) 0);

      // If all needed variables are set
      if ((zoneMenuPlayer.getSubcreateWorld() != null) && (zoneMenuPlayer.getSubcreateCorner1() != null)
        && (zoneMenuPlayer.getSubcreateCorner2() != null)) {
        // Reset beacons and create new ones
        this.zoneMenuPlugin.zoneMenuSubcreateCorner.reset(zoneMenuPlayer.getSubcreateCorner1(), player);
        this.zoneMenuPlugin.zoneMenuSubcreateCorner.create(zoneMenuPlayer.getSubcreateCorner1(), player,
          Material.GLOWSTONE, (byte) 0);

        // Grab some values to work with
        World playerworld = zoneMenuPlayer.getSubcreateWorld();
        Location playerpos1 = zoneMenuPlayer.getSubcreateCorner1().clone();
        Location playerpos2 = zoneMenuPlayer.getSubcreateCorner2().clone();

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
  }
}
