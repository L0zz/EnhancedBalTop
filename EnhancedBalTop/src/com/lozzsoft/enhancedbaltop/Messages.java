package com.lozzsoft.enhancedbaltop;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;


public class Messages
{
    private static final String messagesFileName = "messages.yml";
    
    private static HashMap<String, String> messages = new HashMap<String, String>();
    private static HashMap<String, String> overrides = new HashMap<String, String>();
    private static String cmdPreFix = "";
    
    
    public Messages(Plugin tmpPlugin)
    { 
    	ConsoleCommandSender tmpConsole = tmpPlugin.getServer().getConsoleSender();
    	YamlConfiguration messagesYMLConfig;
    	boolean replaceMsgs = false;
    	
    	if (! tmpPlugin.getDataFolder().exists()) 
    	{
            tmpPlugin.getDataFolder().mkdirs();
        }
    	 
        File tmpMessagesFile = new File(tmpPlugin.getDataFolder(), messagesFileName);
         
        if (! tmpMessagesFile.exists()) 
        {
        	tmpPlugin.saveResource(messagesFileName, false);
        	replaceMsgs = true;
        }
       
        messagesYMLConfig = loadMessagesFile(tmpMessagesFile,tmpPlugin,false);
        
        //Added to replace messages file if less than version 1.5
        if ( messagesYMLConfig.getKeys(false).contains("version"))
        {
        	String msgVersion = messagesYMLConfig.getString("version");
        	
        	if ( Updater.checkHigher(msgVersion,"v1.5.0") )
        	{
        		//Log.info("DEBUG: messages Constr, checkHigher v1.5.0 = true");
        		tmpPlugin.saveResource(messagesFileName, true);
        		replaceMsgs = true;
        	}
        }
        else
        {
    		tmpPlugin.saveResource(messagesFileName, true);
    		replaceMsgs = true;
        }
        
        messagesYMLConfig = loadMessagesFile(tmpMessagesFile,tmpPlugin,true);
        
        if ( messages.containsKey("cmdprefix"))
    	{
    		cmdPreFix=messages.get("cmdprefix");
    	}
    	else
    	{
    		cmdPreFix="&7[&eEnh&6BalTop&7] ";
    	}

        if ( replaceMsgs)
        {
        	String msgVersion2 = messagesYMLConfig.getString("version");
        	tmpConsole.sendMessage(getMessage("replacemsgs", messagesFileName, msgVersion2));
        }
        
    }

    
    private YamlConfiguration loadMessagesFile(File tmpMessagesFile, Plugin tmpPlugin, Boolean checkDefault)
    {
    	ConsoleCommandSender tmpConsole = tmpPlugin.getServer().getConsoleSender();
    	
    	YamlConfiguration tmpMessagesYMLConfig = new YamlConfiguration();
  
    	try 
    	{
    		tmpMessagesYMLConfig.load(tmpMessagesFile);
    	} 
    
    	catch (FileNotFoundException e1) 
    	{
    		e1.printStackTrace();
    		tmpPlugin.getServer().getPluginManager().disablePlugin(tmpPlugin);
    	} 
    
    	catch (IOException e1) 
    	{
    		e1.printStackTrace();
    		tmpPlugin.getServer().getPluginManager().disablePlugin(tmpPlugin);
    	}
    	
    	catch (InvalidConfigurationException e1) 
    	{
    		e1.printStackTrace();
    		tmpPlugin.getServer().getPluginManager().disablePlugin(tmpPlugin);
    	}
    	
    	if (checkDefault)
    	{
    		InputStream messagesStream = tmpPlugin.getResource(messagesFileName);
    		Reader messagesReader = new InputStreamReader(messagesStream);
    		YamlConfiguration defaultMessages = YamlConfiguration.loadConfiguration(messagesReader);
        
    		boolean saveMessagesFile = false;
        
    		for ( String key : defaultMessages.getKeys(false) )
    		{
    			if ( ! tmpMessagesYMLConfig.getKeys(false).contains(key) )
    			{
    				tmpMessagesYMLConfig.addDefault(key, defaultMessages.get(key));
    				tmpMessagesYMLConfig.set(key, defaultMessages.get(key));
    				saveMessagesFile = true;
        		
    				tmpConsole.sendMessage("§7[§eEnh§6BalTop§7] §cAdding Missing Key: §a " + key + " §cTo §a" + messagesFileName);
    			}
        	
    			messages.put(key, tmpMessagesYMLConfig.getString(key));
    		}
        
    		if ( saveMessagesFile )
    		{
    			try 
    			{
    				tmpMessagesYMLConfig.save(tmpMessagesFile);
    			}
    			catch (IOException e) 
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	
    	return tmpMessagesYMLConfig;
    }
    
    
    public static void setOverrides(HashMap<String, String> tmpOverrides) 
    {
        overrides = tmpOverrides;
    }

    
    public static String getMessage(String key, Object... replacements) 
    {     
    	if ( key == "")
    		return cmdPreFix.replace("&", "§").replace("§§", "&");
    	
        if ( ! messages.containsKey(key)) 
        {
        	ConsoleCommandSender tmpConsole = Bukkit.getConsoleSender();
            tmpConsole.sendMessage(cmdPreFix.replace("&", "§").replace("§§", "&") + "§cUnknown Message Key:§a " + key);
            return "";
        }
        
        String format;
        
        if ( overrides != null && overrides.containsKey(key)) 
        {
            format = cmdPreFix + overrides.get(key);
        } 
        else 
        {
            format = cmdPreFix + messages.get(key);
        }
        
        for (int i = 0; i < replacements.length; i++) 
        {
            format = format.replace("%" + i + "%", replacements[i].toString());
        }
        
        format = format.replace("&", "§").replace("§§", "&");
        return format;
    }
}
