package at.joestr.zonemenu.event.playerinteract;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.configuration.LocaleHelper;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.ZoneMenu;
import at.joestr.zonemenu.configuration.CurrentEntries;
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

  private ZoneMenu zoneMenuPlugin;
  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  public FindPlayerInteract(ZoneMenu zonemenu) {

    this.zoneMenuPlugin = zonemenu;
    this.zoneMenuPlugin.getServer().getPluginManager().registerEvents(this, this.zoneMenuPlugin);
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {

    // Grab player form the event
    Player player = event.getPlayer();

    // If the player is not in the map ...
    if (!this.zoneMenuPlugin.zoneMenuPlayers.containsKey(player)) {

      // .. do not proceed.
      return;
    }

    // Grab the ZoneMenuPlayer
    ZoneMenuPlayer zoneMenuPlayer = this.zoneMenuPlugin.zoneMenuPlayers.get(player);

    // Using a stick? ToolType correct?
    if ((player.getInventory().getItemInMainHand().getType() != Material.STICK)
      || (this.zoneMenuPlugin.zoneMenuPlayers.get(player).getToolType() != ZoneMenuToolType.FIND)) {

      return;
    }

    // Check event action
    if ((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      // Check if this location is equal to the stored one
      if (event.getClickedBlock().getLocation().equals(zoneMenuPlayer.getFindLocation())) {
        event.setCancelled(true);
        return;
      }

      // Put player an location into a map
      zoneMenuPlayer.setFindLocation(event.getClickedBlock().getLocation());

      // Cancel the event
      event.setCancelled(true);

      // Get regions on clicked location
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
        textToShow = new MessageHelper(languageResolverFunction)
          .path(CurrentEntries.LANG_EVT_FIND_NONE.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .string();
      } else {
        textToShow = new MessageHelper(languageResolverFunction)
          .path(CurrentEntries.LANG_EVT_FIND_FOUND.toString())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .modify((message) -> {
            return message.replace(
              "%zonenameslist",
              foundRegions.stream().collect(Collectors.joining(", "))
            );
          })
          .string();
      }

      // Send player a actionbar message
      this.zoneMenuPlugin.sendActionBarToPlayer(player, this.zoneMenuPlugin.colorCode('&', textToShow));
    }
  }
}
