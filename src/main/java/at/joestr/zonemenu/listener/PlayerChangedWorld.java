package at.joestr.zonemenu.listener;

import at.joestr.zonemenu.ZoneMenuPlugin;
import at.joestr.zonemenu.util.ZoneMenuManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

/**
 * Event listener which handles world changes
 *
 * @author joestr
 * @since build_1
 * @version ${project.version}
 */
public class PlayerChangedWorld implements Listener {

  private ZoneMenuPlugin zoneMenuPlugin;

  public PlayerChangedWorld(ZoneMenuPlugin zonemenu) {

    this.zoneMenuPlugin = zonemenu;
    this.zoneMenuPlugin.getServer().getPluginManager().registerEvents(this, this.zoneMenuPlugin);
  }

  @EventHandler
  public void onChangedWorld(PlayerChangedWorldEvent event) {

    ZoneMenuManager.getInstance().clearUpZoneMenuPlayer(event.getPlayer());
  }
}
