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
package at.joestr.zonemenu;

import at.joestr.javacommon.configuration.AppConfiguration;
import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.zonemenu.command.CommandZone;
import at.joestr.zonemenu.command.CommandZoneAddmember;
import at.joestr.zonemenu.command.CommandZoneCancel;
import at.joestr.zonemenu.command.CommandZoneCreate;
import at.joestr.zonemenu.command.CommandZoneDelete;
import at.joestr.zonemenu.command.CommandZoneFind;
import at.joestr.zonemenu.command.CommandZoneFlag;
import at.joestr.zonemenu.command.CommandZoneInfo;
import at.joestr.zonemenu.command.CommandZoneList;
import at.joestr.zonemenu.command.CommandZoneRemovemember;
import at.joestr.zonemenu.command.CommandZoneSelect;
import at.joestr.zonemenu.command.CommandZoneSubcreate;
import at.joestr.zonemenu.command.CommandZoneUpdate;
import at.joestr.zonemenu.event.playerinteract.PlayerInteractZoneCreate;
import at.joestr.zonemenu.event.playerinteract.PlayerInteractZoneFind;
import at.joestr.zonemenu.event.playerinteract.PlayerInteractZoneSubcreate;
import at.joestr.zonemenu.listener.PlayerChangedWorld;
import at.joestr.zonemenu.listener.PlayerMove;
import at.joestr.zonemenu.listener.PlayerQuit;
import at.joestr.zonemenu.util.ZoneMenuManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ZoneMenuPlugin extends JavaPlugin implements Listener {

  private static final Logger LOG = Logger.getLogger(ZoneMenuPlugin.class.getName());

  public WorldEditPlugin worldEditPlugin;
  public WorldGuardPlugin worldGuardPlugin;

  private final Map<String, TabExecutor> commandMap = new HashMap<>();
  private final List<Listener> eventListeners = new ArrayList<>();

  @Override
  public void onEnable() {

    Metrics metrics = new Metrics(this, 4437);

    try {
      ZoneMenuManager.getInstance(this);
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
    }

    this.loadAppConfiguration();
    this.loadLanguageConfiguration();

    this.commandMap.put("zone", new CommandZone());
    this.commandMap.put("zone-find", new CommandZoneFind());
    this.commandMap.put("zone-create", new CommandZoneCreate());
    this.commandMap.put("zone-subcreate", new CommandZoneSubcreate());
    this.commandMap.put("zone-cancel", new CommandZoneCancel());
    this.commandMap.put("zone-addmember", new CommandZoneAddmember());
    this.commandMap.put("zone-removemember", new CommandZoneRemovemember());
    this.commandMap.put("zone-flag", new CommandZoneFlag());
    this.commandMap.put("zone-info", new CommandZoneInfo());
    this.commandMap.put("zone-delete", new CommandZoneDelete());
    this.commandMap.put("zone-list", new CommandZoneList());
    this.commandMap.put("zone-select", new CommandZoneSelect());
    this.commandMap.put("zone-update", new CommandZoneUpdate());
    this.registerCommands();

    this.eventListeners.add(new PlayerInteractZoneFind());
    this.eventListeners.add(new PlayerInteractZoneCreate());
    this.eventListeners.add(new PlayerInteractZoneSubcreate());
    this.eventListeners.add(new PlayerQuit());
    this.eventListeners.add(new PlayerChangedWorld());
    this.eventListeners.add(new PlayerMove());
    this.registerEventListeners();
  }

  @Override
  public void onDisable() {
  }

  private void registerCommands() {
    this.commandMap.forEach(
      (command, tabExecutor) -> {
        PluginCommand pluginCommand = this.getCommand(command);
        if (pluginCommand == null) {
          return;
        }
        pluginCommand.setExecutor(tabExecutor);
        pluginCommand.setTabCompleter(tabExecutor);
      });
  }

  private void registerEventListeners() {
    this.eventListeners.forEach(eventListener -> {
      this.getServer().getPluginManager().registerEvents(eventListener, this);
    });
  }

  private void loadAppConfiguration() {
    InputStream bundledConfig = this.getClass().getResourceAsStream("config.yml");
    File externalConfig = new File(this.getDataFolder(), "config.yml");

    try {
      AppConfiguration.getInstance(externalConfig, bundledConfig);
    } catch (IOException ex) {
      this.getLogger().log(Level.SEVERE, "Error whilst intialising the plugin configuration!", ex);
      this.getServer().getPluginManager().disablePlugin(this);
    }
  }

  private void loadLanguageConfiguration() {
    Map<String, InputStream> bundledLanguages = new HashMap<>();
    bundledLanguages.put("en.yml", this.getClass().getResourceAsStream("languages/en.yml"));
    File externalLanguagesFolder = new File(this.getDataFolder(), "languages");

    try {
      LanguageConfiguration.getInstance(
        externalLanguagesFolder, bundledLanguages, new Locale("en"));
    } catch (IOException ex) {
      this.getLogger()
        .log(Level.SEVERE, "Error whilst intialising the language configuration!", ex);
      this.getServer().getPluginManager().disablePlugin(this);
    }
  }
}
