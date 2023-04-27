package at.joestr.zonemenu.event.playerinteract;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.configuration.LocaleHelper;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.ZoneMenuPlugin;
import at.joestr.zonemenu.configuration.CurrentEntries;
import at.joestr.zonemenu.util.ZoneMenuManager;
import at.joestr.zonemenu.util.ZoneMenuPlayer;
import at.joestr.zonemenu.util.ZoneMenuSignType;
import at.joestr.zonemenu.util.ZoneMenuToolType;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    Player player = event.getPlayer();

    if (!ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {
      return;
    }

    ZoneMenuPlayer zoneMenuPlayer = ZoneMenuManager.getInstance().zoneMenuPlayers.get(player);

    boolean hasStickInMainHand = player.getInventory().getItemInMainHand().getType() == Material.STICK;
    boolean isToolTypeSign = ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() == ZoneMenuToolType.SIGN;
    boolean isSignTypeZone = ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getSignType() == ZoneMenuSignType.ZONE;

    if (!(hasStickInMainHand && isToolTypeSign && isSignTypeZone)) {
      return;
    }

    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
      if (event.getClickedBlock().getLocation().equals(zoneMenuPlayer.getCreateCorner1())) {
        event.setCancelled(true);
        return;
      }

      event.setCancelled(true);

      zoneMenuPlayer.setCreateWorld(event.getClickedBlock().getLocation().getWorld());
      ZoneMenuManager.getInstance().zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner1(), player);
      zoneMenuPlayer.setCreateCorner1(event.getClickedBlock().getLocation());
      ZoneMenuManager.getInstance().zoneMenuCreateCorner.create(zoneMenuPlayer.getCreateCorner1(), player, (byte) 10);

      boolean createWorldIsPresent = zoneMenuPlayer.getCreateWorld() != null;
      boolean createCorner1IsPresent = zoneMenuPlayer.getCreateCorner1() != null;
      boolean createCorner2IsPresent = zoneMenuPlayer.getCreateCorner2() != null;

      if (createWorldIsPresent && createCorner1IsPresent && createCorner2IsPresent) {
        ZoneMenuManager.getInstance().zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner2(), player);
        ZoneMenuManager.getInstance().zoneMenuCreateCorner.create(zoneMenuPlayer.getCreateCorner2(), player, (byte) 2);

        ZoneMenuManager.getInstance().zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner3(), player);
        Location loc = zoneMenuPlayer.getCreateCorner1().clone();
        loc.setX(zoneMenuPlayer.getCreateCorner2().getX());
        zoneMenuPlayer.setCreateCorner3(loc);
        ZoneMenuManager.getInstance().zoneMenuCreateCorner.create(loc, player, (byte) 0);

        ZoneMenuManager.getInstance().zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner4(), player);
        loc = zoneMenuPlayer.getCreateCorner1().clone();
        loc.setZ(zoneMenuPlayer.getCreateCorner2().getZ());
        zoneMenuPlayer.setCreateCorner4(loc);
        ZoneMenuManager.getInstance().zoneMenuCreateCorner.create(loc, player, (byte) 0);

        World playerworld = zoneMenuPlayer.getCreateWorld();
        Location playerpos1 = zoneMenuPlayer.getCreateCorner1().clone();
        playerpos1.setY(playerworld.getMinHeight());
        Location playerpos2 = zoneMenuPlayer.getCreateCorner2().clone();
        playerpos2.setY(playerworld.getMaxHeight());

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

        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_EVT_SIGN_FIRST.toString())
          .receiver(player)
          .send();

        String areaValue = Integer.toString(
          ZoneMenuManager.getInstance().getPlayerSelection(player).getLength()
          * ZoneMenuManager.getInstance().getPlayerSelection(player).getWidth());

        try {
          new MessageHelper(languageResolverFunction)
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_EVT_SIGN_AREA.toString())
            .modify((message) -> message.replace("%area", areaValue))
            .receiver(player)
            .sendActionBar();
        } catch (Exception ex) {
          Logger.getLogger(CreatePlayerInteract.class.getName()).log(Level.SEVERE, null, ex);
        }
      } else {
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_EVT_SIGN_FIRST.toString())
          .receiver(player)
          .send();
      }
    }

    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (event.getClickedBlock().getLocation().equals(zoneMenuPlayer.getCreateCorner2())) {
        event.setCancelled(true);
        return;
      }

      event.setCancelled(true);

      zoneMenuPlayer.setCreateWorld(event.getClickedBlock().getLocation().getWorld());
      ZoneMenuManager.getInstance().zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner2(), player);
      zoneMenuPlayer.setCreateCorner2(event.getClickedBlock().getLocation());
      ZoneMenuManager.getInstance().zoneMenuCreateCorner.create(zoneMenuPlayer.getCreateCorner2(), player, (byte) 2);

      boolean createWorldIsPresent = zoneMenuPlayer.getCreateWorld() != null;
      boolean createCorner1IsPresent = zoneMenuPlayer.getCreateCorner1() != null;
      boolean createCorner2IsPresent = zoneMenuPlayer.getCreateCorner2() != null;

      if (createWorldIsPresent && createCorner1IsPresent && createCorner2IsPresent) {
        ZoneMenuManager.getInstance().zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner1(), player);
        ZoneMenuManager.getInstance().zoneMenuCreateCorner.create(zoneMenuPlayer.getCreateCorner1(), player, (byte) 10);

        ZoneMenuManager.getInstance().zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner3(), player);
        Location loc = zoneMenuPlayer.getCreateCorner1().clone();
        loc.setX(zoneMenuPlayer.getCreateCorner2().getX());
        zoneMenuPlayer.setCreateCorner3(loc);
        ZoneMenuManager.getInstance().zoneMenuCreateCorner.create(loc, player, (byte) 0);

        ZoneMenuManager.getInstance().zoneMenuCreateCorner.reset(zoneMenuPlayer.getCreateCorner4(), player);
        loc = zoneMenuPlayer.getCreateCorner1().clone();
        loc.setZ(zoneMenuPlayer.getCreateCorner2().getZ());
        zoneMenuPlayer.setCreateCorner4(loc);
        ZoneMenuManager.getInstance().zoneMenuCreateCorner.create(loc, player, (byte) 0);

        World playerworld = zoneMenuPlayer.getCreateWorld();
        Location playerpos1 = zoneMenuPlayer.getCreateCorner1().clone();
        playerpos1.setY(playerworld.getMinHeight());
        Location playerpos2 = zoneMenuPlayer.getCreateCorner2().clone();
        playerpos2.setY(playerworld.getMaxHeight());

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

        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_EVT_SIGN_SECOND.toString())
          .receiver(player)
          .send();

        String areaValue = Integer.toString(
          ZoneMenuManager.getInstance().getPlayerSelection(player).getLength()
          * ZoneMenuManager.getInstance().getPlayerSelection(player).getWidth());

        try {
          new MessageHelper(languageResolverFunction)
            .locale(LocaleHelper.resolve(player.getLocale()))
            .path(CurrentEntries.LANG_EVT_SIGN_AREA.toString())
            .modify((message) -> message.replace("%area", areaValue))
            .receiver(player)
            .sendActionBar();
        } catch (Exception ex) {
          Logger.getLogger(CreatePlayerInteract.class.getName()).log(Level.SEVERE, null, ex);
        }
      } else {
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_EVT_SIGN_SECOND.toString())
          .receiver(player)
          .send();
      }
    }
  }
}
