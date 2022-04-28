// 
// Private License
// 
// Copyright (c) 2019-2020 Joel Strasser <strasser999@gmail.com>
// 
// Only the copyright holder is allowed to use this software.
// 
package at.joestr.zonemenu.configuration;

import com.vdurmont.semver4j.Semver;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joel
 */
public class JenkinsUpdater {
	
	public enum State {
		ERROR_OFF,
		SUCCESS_UPTODATE,
		SUCCESS_AVAILABLE,
		SUCCES_DOWNLOADED
	}
	
	public class Update {
		private Semver currentVersion;
		private Semver newVersion;
		private String downloadUrl;
		private LocalDateTime expiry;

		public Update(Semver currentVersion, Semver newVersion, String downloadUrl, LocalDateTime expiry) {
			this.currentVersion = currentVersion;
			this.newVersion = newVersion;
			this.downloadUrl = downloadUrl;
			this.expiry = expiry;
		}

		public Semver getCurrentVersion() {
			return currentVersion;
		}

		public void setCurrentVersion(Semver currentVersion) {
			this.currentVersion = currentVersion;
		}

		public Semver getNewVersion() {
			return newVersion;
		}

		public void setNewVersion(Semver newVersion) {
			this.newVersion = newVersion;
		}

		public String getDownloadUrl() {
			return downloadUrl;
		}

		public void setDownloadUrl(String downloadUrl) {
			this.downloadUrl = downloadUrl;
		}

		public LocalDateTime getExpiry() {
			return expiry;
		}

		public void setExpiry(LocalDateTime expiry) {
			this.expiry = expiry;
		}
	}
	
	private final boolean enabled;
	private final boolean download;
	private final Semver currentVersion;
	private final String targetUrl;
	private final String classifier;
	private final File downloadFolder;
	
	private final String pomPropsUrl;
	private final Properties pomProps;
	private Update lastUpdate;

	/**
	 * Creates an instance of this updater
	 * 
	 * @param enabled If the updater is enabled.
	 * @param download If an attempt to download the update should be made.
	 * @param currentVersion The version currently running.
	 * @param targetUrl The URL of the target folder after a Maven build.
	 * @param pomProperties Where the pom.properties-file is located.
	 * @param classifier The classifier of this software (like 'shaded' etc.)
	 * @param downloadFolder The folder where the download should be placed.
	 */
	public JenkinsUpdater(boolean enabled, boolean download, String currentVersion, String targetUrl, String pomProperties, String classifier, File downloadFolder) {
		this.enabled = enabled;
		this.download = download;
		this.currentVersion = new Semver(currentVersion, Semver.SemverType.IVY);
		this.targetUrl = targetUrl;
		this.classifier = classifier;
		this.downloadFolder = downloadFolder;
		
		this.pomPropsUrl = new StringBuilder(targetUrl).append(pomProperties).toString();
		this.pomProps = new Properties();
		this.lastUpdate = null;
	}
	
	public CompletableFuture<State> checkForUpdate() {
		return CompletableFuture.supplyAsync(() -> {
			if (!enabled) return State.ERROR_OFF;
			
			if ((lastUpdate == null) || (lastUpdate.getExpiry().isBefore(LocalDateTime.now()))) {
				downloadPomProperties(pomPropsUrl);
			
				Semver newVersion =
					new Semver(pomProps.getProperty("version", "0.1.0-SNAPSHOT"), Semver.SemverType.IVY);

				if (!newVersion.isGreaterThan(currentVersion)) return State.SUCCESS_UPTODATE;

				lastUpdate = new Update(
						currentVersion,
						newVersion,
						new StringBuilder(targetUrl)
							.append(pomProps.getProperty("artifactId"))
							.append("-")
							.append(newVersion.getOriginalValue())
							.append(classifier.equalsIgnoreCase("") ? "" : "-" + classifier)
							.append(".jar")
							.toString(),
						LocalDateTime.now().plusHours(12)
					);
			}
			
			if (!download) {
				return State.SUCCESS_AVAILABLE;
			}
			
			if (download) {
				this.downloadUpdateTo(downloadFolder);
				return State.SUCCES_DOWNLOADED;
			}
			
			return State.ERROR_OFF;
		});
	}
	
	private void downloadPomProperties(String pomPropertiesUrl) {
		try (InputStream inputStream = new URL(pomPropertiesUrl).openStream()) {
			this.pomProps.load(inputStream);
		} catch (IOException ex) {
			Logger.getGlobal().log(Level.SEVERE, "", ex);
		}
	}
	
	private boolean downloadUpdateTo(File folder) {
		boolean result = false;
		
		if (!folder.exists() || !folder.canWrite())
			return result;

		URL downloadUrl = null;
		try {
			 downloadUrl = new URL(lastUpdate.getDownloadUrl());
		} catch (MalformedURLException ex) {
			Logger.getLogger(JenkinsUpdater.class.getName()).log(Level.SEVERE, null, ex);
		}
		if (downloadUrl == null) return result;

		try (InputStream newFile = downloadUrl.openStream()) {
			Files.copy(
				newFile,
				new File(folder, pomProps.getProperty("artifactId") + ".jar").toPath(),
				StandardCopyOption.REPLACE_EXISTING
			);
		} catch (IOException ex) {
			Logger.getLogger(JenkinsUpdater.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		result = true;

		return result;
	}

	public Update getLastUpdate() {
		return lastUpdate;
	}
}
