package com.hazee.hypertp.util;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FileUtil {
    
    public static void createFileIfNotExists(File file) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void createFileFromResource(JavaPlugin plugin, String resourcePath, File outputFile) {
        if (!outputFile.exists()) {
            try (InputStream in = plugin.getResource(resourcePath)) {
                if (in != null) {
                    outputFile.getParentFile().mkdirs();
                    Files.copy(in, outputFile.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}