package xyz.joestr.zonemenu.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * This class represents a player using the ZoneMenu plugin.
 * 
 * @author joestr
 * @since ${project.version}
 * @version ${project.version}
 */
public class ZoneMenuPlayer {

    private Player player = null;

    private ZoneMenuToolType toolType = null;

    private ZoneMenuSignType signType = null;

    private Location findLocation = null;

    private World createWorld = null;
    private Location createCorner1 = null;
    private Location createCorner2 = null;
    private Location createCorner3 = null;
    private Location createCorner4 = null;

    private World subcreateWorld = null;
    private Location subcreateCorner1 = null;
    private Location subcreateCorner2 = null;

    /**
     * Constrcutor for the {@link xyz.joestr.zonemenu.util.ZoneMenuPlayer
     * ZoneMenuPlayer} class.
     * 
     * @param player
     *            A {@link org.bukkit.entity.Player Player}.
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
     * @param player
     *            A {@link org.bukkit.entity.Player player}.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets the {@link xyz.joestr.zonemenu.util.ZoneMenuSignType ZoneMenuSignType}.
     * 
     * @return {@link xyz.joestr.zonemenu.util.ZoneMenuSignType ZoneMenuSignType}
     */
    public ZoneMenuSignType getSignType() {
        return signType;
    }

    /**
     * Sets the {@link xyz.joestr.zonemenu.util.ZoneMenuSignType ZoneMenuSignType}.
     * 
     * @param signType
     *            A {@link xyz.joestr.zonemenu.util.ZoneMenuSignType
     *            ZoneMenuSignType}.
     */
    public void setSignType(ZoneMenuSignType signType) {
        this.signType = signType;
    }

    /**
     * Gets the {@link xyz.joestr.zonemenu.util.ZoneMenuToolType ZoneMenuToolType}.
     * 
     * @return {@link xyz.joestr.zonemenu.util.ZoneMenuToolType ZoneMenuToolType}
     */
    public ZoneMenuToolType getToolType() {
        return toolType;
    }

    /**
     * Sets the {@link xyz.joestr.zonemenu.util.ZoneMenuToolType ZoneMenuToolType}.
     * 
     * @param toolType
     *            A {@link xyz.joestr.zonemenu.util.ZoneMenuToolType
     *            ZoneMenuToolType}.
     */
    public void setToolType(ZoneMenuToolType toolType) {
        this.toolType = toolType;
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
     * @param findLocation
     *            A {@link org.bukkit.Location Location}.
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
     * @param createWorld
     *            A {@link org.bukkit.World World}.
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
     * @param createCorner1
     *            A {@linkplain org.bukkit.Location Location}.
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
     * @param createCorner2
     *            A {@linkplain org.bukkit.Location Location}.
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
     * @param createCorner3
     *            A {@linkplain org.bukkit.Location Location}.
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
     * @param createCorner4
     *            A {@linkplain org.bukkit.Location Location}.
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
     * @param subcreateWorld
     *            A {@link org.bukkit.World World}.
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
     * @param createCorner1
     *            A {@linkplain org.bukkit.Location Location}.
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
     * @param createCorner1
     *            A {@linkplain org.bukkit.Location Location}.
     */
    public void setSubcreateCorner2(Location subcreateCorner2) {
        this.subcreateCorner2 = subcreateCorner2;
    }
}
