package xyz.joestr.zonemenu.util;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * YMLDelegate class which can handle functions for YML-Files.
 * @author Joel
 * @since build_1
 * @version build_5
 */
public class YMLDelegate {
	
	Plugin plugin = null;
	Map<String, Object> hashMap = new HashMap<String, Object>();
	String configurationSection = "";
	String fileName = "";
	
	/**
	 * Create a YMLDelegate instace.
	 * @author joestr
	 * @since build_1
	 * @param plugin Instace of a plugin.
	 * @param configurationSection Name of the configuration section.
	 * @param fileName Name of the file.
	 */
	public YMLDelegate(Plugin plugin, String configurationSection, String fileName) {
		
		this.plugin = plugin;
		this.configurationSection = configurationSection;
		this.fileName = fileName;
	}
	
	/**
	 * Get the name of the file.
	 * @author joestr
	 * @since build_1
	 * @return Name of the file.
	 */
	public String getFileName() { return this.fileName; }
	
	/**
	 * Get the configuration section as a map.
	 * @author joestr
	 * @since build_1
	 * @return Map
	 */
	public Map<String, Object> getMap() { return this.hashMap; }
	
	/**
	 * Set the configuration section with a map
	 * @author joestr
	 * @since build_1
	 * @param map Map
	 */
	public void setMap(Map<String, Object> map) { this.hashMap = map; }
	
	/**
	 * Get the configuration section as a map.
	 * @deprecated
	 * @see YMLDelegate#getMap()
	 * @author joestr
	 * @return Map
	 */
	public Map<String, Object> Map() { return this.getMap(); }
	
	/**
	 * Set the configuration section with a map
	 * @deprecated
	 * @see YMLDelegate#setMap(Map)
	 * @author joestr
	 * @since build_1
	 * @param map Map
	 */
	public void Map(Map<String, Object> map) { this.setMap(map); }
	
	/**
	 * Load the file, get the configuration section and put it into a map.
	 * @author joestr
	 * @since build_1
	 */
	public void Load() {
		
	    try {
	    	
	    	FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), fileName));
	    	hashMap.putAll(fileConfiguration.getConfigurationSection(configurationSection).getValues(true));
	    } catch (Exception exception) {
	    	
	        exception.printStackTrace();
	    }
	}
	
	/**
	 * Save the map to the file.
	 * @author joestr
	 * @since build_1
	 */
	public void Save() {

		try {
			
			FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), fileName));
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
	 * @author joestr
	 * @since build_1
	 */
	public void Reset() {
		
		try {
			
			this.plugin.saveResource(fileName, true);
		} catch(Exception exception) {
			
			exception.printStackTrace();
		}
		
		Load();
	}
	
	/**
	 * Create a new file.
	 * @author joestr
	 * @since build_1
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
	 * @author joestr
	 * @since build_1
	 */
	public boolean Exist() {
		
		boolean returnVariable = false;
		
		File file = null;
		
		try {
			
			file = new File(this.plugin.getDataFolder(), fileName);
			
			if(file.exists()) {
				
				returnVariable = true;
			}
		} catch (NullPointerException exception) {
			
			exception.printStackTrace();
		} catch (SecurityException exception) {
			
			exception.printStackTrace();
		} catch (Exception exception) {
			
			exception.printStackTrace();
		}
		
		return returnVariable;
	}
	
	/**
	 * Check if loaded file contains the same entries as the ressource file.
	 * @author joestr
	 * @since build_1
	 */
	public boolean Check() {
		
		boolean returnVariable = true;
		
		Reader reader = null;
		
		try {
			
			reader = new InputStreamReader(this.plugin.getResource(fileName), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		} catch (Exception exception) {
			
			exception.printStackTrace();
		}
		
		for(String s : YamlConfiguration.loadConfiguration(reader).getConfigurationSection(configurationSection).getKeys(true)) {
							
			if(!this.hashMap.containsKey(s)) {
				
				returnVariable = false;
				break;
			}
		}

		
		return returnVariable;
	}
	
	/**
	 * Check if loaded file contains the same entries as the ressource file and if not add the missing entries to the map.
	 * @author joestr
	 * @since build_1
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
		
		ConfigurationSection configurationsection = YamlConfiguration.loadConfiguration(reader).getConfigurationSection(configurationSection);
		
		for(String s : configurationsection.getKeys(true)) {
							
			if(!this.hashMap.containsKey(s)) {
				
				Bukkit.getLogger().log(Level.WARNING, "File: " + this.getFileName() + "; Missing entry: " + s + "; (using from default config)");
				this.hashMap.put(s, configurationsection.get(s));
				
				if(!addedEntry) { addedEntry = true; }
			}
		}
		
		return addedEntry;
	}
}
