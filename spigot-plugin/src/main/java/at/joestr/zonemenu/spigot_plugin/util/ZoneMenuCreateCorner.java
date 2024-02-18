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
package at.joestr.zonemenu.spigot_plugin.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ZoneMenuCreateCorner {

  Plugin plugin = null;

  /**
   * Constructor of the {@link at.joestr.zonemenu.spigot_plugin.util.ZoneMenuCreateCorner
   * ZoneMenuCreateCorner} class.
   *
   * @param plugin A {@link org.bukkit.plugin.Plugin plugin}.
   */
  public ZoneMenuCreateCorner(Plugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Creates a beacon shown to a player via a blockchange.
   *
   * @param location A {@link org.bukkit.Location location} where the beacon
   * should be.
   * @param player A {@link org.bukkit.entity.Player player} to send the
   * blockchange to.
   * @param glassColor A {@link java.lang.Byte byte} which represents the color
   * of the glass.
   */
  @SuppressWarnings("deprecation")
  public void create(Location location, Player player, byte glassColor) {

    if (location == null || player == null) {

      return;
    }

    int x = location.getBlockX();
    int y = 0;
    int z = location.getBlockZ();
    World world = location.getWorld();

    this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {

      for (int xPoint = x - 1; xPoint <= x + 1; xPoint++) {

        for (int zPoint = z - 1; zPoint <= z + 1; zPoint++) {

          player.sendBlockChange(
            world.getBlockAt(xPoint, y, zPoint).getLocation(),
            Material.DIAMOND_BLOCK.createBlockData()
          );
        }
      }

      player.sendBlockChange(
        world.getBlockAt(x, 1, z).getLocation(),
        Material.BEACON.createBlockData()
      );

      int highestYPoint = 0;

      for (int yPoint = 2; yPoint <= 255; yPoint++) {

        if (world.getBlockAt(x, yPoint, z).getType() != Material.AIR) {

          highestYPoint = yPoint;

          player.sendBlockChange(
            world.getBlockAt(x, yPoint, z).getLocation(),
            Material.LEGACY_STAINED_GLASS,
            glassColor
          );
        }
      }

      player.sendBlockChange(
        world.getBlockAt(x, highestYPoint, z).getLocation(),
        Material.LEGACY_STAINED_GLASS,
        glassColor
      );
    }, 10L);
  }
  // --- end

  /**
   * Resets a beacon shown to a player via a blockchange.
   *
   * @param location A {@linkplain org.bukkit.Location location}.
   * @param player A {@linkplain Player player} to send the blockchange to.
   */
  @SuppressWarnings({"deprecation"})
  public void reset(Location location, Player player) {

    if (location == null || player == null) {

      return;
    }

    int x = location.getBlockX();
    int y = 0;
    int z = location.getBlockZ();
    World world = location.getWorld();

    player.sendBlockChange(
      world.getBlockAt(x, 1, z).getLocation(),
      world.getBlockAt(x, 1, z).getBlockData()
    );

    for (int i = 2; i <= 255; i++) {

      if (world.getBlockAt(x, i, z).getType() != Material.AIR) {

        player.sendBlockChange(
          world.getBlockAt(x, i, z).getLocation(),
          world.getBlockAt(x, i, z).getBlockData()
        );
      }
    }

    for (int xPoint = x - 1; xPoint <= x + 1; xPoint++) {

      for (int zPoint = z - 1; zPoint <= z + 1; zPoint++) {

        player.sendBlockChange(
          world.getBlockAt(xPoint, y, zPoint).getLocation(),
          world.getBlockAt(x, y, z).getBlockData()
        );
      }
    }
  }
}
