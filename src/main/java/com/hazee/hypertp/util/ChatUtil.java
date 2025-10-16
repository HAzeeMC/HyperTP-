package com.hazee.hypertp.util;

import org.bukkit.ChatColor;

public class ChatUtil {
    
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}