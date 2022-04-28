package at.joestr.zonemenu.command.subcommand;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import at.joestr.zonemenu.ZoneMenu;
import at.joestr.zonemenu.util.ZoneMenuPlayer;
import at.joestr.zonemenu.util.ZoneMenuToolType;

public class SubCommandZoneFind {

    ZoneMenu zoneMenuPlugin = null;

    public SubCommandZoneFind(ZoneMenu plugin) {
        this.zoneMenuPlugin = plugin;
    }

    public void process(Player player, String[] args) {

        // .length does not equals 1 -> Wrong usage
        if (args.length != 1) {

            player.sendMessage(this.zoneMenuPlugin.colorCode('&',
                (String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix")
                + ((String) this.zoneMenuPlugin.configDelegate.getMap().get("usage_message")).replace("{0}",
                    "/zone find")));

            return;
        }

        // Check if players inventory contains a stick
        if (!player.getInventory().contains(Material.STICK)) {

            // Add a stick to players inventory
            player.getInventory().addItem(new ItemStack[]{
                new ItemStack(Material.STICK, 1)});
        }

        // If the player is in the map ...
        if (this.zoneMenuPlugin.zoneMenuPlayers.containsKey(player)) {

            // ... set the ToolType.
            this.zoneMenuPlugin.zoneMenuPlayers.get(player).setToolType(ZoneMenuToolType.FIND);
        } else {

            // If not create it here, set the ToolType and put it into the map.
            ZoneMenuPlayer zoneMenuPlayer = new ZoneMenuPlayer(player);

            zoneMenuPlayer.setToolType(ZoneMenuToolType.FIND);

            this.zoneMenuPlugin.zoneMenuPlayers.put(player, zoneMenuPlayer);

            zoneMenuPlayer.setZoneFindBossbar(Bukkit.createBossBar("Find bossbar", BarColor.GREEN, BarStyle.SOLID));

            zoneMenuPlayer.getZoneFindBossbar().setVisible(true);

            zoneMenuPlayer.getZoneFindBossbar().addPlayer(player);
        }

        // Send the player a message
        player.sendMessage(this.zoneMenuPlugin.colorCode('&',
            (String) this.zoneMenuPlugin.configDelegate.getMap().get("prefix")
            + (String) this.zoneMenuPlugin.configDelegate.getMap().get("zone_find")));
    }
}
