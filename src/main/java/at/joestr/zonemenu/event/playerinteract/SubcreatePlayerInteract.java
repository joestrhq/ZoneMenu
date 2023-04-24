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
 * @since ${project.version}
 * @version ${project.version}
 */
public class SubcreatePlayerInteract implements Listener {

  private ZoneMenuPlugin zoneMenuPlugin;
  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  public SubcreatePlayerInteract(ZoneMenuPlugin zonemenu) {

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

    if ((player.getInventory().getItemInMainHand().getType() != Material.STICK)
      || (ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.SIGN)
      || (ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getSignType() != ZoneMenuSignType.SUBZONE)) {
      return;
    }

    String sign1 = "";

    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

      if (event.getClickedBlock().getLocation().equals(zoneMenuPlayer.getSubcreateCorner1())) {
        event.setCancelled(true);
        return;
      }

      zoneMenuPlayer.setSubcreateWorld(event.getClickedBlock().getLocation().getWorld());
      event.setCancelled(true);
      ZoneMenuManager.getInstance().zoneMenuSubcreateCorner.reset(zoneMenuPlayer.getSubcreateCorner1(), player);
      zoneMenuPlayer.setSubcreateCorner1(event.getClickedBlock().getLocation());
      ZoneMenuManager.getInstance().zoneMenuSubcreateCorner.create(zoneMenuPlayer.getSubcreateCorner1(), player, Material.GLOWSTONE,
        (byte) 0);

      if ((zoneMenuPlayer.getSubcreateWorld() != null) && (zoneMenuPlayer.getSubcreateCorner1() != null)
        && (zoneMenuPlayer.getSubcreateCorner2() != null)) {

        ZoneMenuManager.getInstance().zoneMenuSubcreateCorner.reset(zoneMenuPlayer.getSubcreateCorner2(), player);
        ZoneMenuManager.getInstance().zoneMenuSubcreateCorner.create(zoneMenuPlayer.getSubcreateCorner2(), player,
          Material.SEA_LANTERN, (byte) 0);

        World playerworld = zoneMenuPlayer.getSubcreateWorld();
        Location playerpos1 = zoneMenuPlayer.getSubcreateCorner1().clone();
        Location playerpos2 = zoneMenuPlayer.getSubcreateCorner2().clone();

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
          .path(CurrentEntries.LANG_EVT_SIGN_FIRST.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .receiver(player)
          .send();

        String areaValue = Integer.toString(
          ZoneMenuManager.getInstance().getPlayerSelection(player).getLength()
          * ZoneMenuManager.getInstance().getPlayerSelection(player).getWidth());

        try {
          new MessageHelper(languageResolverFunction)
            .path(CurrentEntries.LANG_EVT_SIGN_AREA.toString())
            .locale(LocaleHelper.resolve(player.getLocale()))
            .modify((message) -> message.replace("%area", areaValue))
            .receiver(player)
            .sendActionBar();
        } catch (Exception ex) {
          Logger.getLogger(SubcreatePlayerInteract.class.getName()).log(Level.SEVERE, null, ex);
        }
      } else {
        new MessageHelper(languageResolverFunction)
          .path(CurrentEntries.LANG_EVT_SIGN_FIRST.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .receiver(player)
          .send();
      }
    }

    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

      if (event.getClickedBlock().getLocation().equals(zoneMenuPlayer.getSubcreateCorner2())) {
        event.setCancelled(true);
        return;
      }

      zoneMenuPlayer.setSubcreateWorld(event.getClickedBlock().getLocation().getWorld());
      event.setCancelled(true);
      ZoneMenuManager.getInstance().zoneMenuSubcreateCorner.reset(zoneMenuPlayer.getSubcreateCorner2(), player);
      zoneMenuPlayer.setSubcreateCorner2(event.getClickedBlock().getLocation());
      ZoneMenuManager.getInstance().zoneMenuSubcreateCorner.create(zoneMenuPlayer.getSubcreateCorner2(), player,
        Material.SEA_LANTERN, (byte) 0);

      if ((zoneMenuPlayer.getSubcreateWorld() != null) && (zoneMenuPlayer.getSubcreateCorner1() != null)
        && (zoneMenuPlayer.getSubcreateCorner2() != null)) {

        ZoneMenuManager.getInstance().zoneMenuSubcreateCorner.reset(zoneMenuPlayer.getSubcreateCorner1(), player);
        ZoneMenuManager.getInstance().zoneMenuSubcreateCorner.create(zoneMenuPlayer.getSubcreateCorner1(), player,
          Material.GLOWSTONE, (byte) 0);

        World playerworld = zoneMenuPlayer.getSubcreateWorld();
        Location playerpos1 = zoneMenuPlayer.getSubcreateCorner1().clone();
        Location playerpos2 = zoneMenuPlayer.getSubcreateCorner2().clone();

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
          .path(CurrentEntries.LANG_EVT_SIGN_FIRST.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .receiver(player)
          .send();

        String areaValue = Integer.toString(
          ZoneMenuManager.getInstance().getPlayerSelection(player).getLength()
          * ZoneMenuManager.getInstance().getPlayerSelection(player).getWidth());

        try {
          new MessageHelper(languageResolverFunction)
            .path(CurrentEntries.LANG_EVT_SIGN_AREA.toString())
            .locale(LocaleHelper.resolve(player.getLocale()))
            .modify((message) -> message.replace("%area", areaValue))
            .receiver(player)
            .sendActionBar();
        } catch (Exception ex) {
          Logger.getLogger(SubcreatePlayerInteract.class.getName()).log(Level.SEVERE, null, ex);
        }
      } else {
        new MessageHelper(languageResolverFunction)
          .path(CurrentEntries.LANG_EVT_SIGN_FIRST.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .receiver(player)
          .send();
      }
    }
  }
}
