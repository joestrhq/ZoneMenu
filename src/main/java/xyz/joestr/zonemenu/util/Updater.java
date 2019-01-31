package xyz.joestr.zonemenu.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class Updater {

    private String updateURI = "";
    private String donwloadURI = "";
    private String thisVersion = "";

    public Updater(String updateURI, String thisVersion) {

        this.updateURI = updateURI;
        this.thisVersion = thisVersion;
    }

    public boolean isUpdateAvailable() throws IOException {

        Properties pomProperties = new Properties();

        URL updateURL = new URL(updateURI);

        URLConnection updateURLConnection = updateURL.openConnection();

        BufferedReader updateBufferedReader = new BufferedReader(
            new InputStreamReader(
                updateURLConnection.getInputStream()
            )
        );

        pomProperties.load(updateBufferedReader);

        updateBufferedReader.close();

        String version = pomProperties.getProperty("version");

        return version.equals(thisVersion);
    }

    public void asyncIsUpdateAvailable(BiConsumer<Boolean, Throwable> whenComplete) {

        CompletableFuture.supplyAsync(
            () -> {

                boolean result = false;

                try {

                    result = this.isUpdateAvailable();
                } catch (IOException e) {

                    e.printStackTrace();
                }

                return result;
            }
        ).whenComplete(
            whenComplete
        );
    }

    public String getUpdateURI() {
        return updateURI;
    }

    public void setUpdateURI(String updateURI) {
        this.updateURI = updateURI;
    }

    public String getDonwloadURI() {
        return donwloadURI;
    }

    public void setDonwloadURI(String donwloadURI) {
        this.donwloadURI = donwloadURI;
    }

    public String getThisVersion() {
        return thisVersion;
    }

    public void setThisVersion(String thisVersion) {
        this.thisVersion = thisVersion;
    }
}
