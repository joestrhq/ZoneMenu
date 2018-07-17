package xyz.joestr.zonemenu.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

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

	public ZoneMenuPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public ZoneMenuSignType getSignType() {
		return signType;
	}

	public void setSignType(ZoneMenuSignType signType) {
		this.signType = signType;
	}

	public ZoneMenuToolType getToolType() {
		return toolType;
	}

	public void setToolType(ZoneMenuToolType toolType) {
		this.toolType = toolType;
	}

	public Location getFindLocation() {
		return findLocation;
	}

	public void setFindLocation(Location findLocation) {
		this.findLocation = findLocation;
	}
	
	public World getCreateWorld() {
		return createWorld;
	}

	public void setCreateWorld(World createWorld) {
		this.createWorld = createWorld;
	}

	public Location getCreateCorner1() {
		return createCorner1;
	}

	public void setCreateCorner1(Location createCorner1) {
		this.createCorner1 = createCorner1;
	}

	public Location getCreateCorner2() {
		return createCorner2;
	}

	public void setCreateCorner2(Location createCorner2) {
		this.createCorner2 = createCorner2;
	}

	public Location getCreateCorner3() {
		return createCorner3;
	}

	public void setCreateCorner3(Location createCorner3) {
		this.createCorner3 = createCorner3;
	}

	public Location getCreateCorner4() {
		return createCorner4;
	}

	public void setCreateCorner4(Location createCorner4) {
		this.createCorner4 = createCorner4;
	}

	public World getSubcreateWorld() {
		return subcreateWorld;
	}

	public void setSubcreateWorld(World subcreateWorld) {
		this.subcreateWorld = subcreateWorld;
	}

	public Location getSubcreateCorner1() {
		return subcreateCorner1;
	}

	public void setSubcreateCorner1(Location subcreateCorner1) {
		this.subcreateCorner1 = subcreateCorner1;
	}

	public Location getSubcreateCorner2() {
		return subcreateCorner2;
	}

	public void setSubcreateCorner2(Location subcreateCorner2) {
		this.subcreateCorner2 = subcreateCorner2;
	}
}
