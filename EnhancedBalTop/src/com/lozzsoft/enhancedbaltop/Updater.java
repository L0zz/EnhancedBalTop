package com.lozzsoft.enhancedbaltop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import org.bukkit.command.ConsoleCommandSender;

import com.lozzsoft.enhancedbaltop.Messages;

import static com.lozzsoft.enhancedbaltop.EnhancedBalTop.plugin;


public class Updater 
{
	public static int resource = 20168;
    public static String latestVersion = "";
    public static boolean updateAvailable = false;
    
    
    public static void SpigotUpdater(int resource) 
    {
        Updater.resource = resource;
    }

    /**
     * Asks spigot for the version
     */
    public static String getSpigotVersion() 
    {
        try 
        {
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + resource).getBytes("UTF-8"));
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            
            if (version.length() <= 7) 
            {
                return version;
            }
            
        } 
        catch (Exception ex) 
        {
        	ConsoleCommandSender console = plugin.getServer().getConsoleSender();
            console.sendMessage(Messages.getMessage("failcheck"));
        }
        
        return null;
    }
    
    public static boolean updateAvailable() 
    {
        return updateAvailable;
    }

    public static String getLatestVersion() 
    {
        return latestVersion;
    }
    
   
    public static String toReadable(String version) 
    {
        String[] split = Pattern.compile(".", Pattern.LITERAL).split(version.replace("v", ""));
        version = "";
        for (String s : split)
            version += String.format("%4s", s);
        return version;
    }
    
    
    public static boolean checkHigher(String currentVersion, String newVersion) 
    {
        String current = toReadable(currentVersion);
        String newVers = toReadable(newVersion);
        return current.compareTo(newVers) < 0;
    }
    
    
    public static boolean checkUpdate(String currentVersion) 
    {
        if (getLatestVersion() != "")
            return true;
        
        String version = getSpigotVersion();
         
        if (version != null) 
        {
            if (checkHigher(currentVersion, version)) 
            {
                latestVersion = version;
                updateAvailable = true;
                return true;
            }
        }
        return false;
    }

}
