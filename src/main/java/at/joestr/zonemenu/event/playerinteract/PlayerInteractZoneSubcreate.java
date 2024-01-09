//
// MIT License
//
// Copyright (c) 2017-2023 Joel Strasser <joelstrasser1@gmail.com>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package at.joestr.zonemenu.event.playerinteract;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.configuration.LocaleHelper;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.configuration.CurrentEntries;
import at.joestr.zonemenu.util.ZoneMenuManager;
import at.joestr.zonemenu.util.ZoneMenuMode;
import at.joestr.zonemenu.util.ZoneMenuPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractZoneSubcreate implements Listener {

  private static final Logger LOG = Logger.getLogger(PlayerInteractZoneSubcreate.class.getName());
  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  public PlayerInteractZoneSubcreate() {
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {

    Player player = event.getPlayer();

    if (!ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {
      return;
    }

    ZoneMenuPlayer zoneMenuPlayer = ZoneMenuManager.getInstance().zoneMenuPlayers.get(player);

    boolean hasStickInMainHand = player.getInventory().getItemInMainHand().getType() == Material.STICK;
    boolean isSubcreationModeActive = ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() == ZoneMenuMode.SUBCREATE;

    if (!(hasStickInMainHand && isSubcreationModeActive)) {
      return;
    }

    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

      if (event.getClickedBlock().getLocation().equals(zoneMenuPlayer.getSubcreateCorner1())) {
        event.setCancelled(true);
        return;
      }

      event.setCancelled(true);

      zoneMenuPlayer.setSubcreateWorld(event.getClickedBlock().getLocation().getWorld());
      ZoneMenuManager.getInstance().zoneMenuSubcreateCorner.reset(
        zoneMenuPlayer.getSubcreateCorner1(),
        player
      );
      zoneMenuPlayer.setSubcreateCorner1(event.getClickedBlock().getLocation());
      ZoneMenuManager.getInstance().zoneMenuSubcreateCorner.create(
        zoneMenuPlayer.getSubcreateCorner1(),
        player,
        Material.GLOWSTONE,
        (byte) 0
      );

      World playerworld = zoneMenuPlayer.getCreateWorld();
      Location playerpos1 = zoneMenuPlayer.getSubcreateCorner1().clone();

      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_EVT_SUBCREATION_FIRST_POSITION.toString())
        .modify(message -> message.replace("%pos_x", Integer.toString(playerpos1.getBlockX())))
        .modify(message -> message.replace("%pos_y", Integer.toString(playerpos1.getBlockY())))
        .modify(message -> message.replace("%pos_z", Integer.toString(playerpos1.getBlockZ())))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();

      boolean subcreateWorldIsPresent = zoneMenuPlayer.getSubcreateWorld() != null;
      boolean isSubcreateCorner1Present = zoneMenuPlayer.getSubcreateCorner1() != null;
      boolean isSubcreateCorner2Present = zoneMenuPlayer.getSubcreateCorner2() != null;

      if (subcreateWorldIsPresent && isSubcreateCorner1Present && isSubcreateCorner2Present) {

        ZoneMenuManager.getInstance().zoneMenuSubcreateCorner.reset(
          zoneMenuPlayer.getSubcreateCorner2(),
          player
        );
        ZoneMenuManager.getInstance().zoneMenuSubcreateCorner.create(
          zoneMenuPlayer.getSubcreateCorner2(),
          player,
          Material.SEA_LANTERN,
          (byte) 0
        );

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

        Region playerSelection = ZoneMenuManager.getInstance().getPlayerSelection(player);
        int selectionLength = playerSelection.getLength();
        int selectionWidth = playerSelection.getWidth();
        int selectionHeight = playerSelection.getHeight();
        long area = selectionLength * selectionWidth;
        long volume = selectionLength * selectionWidth * selectionHeight;

        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_EVT_SUBCREATION_AREA_NOTE.toString())
          .modify(message -> message.replace("%length", Integer.toString(selectionLength)))
          .modify(message -> message.replace("%width", Integer.toString(selectionWidth)))
          .modify(message -> message.replace("%area", Long.toString(area)))
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_EVT_SUBCREATION_VOLUME_NOTE.toString())
          .modify(message -> message.replace("%length", Integer.toString(selectionLength)))
          .modify(message -> message.replace("%width", Integer.toString(selectionWidth)))
          .modify(message -> message.replace("%height", Integer.toString(selectionHeight)))
          .modify(message -> message.replace("%volume", Long.toString(volume)))
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_EVT_SUBCREATION_SUBCREATION_NOTE.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
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

      World playerworld = zoneMenuPlayer.getCreateWorld();
      Location playerpos2 = zoneMenuPlayer.getCreateCorner2().clone();

      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_EVT_SUBCREATION_SECOND_POSITION.toString())
        .modify(message -> message.replace("%pos_x", Integer.toString(playerpos2.getBlockX())))
        .modify(message -> message.replace("%pos_y", Integer.toString(playerpos2.getBlockY())))
        .modify(message -> message.replace("%pos_z", Integer.toString(playerpos2.getBlockZ())))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();

      if ((zoneMenuPlayer.getSubcreateWorld() != null) && (zoneMenuPlayer.getSubcreateCorner1() != null)
        && (zoneMenuPlayer.getSubcreateCorner2() != null)) {

        ZoneMenuManager.getInstance().zoneMenuSubcreateCorner.reset(zoneMenuPlayer.getSubcreateCorner1(), player);
        ZoneMenuManager.getInstance().zoneMenuSubcreateCorner.create(zoneMenuPlayer.getSubcreateCorner1(), player,
          Material.GLOWSTONE, (byte) 0);

        Location playerpos1 = zoneMenuPlayer.getSubcreateCorner2().clone();

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

        Region playerSelection = ZoneMenuManager.getInstance().getPlayerSelection(player);
        int selectionLength = playerSelection.getLength();
        int selectionWidth = playerSelection.getWidth();
        int selectionHeight = playerSelection.getHeight();
        long area = selectionLength * selectionWidth;
        long volume = selectionLength * selectionWidth * selectionHeight;

        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_EVT_SUBCREATION_AREA_NOTE.toString())
          .modify(message -> message.replace("%length", Integer.toString(selectionLength)))
          .modify(message -> message.replace("%width", Integer.toString(selectionWidth)))
          .modify(message -> message.replace("%area", Long.toString(area)))
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_EVT_SUBCREATION_VOLUME_NOTE.toString())
          .modify(message -> message.replace("%length", Integer.toString(selectionLength)))
          .modify(message -> message.replace("%width", Integer.toString(selectionWidth)))
          .modify(message -> message.replace("%height", Integer.toString(selectionHeight)))
          .modify(message -> message.replace("%volume", Long.toString(volume)))
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_EVT_SUBCREATION_SUBCREATION_NOTE.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
      }
    }
  }
}
