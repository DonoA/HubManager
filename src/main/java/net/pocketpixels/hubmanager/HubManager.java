/*
 * This file is part of HubManager.
 * 
 * HubManager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * HubManager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with HubManager.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package net.pocketpixels.hubmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;
import net.pocketpixels.hubmanager.InventoryMenu.MenuOption;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 *
 * @author donoa_000
 */
public class HubManager extends JavaPlugin implements PluginMessageListener {
    
    @Getter
    private static JavaPlugin instance;
    
    @Getter
    private static HashMap<String, InventoryMenu> Menus = new HashMap<>();
    
    @Override
    public void onEnable() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new CompassHandler(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        instance = this;
        loadMenus();
    }
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
      if (!channel.equals("BungeeCord")) {
        return;
      }
      ByteArrayDataInput in = ByteStreams.newDataInput(message);
      String subchannel = in.readUTF();
    }
    
    private void loadMenus(){
        if(!getDataFolder().exists()){
            getDataFolder().mkdir();
        }
        for(File f : getDataFolder().listFiles()){
            if(f.getName().endsWith(".menu")){
                try {
                    ObjectMapper JSonParser = new ObjectMapper();
                    Menu menu = JSonParser.readValue(f, Menu.class);
                    InventoryMenu.MenuOption[] Items = new InventoryMenu.MenuOption[menu.items.length];
                    int j = 0;
                    for(Menu.Item i : menu.items){
                        Items[j] = new InventoryMenu.MenuOption(i.name, Material.valueOf(i.icon), i.lore, i.command, i.X, i.Y);
                        j++;
                    }
                    Menus.put(f.getName().replace(".menu", ""), new InventoryMenu(menu.title, Items, menu.size));
                } catch (IOException ex) {
                    Logger.getLogger(HubManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private static class Menu{
        @Setter
        private String title;
        
        @Setter
        private int size;
        
        @Setter
        private Item[] items;
                
        private static class Item{
            @Setter
            private String name;
            
            @Setter
            private String icon;
            
            @Setter
            private String[] lore;
            
            @Setter
            private String command;
            
            @Setter
            private int X;
            
            @Setter
            private int Y;
        }
    }
}
