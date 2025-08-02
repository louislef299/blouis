package com.github.louislef299.blouis.cmd;

import com.github.rvesse.airline.annotations.Command;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Command(name = "version", description = "Show version information")
public class VersionCommand implements Runnable {
    @Override
    public void run() {
        Properties props = getBuildProperties();
        
        System.out.println("blou version " + props.getProperty("version", "unknown"));
        System.out.println("Built: " + props.getProperty("build.time", "unknown"));
        System.out.println("Java: " + props.getProperty("java.version", "unknown"));
        System.out.println("OS: " + props.getProperty("os.name", "unknown") + " " + props.getProperty("os.arch", "unknown"));
        System.out.println("Git: " + props.getProperty("git.commit", "unknown") + " (" + props.getProperty("git.branch", "unknown") + ")");
    }
    
    private Properties getBuildProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("version.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            // Return empty properties, getProperty() will return defaults
        }
        return props;
    }
}