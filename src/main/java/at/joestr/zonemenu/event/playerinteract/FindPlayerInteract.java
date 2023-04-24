package at.joestr.zonemenu.event.playerinteract;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.configuration.LocaleHelper;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.ZoneMenuPlugin;
import at.joestr.zonemenu.configuration.CurrentEntries;
import at.joestr.zonemenu.util.ZoneMenuManager;
import at.joestr.zonemenu.util.ZoneMenuPlayer;
import at.joestr.zonemenu.util.ZoneMenuToolType;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.bukkit.Material;
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
public class FindPlayerInteract implements Listener {

  private ZoneMenuPlugin zoneMenuPlugin;
  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  public FindPlayerInteract(ZoneMenuPlugin zonemenu) {
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
      || (ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.FIND)) {
      return;
    }

    if ((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      if (event.getClickedBlock().getLocation().equals(zoneMenuPlayer.getFindLocation())) {
        event.setCancelled(true);
        return;
      }

      zoneMenuPlayer.setFindLocation(event.getClickedBlock().getLocation());
      event.setCancelled(true);

      ApplicableRegionSet regiononloc = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()))
        .getApplicableRegions(
          BlockVector3.at(
            event.getClickedBlock().getLocation().getBlockX(),
            event.getClickedBlock().getLocation().getBlockY(),
            event.getClickedBlock().getLocation().getBlockZ()
          )
        );

      List<String> foundRegions = new ArrayList<>();
      for (ProtectedRegion region : regiononloc) {
        foundRegions.add(region.getId().replace("+", "#").replace("-", "."));
      }

      String textToShow = "";

      if (foundRegions.isEmpty()) {
        new MessageHelper(languageResolverFunction)
          .path(CurrentEntries.LANG_EVT_FIND_NONE.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .receiver(player)
          .send();
      } else {
        new MessageHelper(languageResolverFunction)
          .path(CurrentEntries.LANG_EVT_FIND_FOUND.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .modify((message) -> {
            return message.replace(
              "%zonenameslist",
              foundRegions.stream().collect(Collectors.joining(", "))
            );
          })
          .receiver(player)
          .send();
      }
    }
  }
}
