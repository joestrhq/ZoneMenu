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
import at.joestr.zonemenu.util.ZoneMenuManager;
import at.joestr.zonemenu.util.ZoneMenuMode;
import at.joestr.zonemenu.util.ZoneMenuPlayer;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class CommandZoneFind implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneFind.class.getName());
  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

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

    ZoneMenuManager.getInstance().zoneMenuPlayers.putIfAbsent(player, new ZoneMenuPlayer(player));

    if (ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).getToolType() == ZoneMenuMode.FIND) {
      new MessageHelper(languageResolverFunction)
        .locale(LocaleHelper.resolve(player.getLocale()))
        .path(CurrentEntries.LANG_CMD_ZONE_FIND_ALREADY_ACTIVATED.toString())
        .prefixPath(CurrentEntries.LANG_PREFIX.toString())
        .showPrefix(true)
        .receiver(sender)
        .send();
    }

    ZoneMenuManager.getInstance().zoneMenuPlayers.get(player).setToolType(ZoneMenuMode.FIND);

    new MessageHelper(languageResolverFunction)
      .locale(LocaleHelper.resolve(player.getLocale()))
      .path(CurrentEntries.LANG_CMD_ZONE_FIND_ACTIVATED.toString())
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
