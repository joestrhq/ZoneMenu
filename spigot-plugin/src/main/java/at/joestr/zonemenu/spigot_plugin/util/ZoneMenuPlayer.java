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
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class ZoneMenuPlayer {

  private Player player = null;

  private ZoneMenuMode mode = null;

  private Location findLocation = null;

  private World createWorld = null;
  private Location createCorner1 = null;
  private Location createCorner2 = null;
  private Location createCorner3 = null;
  private Location createCorner4 = null;

  private World subcreateWorld = null;
  private Location subcreateCorner1 = null;
  private Location subcreateCorner2 = null;

  private BossBar zoneFindBossbar = null;

  /**
   * Constrcutor for the {@link at.joestr.zonemenu.spigot_plugin.util.ZoneMenuPlayer
   * ZoneMenuPlayer} class.
   *
   * @param player A {@link org.bukkit.entity.Player Player}.
   */
  public ZoneMenuPlayer(Player player) {
    this.player = player;
  }

  /**
   * Gets the {@link org.bukkit.entity.Player player}.
   *
   * @return {@link org.bukkit.entity.Player Player}
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Sets a Gets the {@link org.bukkit.entity.Player player}.
   *
   * @param player A {@link org.bukkit.entity.Player player}.
   */
  public void setPlayer(Player player) {
    this.player = player;
  }

  /**
   * Gets the {@link at.joestr.zonemenu.spigot_plugin.util.ZoneMenuMode ZoneMenuToolType}.
   *
   * @return {@link at.joestr.zonemenu.spigot_plugin.util.ZoneMenuMode ZoneMenuToolType}
   */
  public ZoneMenuMode getToolType() {
    return mode;
  }

  /**
   * Sets the {@link at.joestr.zonemenu.spigot_plugin.util.ZoneMenuMode ZoneMenuToolType}.
   *
   * @param toolType A {@link at.joestr.zonemenu.spigot_plugin.util.ZoneMenuMode
   *            ZoneMenuToolType}.
   */
  public void setToolType(ZoneMenuMode toolType) {
    this.mode = toolType;
  }

  /**
   * Gets the {@link org.bukkit.Location find location}.
   *
   * @return {@link org.bukkit.Location Location}.
   */
  public Location getFindLocation() {
    return findLocation;
  }

  /**
   * Sets the {@link org.bukkit.Location find location}.
   *
   * @param findLocation A {@link org.bukkit.Location Location}.
   */
  public void setFindLocation(Location findLocation) {
    this.findLocation = findLocation;
  }

  /**
   * Gets the {@link org.bukkit.World world} for creation.
   *
   * @return A {@link org.bukkit.World World}.
   */
  public World getCreateWorld() {
    return createWorld;
  }

  /**
   * Sets the {@link org.bukkit.World world} for createion.
   *
   * @param createWorld A {@link org.bukkit.World World}.
   */
  public void setCreateWorld(World createWorld) {
    this.createWorld = createWorld;
  }

  /**
   * Gets the {@linkplain org.bukkit.Location first corner} for creation.
   *
   * @return A {@linkplain org.bukkit.Location Location}.
   */
  public Location getCreateCorner1() {
    return createCorner1;
  }

  /**
   * Sets the {@linkplain org.bukkit.Location first corner} for creation.
   *
   * @param createCorner1 A {@linkplain org.bukkit.Location Location}.
   */
  public void setCreateCorner1(Location createCorner1) {
    this.createCorner1 = createCorner1;
  }

  /**
   * Gets the {@linkplain org.bukkit.Location second corner} for creation.
   *
   * @return A {@linkplain org.bukkit.Location Location}.
   */
  public Location getCreateCorner2() {
    return createCorner2;
  }

  /**
   * Sets the {@linkplain org.bukkit.Location second corner} for creation.
   *
   * @param createCorner2 A {@linkplain org.bukkit.Location Location}.
   */
  public void setCreateCorner2(Location createCorner2) {
    this.createCorner2 = createCorner2;
  }

  /**
   * Gets the {@linkplain org.bukkit.Location third corner} for creation.
   *
   * @return A {@linkplain org.bukkit.Location Location}.
   */
  public Location getCreateCorner3() {
    return createCorner3;
  }

  /**
   * Sets the {@linkplain org.bukkit.Location third corner} for creation.
   *
   * @param createCorner3 A {@linkplain org.bukkit.Location Location}.
   */
  public void setCreateCorner3(Location createCorner3) {
    this.createCorner3 = createCorner3;
  }

  /**
   * Gets the {@linkplain org.bukkit.Location fourth corner} for creation.
   *
   * @return A {@linkplain org.bukkit.Location Location}.
   */
  public Location getCreateCorner4() {
    return createCorner4;
  }

  /**
   * Sets the {@linkplain org.bukkit.Location fourth corner} for creation.
   *
   * @param createCorner4 A {@linkplain org.bukkit.Location Location}.
   */
  public void setCreateCorner4(Location createCorner4) {
    this.createCorner4 = createCorner4;
  }

  /**
   * Gets the {@link org.bukkit.World world} for subcreation.
   *
   * @return A {@link org.bukkit.World World}.
   */
  public World getSubcreateWorld() {
    return subcreateWorld;
  }

  /**
   * Sets the {@link org.bukkit.World world} for subcreateion.
   *
   * @param subcreateWorld A {@link org.bukkit.World World}.
   */
  public void setSubcreateWorld(World subcreateWorld) {
    this.subcreateWorld = subcreateWorld;
  }

  /**
   * Gets the {@linkplain org.bukkit.Location first corner} for subcreation.
   *
   * @return A {@linkplain org.bukkit.Location Location}.
   */
  public Location getSubcreateCorner1() {
    return subcreateCorner1;
  }

  /**
   * Sets the {@linkplain org.bukkit.Location first corner} for subcreation.
   *
   * @param subcreateCorner1 A {@linkplain org.bukkit.Location Location}.
   */
  public void setSubcreateCorner1(Location subcreateCorner1) {
    this.subcreateCorner1 = subcreateCorner1;
  }

  /**
   * Gets the {@linkplain org.bukkit.Location second corner} for subcreation.
   *
   * @return A {@linkplain org.bukkit.Location Location}.
   */
  public Location getSubcreateCorner2() {
    return subcreateCorner2;
  }

  /**
   * Sets the {@linkplain org.bukkit.Location second corner} for subcreation.
   *
   * @param subcreateCorner2 A {@linkplain org.bukkit.Location Location}.
   */
  public void setSubcreateCorner2(Location subcreateCorner2) {
    this.subcreateCorner2 = subcreateCorner2;
  }

  public void setZoneFindBossbar(BossBar bossBar) {
    this.zoneFindBossbar = bossBar;
  }

  public BossBar getZoneFindBossbar() {
    return this.zoneFindBossbar;
  }
}
