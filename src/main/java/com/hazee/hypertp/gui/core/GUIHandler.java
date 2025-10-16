package com.hazee.hypertp.gui.core;

import com.hazee.hypertp.HyperTP;
import com.hazee.hypertp.gui.types.HomeListGUI;
import com.hazee.hypertp.gui.types.RTPGUI;
import com.hazee.hypertp.gui.types.TPAGUI;
import org.bukkit.entity.Player;

public class GUIHandler {
    
    private final HyperTP plugin;
    
    public GUIHandler(HyperTP plugin) {
        this.plugin = plugin;
    }
    
    public void openHomeGUI(Player player) {
        HomeListGUI homeGUI = new HomeListGUI(plugin, player);
        homeGUI.open();
    }
    
    public void openTPAGUI(Player player) {
        TPAGUI tpaGUI = new TPAGUI(plugin, player);
        tpaGUI.open();
    }
    
    public void openRTPGUI(Player player) {
        RTPGUI rtpGUI = new RTPGUI(plugin, player);
        rtpGUI.open();
    }
}