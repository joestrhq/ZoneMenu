package xyz.joestr.zonemenu.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class Updater {

    @Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private String updateURI = "";
    @Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private String donwloadURI = "";
    @Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private String thisVersion = "";
    
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
        
        return version != thisVersion ? true : false;
    }
}
