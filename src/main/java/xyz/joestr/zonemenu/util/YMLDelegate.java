package xyz.joestr.zonemenu.util;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * YMLDelegate class which can handle functions for YML-Files.
 *
 * @author Joel
 * @since build_1
 * @version build_7_pre_2
 */
public class YMLDelegate {

    Plugin plugin = null;
    Map<String, Object> hashMap = new LinkedHashMap<>();
    String configurationSection = "";
    String fileName = "";

    /**
     * Create a new YMLDelegate instace
     *
     * @author joestr
     * @since build_1
     * @version build_7_pre_2
     * @param plugin {@linkplain Plugin} Instace of a plugin
     * @param configurationSection {@linkplain String} Name of the configuration
     * section
     * @param fileName {@linkplain String} Name of the file
     */
    public YMLDelegate(Plugin plugin, String configurationSection, String fileName) {

        this.plugin = plugin;
        this.configurationSection = configurationSection;
        this.fileName = fileName;
    }

    /**
     * Get the name of the file.
     *
     * @author joestr
     * @since build_1
     * @version build_7_pre_2
     * @return {@linkplain String} Name of the file.
     */
    public String getFileName() {

        return this.fileName;
    }

    /**
     * Get the configuration section as a map.
     *
     * @author joestr
     * @since build_1
     * @version build_7_pre_2
     * @return {@linkplain Map}<{@linkplain String}, {@linkplain Object}> Map
     * with entries
     */
    public Map<String, Object> getMap() {

        return this.hashMap;
    }

    /**
     * Set the configuration section with a map.
     *
     * @author joestr
     * @since build_1
     * @version build_7_pre_2
     * @param map {@linkplain Map}<{@linkplain String}, {@linkplain Object}> Map
     * with entries
     */
    public void setMap(Map<String, Object> map) {

        this.hashMap = map;
    }

    /**
     * Get the configuration section as a map.
     *
     * @author joestr
     * @since build_1
     * @version build_7_pre_2
     * @return {@linkplain Map}<{@linkplain String}, {@linkplain Onject}> Map
     * with entries
     * @deprecated Use {@linkplain #getMap()} instead.
     */
    public Map<String, Object> Map() {
        return this.getMap();
    }

    /**
     * Set the configuration section with a map.
     *
     * @author joestr
     * @since build_1
     * @version build_7_pre_2
     * @param map {@linkplain Map}<{@linkplain String}, {@linkplain Object}> Map
     * with entries
     * @deprecated Use {@linkplain #setMap(Map)} instead.
     */
    public void Map(Map<String, Object> map) {
        this.setMap(map);
    }

    /**
     * Load the file, get the configuration section and put it into a map.
     *
     * @author joestr
     * @since build_1
     * @version build_7_pre_2
     */
    public void Load() {

        try {

            FileConfiguration fileConfiguration = YamlConfiguration
                .loadConfiguration(new File(this.plugin.getDataFolder(), fileName));
            hashMap.putAll(fileConfiguration.getConfigurationSection(configurationSection).getValues(true));
        } catch (Exception exception) {

            exception.printStackTrace();
        }
    }

    /**
     * Save the map to the file.
     *
     * @author joestr
     * @since build_1
     * @version build_7_pre_2
     */
    public void Save() {

        try {

            FileConfiguration fileConfiguration = YamlConfiguration
                .loadConfiguration(new File(this.plugin.getDataFolder(), fileName));
            fileConfiguration.createSection(configurationSection, hashMap);
            fileConfiguration.save(new File(this.plugin.getDataFolder(), fileName));
        } catch (NullPointerException exception) {

            exception.printStackTrace();
        } catch (Exception exception) {

            exception.printStackTrace();
        }
    }

    /**
     * Reset the file with the ressource file.
     *
     * @author joestr
     * @since build_1
     * @version build_7_pre_2
     */
    public void Reset() {

        try {

            this.plugin.saveResource(fileName, true);
        } catch (Exception exception) {

            exception.printStackTrace();
        }

        Load();
    }

    /**
     * Create a new file.
     *
     * @author joestr
     * @since version_1
     */
    public void Create() {

        try {

            this.plugin.saveResource(fileName, false);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * Check for existing file.
     *
     * @author joestr
     * @since build_1
     * @version build_7_pre_2
     * @return {@linkplain Boolean} If the file exists or not.
     */
    public boolean Exist() {

        boolean result = false;

        File file = null;

        try {

            file = new File(this.plugin.getDataFolder(), fileName);

            if (file.exists()) {

                result = true;
            }
        } catch (NullPointerException exception) {

            exception.printStackTrace();
        } catch (SecurityException exception) {

            exception.printStackTrace();
        } catch (Exception exception) {

            exception.printStackTrace();
        }

        return result;
    }

    /**
     * Check if loaded file contains the same entries as the ressource file.
     *
     * @author joestr
     * @since build_1
     * @version build_7_pre_2
     * @return {@linkplain Boolean} If the was successful or not.
     */
    public boolean Check() {

        boolean result = true;

        Reader reader = null;

        try {

            reader = new InputStreamReader(this.plugin.getResource(fileName), "UTF-8");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        } catch (Exception exception) {

            exception.printStackTrace();
        }

        for (String s : YamlConfiguration.loadConfiguration(reader).getConfigurationSection(configurationSection)
            .getKeys(true)) {

            if (!this.hashMap.containsKey(s)) {

                result = false;
                break;
            }
        }

        return result;
    }

    /**
     * Check if loaded file contains the same entries as the ressource file and
     * if not add the missing entries to the map.
     *
     * @since build_1
     * @version build_7_pre_2
     * @return {@linkplain Boolean} If an entry was added.
     */
    public boolean EntryCheck() {

        boolean addedEntry = false;

        Reader reader = null;

        try {

            reader = new InputStreamReader(this.plugin.getResource(fileName), "UTF-8");
        } catch (UnsupportedEncodingException exception) {

            exception.printStackTrace();
        } catch (Exception exception) {

            exception.printStackTrace();
        }

        ConfigurationSection configurationsection = YamlConfiguration.loadConfiguration(reader)
            .getConfigurationSection(configurationSection);

        for (String s : configurationsection.getKeys(true)) {

            if (!this.hashMap.containsKey(s)) {

                Bukkit
                    .getLogger()
                    .log(
                        Level.WARNING,
                        "File: {0}; Missing entry: {1}; (using from default config)",
                        new Object[]{this.getFileName(), s}
                    );
                this.hashMap.put(s, configurationsection.get(s));

                if (!addedEntry) {
                    addedEntry = true;
                }
            }
        }

        return addedEntry;
    }
}
