package xyz.joestr.zonemenu.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Class which handles creation of create corners.
 *
 * @author joestr
 * @since ${project.version}
 * @version ${project.version}
 */
public class ZoneMenuCreateCorner {

    Plugin plugin = null;

    /**
     * Constructor of the {@link xyz.joestr.zonemenu.util.ZoneMenuCreateCorner
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
     * @param glassColor A {@link java.lang.Byte byte} which represents the
     * color of the glass.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
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

                    player.sendBlockChange(world.getBlockAt(xPoint, y, zPoint).getLocation(), Material.DIAMOND_BLOCK,
                        (byte) 0);
                }
            }

            player.sendBlockChange(world.getBlockAt(x, 1, z).getLocation(), Material.BEACON, (byte) 0);

            int highestYPoint = 0;

            for (int yPoint = 2; yPoint <= 255; yPoint++) {

                if (world.getBlockAt(x, yPoint, z).getType() != Material.AIR) {

                    highestYPoint = yPoint;

                    player.sendBlockChange(world.getBlockAt(x, yPoint, z).getLocation(), Material.LEGACY_STAINED_GLASS,
                        glassColor);
                }
            }

            player.sendBlockChange(world.getBlockAt(x, highestYPoint, z).getLocation(), Material.LEGACY_STAINED_GLASS,
                glassColor);
        }, 10L);
    }
    // --- end

    /**
     * Resets a beacon shown to a player via a blockchange.
     *
     * @param location A {@linkplain org.bukkit.Location location}.
     * @param player A {@linkplain Player player} to send the blockchange to.
     * @author joestr
     * @since ${project.version}
     * @version ${project.version}
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

        player.sendBlockChange(world.getBlockAt(x, 1, z).getLocation(), world.getBlockAt(x, 1, z).getType(),
            world.getBlockAt(x, 1, z).getData());

        for (int i = 2; i <= 255; i++) {

            if (world.getBlockAt(x, i, z).getType() != Material.AIR) {

                player.sendBlockChange(world.getBlockAt(x, i, z).getLocation(), world.getBlockAt(x, i, z).getType(),
                    world.getBlockAt(x, i, z).getData());
            }
        }

        for (int xPoint = x - 1; xPoint <= x + 1; xPoint++) {

            for (int zPoint = z - 1; zPoint <= z + 1; zPoint++) {

                player.sendBlockChange(world.getBlockAt(xPoint, y, zPoint).getLocation(),
                    world.getBlockAt(x, y, z).getType(), world.getBlockAt(x, y, z).getData());
            }
        }
    }
}
