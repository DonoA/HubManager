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
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 *
 * @author donoa_000
 */
public class HubManager extends JavaPlugin implements PluginMessageListener, CommandExecutor {
    
    @Getter
    private static JavaPlugin instance;
    
    @Getter
    private static HashMap<String, InventoryMenu> Menus = new HashMap<>();
    
    @Getter
    private static HashMap<String, Integer> ServerCount = new HashMap<>();
    
    @Getter
    private final static String prefix = ChatColor.YELLOW + "[" + ChatColor.BLUE + "HubManager" + ChatColor.YELLOW + "]" + ChatColor.RESET;
    
    @Getter
    private static String FileSep = System.getProperty("file.separator");
    
    @Getter
    private static File pluginDirectory;
    
    @Getter
    private static Runnable getCurrent = new Runnable(){
            @Override
            public void run(){
                if(!Bukkit.getOnlinePlayers().isEmpty()){
                    for(String server : ServerCount.keySet()){
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("PlayerCount");
                        out.writeUTF(server);
                        ((Player)Bukkit.getOnlinePlayers().toArray()[0]).sendPluginMessage(HubManager.getInstance(), "RedisBungee", out.toByteArray());
                    }
                }
                InventoryMenu.updateItems();
            }
        };
    
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        PluginManager pm = Bukkit.getPluginManager();
        StaffLogin sl = new StaffLogin();
        pm.registerEvents(new MenuHandler(), this);
        pm.registerEvents(sl, this);
        this.pluginDirectory = getDataFolder();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "RedisBungee");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "RedisBungee", this);
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, getCurrent, 10, 20 * 2);
        instance = this;
        getCommand("ReloadMenus").setExecutor(this);
        getCommand("login").setExecutor(sl);
        getCommand("password").setExecutor(sl);
        loadMenus();
        for(String s : getConfig().getStringList("servers")){
            ServerCount.put(s, -1);
        }
    }
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
      if (!channel.equals("RedisBungee")) {
        return;
      }
      ByteArrayDataInput in = ByteStreams.newDataInput(message);
      String subchannel = in.readUTF();
      if(subchannel.equals("PlayerCount")){
          try {
            ServerCount.put(in.readUTF(), in.readInt());
          }catch(Exception ex){}
      }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        if(cmd.getName().equalsIgnoreCase("reloadmenus") && sender.isOp()){
            Menus.clear();
            loadMenus();
        }
        return true;
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
                        Items[j] = new InventoryMenu.MenuOption(ChatColor.GOLD + i.name, new ItemStack(i.icon, 1, (short) 0, i.itemdat), i.lore, i.command, i.X, i.Y);
                        j++;
                    }
                    ItemStack menuIcon = new ItemStack(Material.valueOf(f.getName().replace(".menu", "")));
                    InventoryMenu.setItemNameAndLore(menuIcon, menu.itemName, new String[] {});
                    Menus.put(f.getName().replace(".menu", ""), new InventoryMenu(menu.title, Items, menu.size, menuIcon, menu.slot));
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
        private int slot;
        
        @Setter
        private Item[] items;

        @Setter
        private String itemName;
                
        private static class Item{
            @Setter
            private String name;
            
            @Setter
            private int icon;
            
            @Setter
            private byte itemdat;
            
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
