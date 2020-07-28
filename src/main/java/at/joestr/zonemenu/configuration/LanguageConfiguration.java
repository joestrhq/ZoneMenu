// 
// Private License
// 
// Copyright (c) 2019-2020 Joel Strasser <strasser999@gmail.com>
// 
// Only the copyright holder is allowed to use this software.
// 
package at.joestr.zonemenu.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joel
 */
public class LanguageConfiguration {

	private static LanguageConfiguration instance;
	private static final Logger LOG = Logger.getLogger(LanguageConfiguration.class.getName());

	private Set<Locale> languagesNotFound;

	private Map<Locale, YamlFileConfiguration> externalLanguageConfigurations;
	private Map<Locale, YamlStreamConfiguration> bundledLanguageConfigurations;
	private Locale fallback;

	private LanguageConfiguration(File externalLanguagesFolder, Map<String, InputStream> bundledLanguages, Locale fallback) throws IOException {
		this.externalLanguageConfigurations = new HashMap<>();
		this.bundledLanguageConfigurations = new HashMap<>();
		this.languagesNotFound = new HashSet<>();
		this.fallback = fallback;

		if (!externalLanguagesFolder.exists()) {
			externalLanguagesFolder.mkdirs();
		}

		for (String languageFileName : bundledLanguages.keySet()) {
			String fileName = languageFileName;
			Locale l
				= Locale.forLanguageTag(
					fileName.contains(".") ? fileName.split("\\.")[0] : fileName
				);
			bundledLanguageConfigurations.put(
				l,
				new YamlStreamConfiguration(
					bundledLanguages.get(languageFileName)
				)
			);
			File externalFile
				= new File(externalLanguagesFolder, fileName);
			if (!externalFile.exists()) {
				bundledLanguageConfigurations.get(l).saveConfigAsFile(
					externalFile
				);
			}
		}

		for (File languageFile : externalLanguagesFolder.listFiles()) {
			String fileName = languageFile.getName();
			externalLanguageConfigurations.put(
				Locale.forLanguageTag(
					fileName.contains(".") ? fileName.split("\\.")[0] : fileName
				),
				new YamlFileConfiguration(languageFile)
			);
		}
	}

	public static LanguageConfiguration getInstance(File externalLanguagesFolder, Map<String, InputStream> bundledLanguagesStream, Locale fallback) throws IOException {
		if (instance != null) {
			throw new RuntimeException("This class has already been instantiated!");
		}

		instance = new LanguageConfiguration(externalLanguagesFolder, bundledLanguagesStream, fallback);

		return instance;
	}

	public static LanguageConfiguration getInstance() {
		if (instance == null) {
			throw new RuntimeException("This class has not been instantiated yet!");
		}

		return instance;
	}

	private void externalLanguageNotFound(Locale locale) {
		if (!this.languagesNotFound.add(locale)) {
			return;
		}

		LOG.log(
			Level.WARNING,
			"The external language file {0}.yml ({1}) was not found!",
			new Object[] {locale.toLanguageTag(), locale.getDisplayName()}
		);
	}

	private void pathNotInExternalLanguage(String path, Locale locale) {
		LOG.log(
			Level.WARNING,
			"The path {0} was not found in {1}!",
			new Object[] {path, this.externalLanguageConfigurations.get(locale)}
		);
	}

	public String getString(String path, Locale locale) {
		YamlFileConfiguration configFile
			= this.externalLanguageConfigurations.get(locale);
		if (configFile != null) {
			String result
				= configFile.getString(path);
			if (result != null) {
				return result;
			} else {
				this.pathNotInExternalLanguage(path, locale);
			}
		} else {
			this.externalLanguageNotFound(locale);
		}

		YamlStreamConfiguration configStream
			= this.bundledLanguageConfigurations.get(locale);
		if (configStream != null) {
			String result
				= configStream.getString(path);
			if (result != null) {
				return result;
			}
		}

		configStream
			= this.bundledLanguageConfigurations.get(fallback);
		if (configStream != null) {
			String result
				= configStream.getString(path);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
}
