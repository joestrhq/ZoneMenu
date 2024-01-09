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

public class PlayerInteractZoneCreate implements Listener {

  private static final Logger LOG = Logger.getLogger(PlayerInteractZoneCreate.class.getName());
  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  public PlayerInteractZoneCreate() {
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    if (!ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {
      return;
    }

    ZoneMenuPlayer zoneMenuPlayer = ZoneMenuManager.getInstance().zoneMenuPlayers.get(player);

    boolean hasStickInMainHand = player.getInventory().getItemInMainHand().getType() == Material.STICK;
    boolean isCreationModeActive = ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() == ZoneMenuMode.CREATE;

    if (!(hasStickInMainHand && isCreationModeActive)) {
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

      World playerworld = zoneMenuPlayer.getCreateWorld();
      Location playerpos1 = zoneMenuPlayer.getCreateCorner1().clone();
      playerpos1.setY(playerworld.getMinHeight());

      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_EVT_CREATION_FIRST_POSITION.toString())
        .modify(message -> message.replace("%pos_x", Integer.toString(playerpos1.getBlockX())))
        .modify(message -> message.replace("%pos_y", Integer.toString(playerpos1.getBlockY())))
        .modify(message -> message.replace("%pos_z", Integer.toString(playerpos1.getBlockZ())))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();

      boolean isCreationWorldPresent = zoneMenuPlayer.getCreateWorld() != null;
      boolean isCreationCorner1Present = zoneMenuPlayer.getCreateCorner1() != null;
      boolean isCreationCorner2Present = zoneMenuPlayer.getCreateCorner2() != null;

      if (isCreationWorldPresent && isCreationCorner1Present && isCreationCorner2Present) {
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

        Region playerSelection = ZoneMenuManager.getInstance().getPlayerSelection(player);
        int selectionLength = playerSelection.getLength();
        int selectionWidth = playerSelection.getWidth();
        int selectionHeight = playerSelection.getHeight();
        long area = selectionLength * selectionWidth;
        long volume = selectionLength * selectionWidth * selectionHeight;

        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_EVT_CREATION_AREA_NOTE.toString())
          .modify(message -> message.replace("%length", Integer.toString(selectionLength)))
          .modify(message -> message.replace("%width", Integer.toString(selectionWidth)))
          .modify(message -> message.replace("%area", Long.toString(area)))
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_EVT_CREATION_VOLUME_NOTE.toString())
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
          .path(CurrentEntries.LANG_EVT_CREATION_CREATION_NOTE.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
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

      World playerworld = zoneMenuPlayer.getCreateWorld();
      Location playerpos2 = zoneMenuPlayer.getCreateCorner2().clone();
      playerpos2.setY(playerworld.getMaxHeight());

      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_EVT_CREATION_SECOND_POSITION.toString())
        .modify(message -> message.replace("%pos_x", Integer.toString(playerpos2.getBlockX())))
        .modify(message -> message.replace("%pos_y", Integer.toString(playerpos2.getBlockY())))
        .modify(message -> message.replace("%pos_z", Integer.toString(playerpos2.getBlockZ())))
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();

      boolean isCreationWorldPresent = zoneMenuPlayer.getCreateWorld() != null;
      boolean isCreationCorner1Present = zoneMenuPlayer.getCreateCorner1() != null;
      boolean isCreationCorner2Present = zoneMenuPlayer.getCreateCorner2() != null;

      if (isCreationWorldPresent && isCreationCorner1Present && isCreationCorner2Present) {
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

        Location playerpos1 = zoneMenuPlayer.getCreateCorner1().clone();
        playerpos1.setY(playerworld.getMinHeight());

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
          .path(CurrentEntries.LANG_EVT_CREATION_AREA_NOTE.toString())
          .modify(message -> message.replace("%length", Integer.toString(selectionLength)))
          .modify(message -> message.replace("%width", Integer.toString(selectionWidth)))
          .modify(message -> message.replace("%area", Long.toString(area)))
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
        new MessageHelper(languageResolverFunction)
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_EVT_CREATION_VOLUME_NOTE.toString())
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
          .path(CurrentEntries.LANG_EVT_CREATION_CREATION_NOTE.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
      }
    }
  }
}
