/*
 * This file is part of BuildingFall.
 * 
 * BuildingFall is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * BuildingFall is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with BuildingFall.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package net.pocketpixels.hubmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import org.bukkit.Bukkit;

/**
 *
 * @author donoa_000
 */
public class GetTopDonors {
    
    public static HashMap<String, Integer> getDonors(){
        HashMap<String, Integer> Donors = new HashMap<>();
        try {
            URLConnection conn = new URL("http://pocketpixels.net/vote/_Incapsula_Resource?CWUDNSAI=9&xinfo=9-18932589-0 0NNN RT(1455247352698 3) q(0 -1 -1 -1) r(0 -1) B12(8,881021,0) U19&incident_id=261001080031309665-146077094794036937&edet=12&cinfo=08000000").openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.103 Safari/537.36");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String name = null;
            int votes = -1;
            String in;
            while ((in = br.readLine()) != null){
                Bukkit.getLogger().info(in);
                if(in.contains("<a class=\"player-name\">")){
                    name = in.replace("<a class=\"player-name\">", "").replace("</a>", "");
                    Bukkit.getLogger().info(name);
                }
                if(in.contains("<div class=\"vote-count\">")){
                    votes = Integer.parseInt(in.replace("<div class=\"vote-count\">", "").replace("</div>", ""));
                    Bukkit.getLogger().info(String.valueOf(votes));
                }
                if(name != null && votes != -1){
                    Donors.put(name, votes);
                    name = null;
                    votes = -1;
                }
            }
            br.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Donors;
    }
}
