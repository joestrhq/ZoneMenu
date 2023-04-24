/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.zonemenu.command;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.configuration.LocaleHelper;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.configuration.CurrentEntries;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 *
 * @author Joel
 */
public class CommandZoneUnselect implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneUnselect.class.getName());

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      new MessageHelper(LanguageConfiguration.getInstance().getResolver())
        .locale(Locale.ENGLISH)
        .path(CurrentEntries.LANG_GEN_NOT_A_PLAYER.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();
      return true;
    }

    if (args.length != 1) {
      return false;
    }

    Player player = (Player) sender;

    WorldEdit
      .getInstance()
      .getSessionManager()
      .get(BukkitAdapter.adapt(player))
      .getRegionSelector(BukkitAdapter.adapt(player.getWorld()))
      .clear();

    new MessageHelper(LanguageConfiguration.getInstance().getResolver())
      .locale(LocaleHelper.resolve(player.getLocale()))
      .path(CurrentEntries.LANG_CMD_ZONE_UNSELECT_SUCCESS.toString())
      .prefixPath(CurrentEntries.LANG_PREFIX.toString())
      .showPrefix(true)
      .receiver(sender)
      .send();

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
