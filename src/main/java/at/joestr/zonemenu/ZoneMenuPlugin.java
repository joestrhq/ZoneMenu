package at.joestr.zonemenu;

import at.joestr.javacommon.configuration.AppConfiguration;
import at.joestr.javacommon.configuration.LanguageConfiguration;
import at.joestr.zonemenu.command.CommandZone;
import at.joestr.zonemenu.event.playerinteract.CreatePlayerInteract;
import at.joestr.zonemenu.event.playerinteract.FindPlayerInteract;
import at.joestr.zonemenu.event.playerinteract.SubcreatePlayerInteract;
import at.joestr.zonemenu.listener.PlayerChangedWorld;
import at.joestr.zonemenu.listener.PlayerMove;
import at.joestr.zonemenu.listener.PlayerQuit;
import at.joestr.zonemenu.util.ZoneMenuManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class of this plugin.
 *
 * @author Joel Strasser (joestr)
 * @version ${project.version}
 */
public class ZoneMenuPlugin extends JavaPlugin implements Listener {

  private final Logger LOGGER = Logger.getLogger(ZoneMenuPlugin.class.getName());

  public WorldEditPlugin worldEditPlugin;
  public WorldGuardPlugin worldGuardPlugin;

  // Map which contains players an their ZoneMenuPlayers
  private Map<String, TabExecutor> commandMap = new HashMap<>();

  @Override
  public void onEnable() {

    Metrics metrics = new Metrics(this, 4437);

    try {
      ZoneMenuManager.getInstance(this);
    } catch (Exception ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }

    this.loadAppConfiguration();
    this.loadLanguageConfiguration();

    this.commandMap.put("zone", new CommandZone(this));

    this.registerCommands();

    new FindPlayerInteract(this);
    new CreatePlayerInteract(this);
    new SubcreatePlayerInteract(this);
    new PlayerQuit(this);
    new PlayerChangedWorld(this);
    new PlayerMove(this);
  }

  @Override
  public void onDisable() {
  }

  private void registerCommands() {
    this.commandMap.forEach(
      (s, e) -> {
        PluginCommand pluginCommand = getCommand(s);
        if (pluginCommand == null) {
          return;
        }
        pluginCommand.setExecutor(e);
        pluginCommand.setTabCompleter(e);
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
