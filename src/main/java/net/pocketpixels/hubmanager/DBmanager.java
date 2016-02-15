/*
 * This file is part of DBmanager.
 * 
 * MCME-Events is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MCME-Events is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MCME-Events.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package net.pocketpixels.hubmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import lombok.Getter;

/**
 *
 * @author Donovan <dallen@dallen.xyz>
 * Dallen's personal JSON database manager
 */
public class DBmanager {
    
    @Getter
    private static ObjectMapper JSonParser = new ObjectMapper();
    
    /**
     *Saves the object in JSON to the given folder and with the given name
     * @param obj The object to be saved
     * @param loc the folder to save the object into
     * @param name the name of the file for the object save
     * @return returns if the file saved
     */
    public static boolean saveObj(Object obj, File loc, String name){
        if(!loc.exists()){
            loc.mkdirs();
        }
        boolean success = true;
        File locStart = new File(loc + HubManager.getFileSep() + name + ".new");
        File locEnd = new File(loc + HubManager.getFileSep() + name);
        try {
           JSonParser.writeValue(locStart, obj);
        } catch (IOException ex) {
            success = false;
        } finally {
            if (success) {
                if (locEnd.exists()) {
                    locEnd.delete();
                }
                locStart.renameTo(locEnd);
            }
        }
        return success;
    }
    
    /**
     *Loads the object of class type out of JSON from the given file
     * @param type The class of the object to be loaded
     * @param loc the location of the file to load the data from
     * @return returns the loaded object as an Object
     */
    public static Object loadObj(Class type, File loc){
        if(!loc.exists()){
            loc.mkdirs();
            return false;
        }
        Object obj;
        try {
            obj = JSonParser.readValue(loc, type);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return obj;
    }
    
    /**
     *Loads the object of class type out of JSON from the given file
     * @param type The class of the object to be loaded
     * @param path the location of the file to load the data from as a String
     * @return returns the loaded object as an Object
     */
    public static Object loadObj(Class type, String path){
        File loc = new File(HubManager.getPluginDirectory() + HubManager.getFileSep() + path);
        if(!loc.exists()){
            loc.mkdirs();
            return false;
        }
        Object obj;
        try {
            obj = JSonParser.readValue(loc, type);
        } catch (Exception ex) {
//            ex.printStackTrace();
            return false;
        }
        return obj;
    }
    
    /**
     *Loads all the objects of class type out of JSON from the given folder
     * @param type The class of the objects to be loaded
     * @param loc the location of the folder to load the data from
     * @return returns a HashMap of the file names and the Objects loaded
     */
    public static HashMap<String, Object> loadAllObj(Class type, File loc){
        if(!loc.exists()){
            loc.mkdirs();
            return null;
        }
        HashMap<String, Object> rtn = new HashMap<String, Object>();
        for(File f : loc.listFiles()){
            rtn.put(f.getName(), loadObj(type, f));
        }
        return rtn;
    }
}
