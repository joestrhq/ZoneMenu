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
import at.joestr.zonemenu.util.ZoneMenuManager;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 * @author Joel
 */
public class CommandZoneCancel implements TabExecutor {

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

    if (args.length != 0) {
      return false;
    }

    Player player = (Player) sender;

    if (!ZoneMenuManager.getInstance().zoneMenuPlayers.containsKey(player)) {
      new MessageHelper(LanguageConfiguration.getInstance().getResolver())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_CMD_ZONE_CANCEL_NOT_RUNNING.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
      return true;
    }

    if (ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() == null) {
      new MessageHelper(LanguageConfiguration.getInstance().getResolver())
          .locale(LocaleHelper.resolve(player.getLocale()))
          .path(CurrentEntries.LANG_CMD_ZONE_CANCEL_NOT_RUNNING.toString())
          .prefixPath(CurrentEntries.LANG_PREFIX.toString())
          .showPrefix(true)
          .receiver(player)
          .send();
      return true;
    }

    ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).setSignType(null);
    ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).setToolType(null);

    new MessageHelper(LanguageConfiguration.getInstance().getResolver())
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_CANCEL_CANCEL.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();

    return true;
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
