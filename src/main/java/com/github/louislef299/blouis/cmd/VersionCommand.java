package com.github.louislef299.blouis.cmd;

import com.github.rvesse.airline.annotations.Command;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Command(name = "version", description = "Show version information")
public class VersionCommand implements Runnable {
    @Override
    public void run() {
        String version = getVersion();
        System.out.println("blou version " + version);
    }
    
    private String getVersion() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("version.properties")) {
            if (input == null) {
                return "unknown";
            }
            Properties props = new Properties();
            props.load(input);
            return props.getProperty("version", "unknown");
        } catch (IOException e) {
            return "unknown";
        }
    }
}