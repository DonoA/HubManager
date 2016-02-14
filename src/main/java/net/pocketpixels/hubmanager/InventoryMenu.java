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

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Banner;


/**
 *
 * @author donoa_000
 */
public class InventoryMenu implements Listener{
    private String name;
    @Getter
    private MenuOption[] Options;
    @Getter
    private int size;
    @Getter
    private int slot;
    @Getter
    private Inventory inven;
    @Getter
    private ItemStack icon;
    
    public static ArrayList<InventoryMenu> openMenus = new ArrayList<>();
   
    public InventoryMenu(String name, MenuOption[] options, int size, ItemStack icon, int slot){
        this.slot = slot;
        this.icon = icon;
        this.size = size;
        this.name = name;
        this.Options = options;
        Bukkit.getServer().getPluginManager().registerEvents(this, HubManager.getInstance());
    }
   
    public void sendMenu(Player player){
        if(Options.length != 1){
            inven = Bukkit.createInventory(player, size*9, name);
            for(MenuOption m : Options){
                if(m.autoUpdate){
                    m.updateLore();
                }
                inven.setItem((m.Y*9) + m.X, m.getIcon());
            }
            openMenus.add(this);
            player.openInventory(inven);
        }else{
            Options[0].exec(player);
        }
    }
   
    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent e){
        if (e.getInventory().getTitle().equals(name)){
            e.setCancelled(true);
            int slot = e.getRawSlot();
            for(MenuOption m : Options){
                if((m.Y*9) + m.X == slot){
                    final Player p = (Player)e.getWhoClicked();
                    if(m.exec(p)){
                        openMenus.remove(this);
                        inven = null;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(HubManager.getInstance(), new Runnable(){
                            @Override
                            public void run(){
                                p.closeInventory();
                            }
                        }, 1);
                    }
                }
            }
        }
    }
   
    public static ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore){
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }
    
    public static void updateItems(){
        for(InventoryMenu im : (ArrayList<InventoryMenu>) openMenus.clone()){
            if(im.getInven() != null){
                for(MenuOption mo : im.getOptions()){
                    mo.updateLore();
                    im.getInven().setItem((mo.getY()*9)+mo.getX(), mo.getIcon());
                }
            }else{
                openMenus.remove(im);
            }
        }
    }
    
    public static class MenuOption{
        @Getter
        private String OptionName;
        @Getter
        private ItemStack Icon;
        @Getter
        private String[] Command;
        @Getter @Setter
        private Banner BannerIcon;
        @Getter
        private int X;
        @Getter
        private int Y;
        @Getter
        private boolean autoUpdate = false;
        @Getter
        private String[] subtext;
        
        public MenuOption(String name, ItemStack icon, String[] subtext, String cmd, int x, int y){
            X = x;
            Y = y;
            this.OptionName = name;
            this.Command = cmd.split(" ");
            this.subtext = subtext;
            for(String s : subtext){
                if(s.contains("{{") && s.contains("}}")){
                    this.autoUpdate = true;
                }
            }
            this.Icon = InventoryMenu.setItemNameAndLore(icon, name, subtext);
        }
        
        public boolean exec(Player p){
            if(Command[0].equalsIgnoreCase("message")){
                String msg = "";
                for(int i = 1; i < Command.length; i++){
                    msg += Command[i] + " ";
                }
                p.sendMessage(msg);
                return true;
            }else if(Command[0].equalsIgnoreCase("connect")){
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(Command[1]);
                p.sendPluginMessage(HubManager.getInstance(), "BungeeCord", out.toByteArray());
                return true;
            }else if(Command[0].equalsIgnoreCase("noclose")){
                return false;
            }
            return false;
        }
        
        public void updateLore(){
            List<String> lore = Icon.getItemMeta().getLore();
            for(int i = 0; i < subtext.length; i++){
                if(subtext[i].contains("{{current}}")){
                    String line = subtext[i];
                    lore.set(i, line.replaceAll("\\{\\{current\\}\\}", ChatColor.GREEN + String.valueOf(HubManager.getServerCount().get(Command[1])) + ChatColor.DARK_PURPLE + ChatColor.ITALIC));
                }
            }
            ItemMeta im = Icon.getItemMeta();
            im.setLore(lore);
            Icon.setItemMeta(im);
        }
    }
}
