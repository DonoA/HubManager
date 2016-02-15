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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.EventExecutor;

/**
 *
 * @author donoa_000
 */
public class StaffLogin implements CommandExecutor, Listener{
    
    @Getter
    private static final Permission staffPermission = new Permission("hubmanager.staff", PermissionDefault.FALSE);
    
    @Getter
    private static final Permission adminPermission = new Permission("hubmanager.admin", PermissionDefault.OP);
    
    @Getter
    private static final Permission loggedInPermission = new Permission("hubmanager.loggedin", PermissionDefault.FALSE);
    
    @Getter
    private static HashMap<String, PlayerDat> playerData = new HashMap<>();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        if(cmd.getName().equalsIgnoreCase("login") && args.length > 0){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(p.hasPermission(staffPermission) || p.hasPermission(adminPermission)){
                    if(playerData.get(p.getName()).checkPassword(args[0])){
                        playerData.get(p.getName()).getKnownIPs().add(p.getAddress().toString().replace("/", ""));
                        DBmanager.saveObj(playerData.get(p.getName()), new File(HubManager.getPluginDirectory() + HubManager.getFileSep() + 
                        "PlayerData" + HubManager.getFileSep()), playerData.get(p.getName()).getUUID().toString());
                        p.addAttachment(HubManager.getInstance(), loggedInPermission.getName(), true);
                    }else{
                        sender.sendMessage(HubManager.getPrefix() + ChatColor.RED + "Incorrect password!");
                    }
                }
            }else{
                sender.sendMessage(HubManager.getPrefix() + ChatColor.RED + "Only players can login!");
            }
        }else if(cmd.getName().equalsIgnoreCase("password") && args.length > 1){
            if(args[0].equalsIgnoreCase("set") && sender instanceof Player){
                Player p = (Player) sender;
                if(p.hasPermission(loggedInPermission)){
                    playerData.get(p.getName()).setPassword(PlayerDat.encryptPassword(args[1]));
                    DBmanager.saveObj(playerData.get(p.getName()), new File(HubManager.getPluginDirectory() + HubManager.getFileSep() + 
                        "PlayerData" + HubManager.getFileSep()), playerData.get(p.getName()).getUUID().toString());
                }
            }else if(args[0].equalsIgnoreCase("reset") && args.length > 2){
                if(sender instanceof ConsoleCommandSender){
                    Object save = DBmanager.loadObj(PlayerDat.class, HubManager.getPluginDirectory() + HubManager.getFileSep() + 
                    "PlayerData" + HubManager.getFileSep() + args[1] + ".json");
                        if(save.equals(false)){
                            sender.sendMessage(HubManager.getPrefix() + ChatColor.RED + "No such user has played!");
                        }else{
                            PlayerDat pd = (PlayerDat) save;
                            pd.setPassword(PlayerDat.encryptPassword(args[2]));
                            DBmanager.saveObj(pd, new File(HubManager.getPluginDirectory() + HubManager.getFileSep() + 
                            "PlayerData" + HubManager.getFileSep()), pd.getUUID().toString());
                        }
                }else{
                    sender.sendMessage(HubManager.getPrefix() + ChatColor.RED + "Password reset only allowed from console!");
                }
            }
        }
        return true;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(e.getPlayer().hasPermission(staffPermission) || e.getPlayer().hasPermission(adminPermission)){
            Object save = DBmanager.loadObj(PlayerDat.class, HubManager.getPluginDirectory() + HubManager.getFileSep() + 
                    "PlayerData" + HubManager.getFileSep() + e.getPlayer().getUniqueId() + ".json");
            if(save.equals(false)){
                PlayerDat pd = new PlayerDat();
                pd.setUUID(e.getPlayer().getUniqueId());
                pd.setPassword(PlayerDat.encryptPassword("password"));
                DBmanager.saveObj(pd, new File(HubManager.getPluginDirectory() + HubManager.getFileSep() + 
                    "PlayerData" + HubManager.getFileSep()), pd.getUUID().toString());
                playerData.put(e.getPlayer().getName(), pd);
            }else{
                playerData.put(e.getPlayer().getName(), (PlayerDat) save);
            }
        }
    }
    
    //STILL NEED TO ADD ALL LISTENERS FOR RESTRICTIONS
    
    private static class PlayerDat{
        @Getter @Setter
        private ArrayList<String> knownIPs;
        
        @Getter @Setter
        private UUID UUID;
        
        @Getter @Setter
        private String Password;
        
        public PlayerDat(){}
        
        public boolean checkPassword(String password){
            return Password.equals(encryptPassword(password));
        }
        
        public static String encryptPassword(String password){
            return password;
        }
    }
}