package at.joestr.zonemenu.command;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.configuration.LocaleHelper;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.ZoneMenu;
import at.joestr.zonemenu.configuration.CurrentEntries;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class CommandZone implements TabExecutor {

  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  public CommandZone(ZoneMenu zonemenu) {
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (!(sender instanceof Player)) {
      new MessageHelper(languageResolverFunction)
        .locale(Locale.ENGLISH)
        .path(CurrentEntries.LANG_GEN_NOT_A_PLAYER.toString())
        .receiver(sender)
        .send();
    }

    Player player = (Player) sender;

    if (!player.hasPermission(CurrentEntries.PERM_CMD_ZONE.toString())) {
      return false;
    }

    new MessageHelper(languageResolverFunction)
      .locale(LocaleHelper.resolve(player.getLocale()))
      .path(CurrentEntries.LANG_CMD_ZONE_X_HEADER.toString())
      .receiver(sender)
      .send();

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_FIND.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_INFO.toString())
        .receiver(sender)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_SUBCREATE.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_SUBCREATE.toString())
        .receiver(sender)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_CANCEL.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_CANCEL.toString())
        .receiver(sender)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_ADDMEMBER.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_ADDMEMBER.toString())
        .receiver(sender)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_REMOVEMEMBER.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_REMOVEMEMBER.toString())
        .receiver(sender)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_FLAG.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_FLAG.toString())
        .receiver(sender)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_INFO.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_INFO.toString())
        .receiver(sender)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_DELETE.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_DELETE.toString())
        .receiver(sender)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_SELECT.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_SELECT.toString())
        .receiver(sender)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_UPDATE.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_UPDATE.toString())
        .receiver(sender)
        .send();
    }

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return List.of();
  }
}
