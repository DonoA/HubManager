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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author donoa_000
 */
public class MenuHandler implements Listener{
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.hasItem() && HubManager.getMenus().containsKey(e.getItem().getType().toString()) && 
                (e.getAction().equals(Action.RIGHT_CLICK_AIR)||e.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
            HubManager.getMenus().get(e.getItem().getType().toString()).sendMenu(e.getPlayer());
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        final Player p = e.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(HubManager.getInstance(), new Runnable(){
            @Override
            public void run(){
                if(Bukkit.getServer().getOnlinePlayers().size() < 2){
                    p.getInventory().clear();
                    for(InventoryMenu im : HubManager.getMenus().values()){
                        p.getInventory().setItem(im.getSlot(), im.getIcon());
                    }
                    HubManager.getGetCurrent().run();
                }
            }
        }, 5);
    }
}
