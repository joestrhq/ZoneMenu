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
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ZoneMenuSubcreateCorner {

  Plugin plugin = null;

  /**
   * Constructor of the {@link at.joestr.zonemenu.spigot_plugin.util.ZoneMenuSubcreateCorner
   * ZoneMenuSubcreateCorner} class.
   *
   * @param plugin A {@link org.bukkit.plugin.Plugin plugin}.
   */
  public ZoneMenuSubcreateCorner(Plugin plugin) {

    this.plugin = plugin;
  }

  /**
   * Creates an indicator for a corner.
   *
   * @param location A {@link org.bukkit.Location location} where the block
   * should be.
   * @param player A {@link org.bukkit.entity.Player player} to send the
   * blockchange to.
   * @param material Which {@link org.bukkit.Material material} should be used.
   * @param data A {@link java.lang.Byte byte} which represents the data.
   */
  @SuppressWarnings("deprecation")
  public void create(Location location, Player player, Material material, byte data) {

    if (location == null || player == null || material == null) {

      return;
    }

    this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
      player.sendBlockChange(location, material.createBlockData());
    }, 10L);
  }

  /**
   * Resets a beacon shown to a player via a blockchange.
   *
   * @param location A {@linkplain org.bukkit.Location location}.
   * @param player A {@linkplain Player player} to send the blockchange to.
   */
  @SuppressWarnings("deprecation")
  public void reset(Location location, Player player) {

    if (location == null || player == null) {
      return;
    }

    player.sendBlockChange(location, location.getBlock().getBlockData());
  }
}
