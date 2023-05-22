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
package at.joestr.zonemenu.command;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.configuration.LocaleHelper;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.configuration.CurrentEntries;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class CommandZone implements TabExecutor {

  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) throws NotImplementedException {
    if (sender instanceof Player) {
      return handleCommandExecution((Player) sender, command, label, args);
    } else if (sender instanceof BlockCommandSender) {
      throw new NotImplementedException();
    } else if (sender instanceof ConsoleCommandSender) {
      throw new NotImplementedException();
    } else if (sender instanceof RemoteConsoleCommandSender) {
      throw new NotImplementedException();
    } else {
      throw new NotImplementedException("Command execution from neither Player, BlockCommandSender, ConsoleCommandSender nor RemoteConsoleCommandSender is not supported.");
    }
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      return handleTabCompletion((Player) sender, command, label, args);
    } else if (sender instanceof BlockCommandSender) {
      throw new NotImplementedException();
    } else if (sender instanceof ConsoleCommandSender) {
      throw new NotImplementedException();
    } else if (sender instanceof RemoteConsoleCommandSender) {
      throw new NotImplementedException();
    } else {
      throw new NotImplementedException("Tab completion from neither Player, BlockCommandSender, ConsoleCommandSender nor RemoteConsoleCommandSender is not supported.");
    }
  }

  private boolean handleCommandExecution(Player player, Command command, String label, String[] args) {
    new MessageHelper(languageResolverFunction)
      .locale(LocaleHelper.resolve(player.getLocale()))
      .path(CurrentEntries.LANG_CMD_ZONE_X_HEADER.toString())
      .prefixPath(CurrentEntries.LANG_PREFIX.toString())
      .showPrefix(true)
      .receiver(player)
      .send();

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_FIND.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_FIND.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_CREATE.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_CREATE.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_SUBCREATE.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_SUBCREATE.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_CANCEL.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_CANCEL.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_ADDMEMBER.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_ADDMEMBER.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_REMOVEMEMBER.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_REMOVEMEMBER.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_FLAG.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_FLAG.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_INFO.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_INFO.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_DELETE.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_DELETE.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_SELECT.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_SELECT.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();
    }

    if (player.hasPermission(CurrentEntries.PERM_CMD_ZONE_UPDATE.toString())) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_X_MSG_UPDATE.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(player)
        .send();
    }

    return true;
  }

  private List<String> handleTabCompletion(Player player, Command command, String label, String[] args) {
    return List.of();
  }
}
