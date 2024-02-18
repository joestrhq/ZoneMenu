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
package at.joestr.zonemenu.spigot_plugin.command;

import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.javacommon.configuration.LocaleHelper;
import at.joestr.javacommon.spigotutils.MessageHelper;
import at.joestr.zonemenu.spigot_plugin.configuration.CurrentEntries;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class CommandZoneUpdate implements TabExecutor {

  private static final Logger LOG = Logger.getLogger(CommandZoneUpdate.class.getName());
  private final BiFunction<String, Locale, String> languageResolverFunction = LanguageConfiguration.getInstance().getResolver();

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length != 0) {
      return false;
    }

    if (!(sender instanceof Player)) {
      new MessageHelper(languageResolverFunction)
        .showPrefix(true)
        .path(CurrentEntries.LANG_GEN_NOT_A_PLAYER.toString())
        .locale(Locale.ENGLISH)
        .receiver(sender)
        .send();
      return true;
    }

    Player player = (Player) sender;

    new MessageHelper(languageResolverFunction)
      .showPrefix(true)
      .path(CurrentEntries.LANG_CMD_ZONE_UPDATE_ASYNCSTART.toString())
      .locale(LocaleHelper.resolve(player.getLocale()))
      .receiver(sender)
      .send();

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    return List.of();
  }
}
