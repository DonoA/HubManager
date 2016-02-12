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
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
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
    private MenuOption[] Options;
    private int size;
   
    public InventoryMenu(String name, MenuOption[] options, int size){
        this.size = size;
        this.name = name;
        this.Options = options;
        Bukkit.getServer().getPluginManager().registerEvents(this, HubManager.getInstance());
    }
   
    public void sendMenu(Player player){
        Inventory inventory = Bukkit.createInventory(player, size*9, name);
        for(MenuOption m : Options){
            inventory.setItem((m.Y*9) + m.X, m.getIcon());
        }
        player.openInventory(inventory);
    }
   
    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent e){
        if (e.getInventory().getTitle().equals(name)){
            e.setCancelled(true);
            int slot = e.getRawSlot();
            for(MenuOption m : Options){
                if((m.Y*9) + m.X == slot){
                    final Player p = (Player)e.getWhoClicked();
                    m.exec(p);
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
   
    private static ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore){
        ItemMeta im = item.getItemMeta();
            im.setDisplayName(name);
            im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
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
        
        public MenuOption(String name, Material icon, String[] subtext, String cmd, int x, int y){
            X = x;
            Y = y;
            this.OptionName = name;
            this.Command = cmd.split(" ");
            this.Icon = InventoryMenu.setItemNameAndLore(new ItemStack(icon), name, subtext);
        }
        
        public void exec(Player p){
            if(Command[0].equalsIgnoreCase("message")){
                String msg = "";
                for(int i = 1; i < Command.length; i++){
                    msg += Command[i] + " ";
                }
                p.sendMessage(msg);
            }else if(Command[0].equalsIgnoreCase("connect")){
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(Command[1]);
                p.sendPluginMessage(HubManager.getInstance(), "BungeeCord", out.toByteArray());
            }
        }
    }
}
