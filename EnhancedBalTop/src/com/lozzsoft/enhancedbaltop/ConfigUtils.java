package com.lozzsoft.enhancedbaltop;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.lozzsoft.enhancedbaltop.EnhancedBalTop.plugin;
import static com.lozzsoft.enhancedbaltop.EnhancedBalTop.console;

public class ConfigUtils
{
	//Permission Nodes
	public static final String oldBaltopPerm = "EnhancedBalTop.baltop.baltop";
	public static final String oldAdminPerm = "EnhancedBalTop.baltop.admin";
	public static final String oldTopnbalPerm = "EnhancedBalTop.baltop.topnbal";
	public static final String oldHoloTopNPerm = "EnhancedBalTop.baltop.holotopn";
	public static final String oldExcludePerm = "EnhancedBalTop.baltop.exclude";
	public static final String oldHologramPerm = "EnhancedBalTop.baltop.hologram";

	public static final String baltopPerm = "EnhancedBalTop.baltop";
	public static final String adminPerm = "EnhancedBalTop.admin";
	public static final String topnbalPerm = "EnhancedBalTop.topnbal";
	public static final String holoTopNPerm = "EnhancedBalTop.holotopn";
	public static final String excludePerm = "EnhancedBalTop.exclude";
	public static final String hologramPerm = "EnhancedBalTop.hologram";
	public static final String enableCmdsPerm = "EnhancedBalTop.enablecmds";
	
	//Misc config values
	public static final String balTopCmd = "baltop";
	public static final int defDecimalPlaces = 2;
	public static final int defTopNBal = 10;
	public static final int defHoloUpdateInterval = 30;
	public static final int hologramSetupInterval = 30 * 20;

	//config.yml values
	public static final String confEnable = "enable";
	public static final String confCheckUpdates = "checkupdates";
	public static final String confdefTopN = "deftopn";
	public static final String confPageSize = "pagesize";
	public static final String confExcludePermNode = "excludepermnode";
	public static final String confUseMoneyDenom = "usemoneydenominations";
	public static final String confEnableUUID = "displayuuid";
	public static final String confDecimalPlaces = "decimalplaces";
	public static final String confDateFormat = "dateformat";
	public static final String confExcludedPlayers = "excludedplayers";
	public static final String confHeader = "header";
	public static final String confFooter = "footer";
	public static final String confDetail = "detail";
	public static final String confCmdAliases = "cmdaliases";
	public static final String confHoloTopN = "holotopn";
	public static final String confHoloPageSize = "holopagesize";
	public static final String confHoloHeader = "holoheader";
	public static final String confHoloDetail = "holodetail";
	public static final String confHoloFooter = "holofooter";
	public static final String confHoloUpdateInterval = "holoupdateinterval";
	public static final String confPlaceHolderEmpty = "placeholderempty";
	public static final String confExcludeDays = "excludedays";
	public static final String confUpdateOnlineBalInterval = "updateonlinebalinterval";
	public static final String confUpdateOfflineBalInterval = "updateofflinebalinterval";
	public static final String confSortBalInterval = "sortbalinterval";
	public static final String confHologramBoards = "HologramBoards/";
	
	public static final  String[] hologramBoardFiles = {"default.yml", "holoboard1.yml", "holoboard2.yml"};
            
	public static final String hologramConfFile = "holograms.yml";
	public static final int initBalInterval = 20 * 20;
		
	//Database (config.yml) keys
	public final static String confDBType = "DBType";
	public final static String confFile = "FILE";
	public final static String confFileName = "Filename";
	public final static String confSQLite = "SQLITE";
	public final static String confDatabase = "Database";
	public final static String confMYSQL= "MYSQL";
	public final static String confUsername = "Username";
	public final static String confPassword = "Password";
	public final static String confHost= "Host";
	public final static String confPort= "Port";
	

	public static HashMap<String,FileConfiguration> configFiles;
	
	private String hologramBoard = hologramBoardFiles[0];
	//private static boolean createdConfigFiles;

	
	public ConfigUtils()
	{
		new ConfigUtils(false);
	}
	
	public ConfigUtils(boolean createConfigFiles)
	{
		if ( ! createConfigFiles )
		{
			configFiles = new HashMap<String,FileConfiguration>();
			getConfigFile("config");
		
			createHologramBoardFiles();
		}
	}

	
	public void setHologramBoard(String tmpHologramBoard)
	{
		hologramBoard = tmpHologramBoard;
	}
	
	
	public void createHologramBoardFiles()
	{
		File tmpBoardDir = new File(plugin.getDataFolder() + "/" + ConfigUtils.confHologramBoards); 
		FileConfiguration configFile;
		
		if ( ! tmpBoardDir.exists() )
			tmpBoardDir.mkdir();
		
		else
		{
			File[] boardFiles = tmpBoardDir.listFiles(new FilenameFilter() 
			{
				public boolean accept(File dir, String name) 
				{
					return name.toLowerCase().endsWith(".yml");
				}
			});
			
			for ( File boardFile : boardFiles)
				configFile = getConfigFile("holoboard", boardFile.getName());		
		}	
		
		for ( String  pluginBoardFile : hologramBoardFiles)
		{
			if ( ! configFiles.containsKey(ConfigUtils.confHologramBoards + pluginBoardFile) )
				configFile = getConfigFile("holoboard", pluginBoardFile);
		}
		
	}
	
	
	public ItemStack getPlayerHead(String playerName, String headTitle)
	{
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta itemm = (SkullMeta) item.getItemMeta();
		if ( (playerName == null) || (playerName == "") )
			itemm.setOwner("MHF_Question");
		else
			itemm.setOwner(playerName);
		
		//StringUtils strUtils = new StringUtils();
		
		//itemm.setDisplayName((String) strUtils.formatLine(headTitle).getFmtStr());
		itemm.setDisplayName(headTitle);
		item.setItemMeta(itemm);
		return item;
	}
	
	
	public FileConfiguration getConfigFile(String configType)
	{	
		return getConfigFile(configType,null);
	}
	
	
	public FileConfiguration getConfigFile(String configType, String configBoardFile)
	{	
		FileConfiguration configFile = null;
		boolean createConfFile = false;
		
		switch(configType)
		{
			case "config":
				if ( configFiles.containsKey("config") )
					configFile = configFiles.get("config");
				else
				{
					configFile = plugin.getConfig();
					configFiles.put("config", configFile);
				}
				break;
			
			case "holoboard":
				if ( configFiles.containsKey(confHologramBoards + configBoardFile))
					configFile = configFiles.get(confHologramBoards + configBoardFile);
				else
				{
					File confFile = new File(plugin.getDataFolder() + "/" + confHologramBoards ,configBoardFile);
					
					if ( ! confFile.exists() )
					{
						plugin.saveResource(confHologramBoards + configBoardFile, false);
						console.sendMessage(Messages.getMessage("crdfltfile", configType + ".yml!"));
					}
	    		
					configFile = YamlConfiguration.loadConfiguration(confFile);
					configFiles.put(confHologramBoards + configBoardFile, configFile);				
				}
				break;
				
			default:
				if ( configFiles.containsKey(configType ) )
					configFile = configFiles.get(configType);
				else
				{
					if (! plugin.getDataFolder().exists()) 
						plugin.getDataFolder().mkdirs();
					
					File confFile = new File(plugin.getDataFolder(),configType + ".yml");
					
					if ( ! confFile.exists() )
					{
						createConfFile = true;
						plugin.saveResource(configType + ".yml", false);
						console.sendMessage(Messages.getMessage("crdfltfile", configType + ".yml!"));
					}
	    		
					configFile = YamlConfiguration.loadConfiguration(confFile);
					configFiles.put(configType, configFile);				
				}
	    		
	    		break;
	    	}
		
		if ( createConfFile)
			return configFile;
			
		switch (configType)
		{
			case "config":
				boolean saveConfigFile = false;
				
				InputStream defaultStream = plugin.getResource(configType + ".yml");
	    		Reader defaultReader = new InputStreamReader(defaultStream);
	    		YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultReader);
				
				for ( String key : defaultConfig.getKeys(true))
				{
					if (! configFile.getKeys(true).contains(key) )
					{
						configFile.addDefault(key, defaultConfig.get(key));
						configFile.set(key, defaultConfig.get(key));
						saveConfigFile = true;
        		
						console.sendMessage(Messages.getMessage("addmissingkey", key, configType + ".yml"));
					}
				}
				
				if ( saveConfigFile )
	    		{
	    			try 
	    			{
	    				File tmpConfigFile = new File(plugin.getDataFolder(), configType + ".yml");
	    				configFile.save(tmpConfigFile);
	    			}
	    			catch (Exception e2) 
	    			{
	    				e2.printStackTrace();
	    			}
	    		}
		}
		return configFile;
	}
	
	
	public FileConfiguration getConfigType(String confKey)
	{
		String confType;
		
		switch (confKey.split("\\.")[0])
		{
			case confHoloHeader:
			case confHoloFooter:
			case confHoloDetail:	
				return getConfigFile("holoboard", hologramBoard);

			default:
				confType = "config";
				return getConfigFile(confType);
		}		
	}
	
	
	public String[] getSectionKeys(String confSection)
	{			
		Set<String> confSections = getConfigType(confSection).getConfigurationSection(confSection).getKeys(false);
		return confSections.toArray(new String[0]);
	}
	
	
	public Boolean getConfBoolean(String confKey )
	{
		String confValStr = getConfigType(confKey).getString(confKey).toLowerCase();
		return confValStr.equals("yes") || confValStr.equals("true");	
	}
	
	
	public String[] getConfStringArr(String confKey)
	{
		return getConfigType(confKey).getStringList(confKey).toArray(new String[0]);
	}

	
	public List<String> getConfStringArrList(String confKey)
	{
		return getConfigType(confKey).getStringList(confKey);
	}

	
	public int getConfInt(String confKey)
	{
		return getConfigType(confKey).getInt(confKey);
	}
	
	
	public String getConfStr(String confKey)
	{
		String tmpConfStr = getConfigType(confKey).getString(confKey); 
		return tmpConfStr == null ? "" : tmpConfStr;
	}
	
	
	public void setConf(String confKey, Object confValue)
	{
		FileConfiguration tmpConfFile = getConfigType(confKey);
		
		tmpConfFile.set(confKey, confValue);
		try
		{
			tmpConfFile.save(tmpConfFile.getName());
		}
		catch (Exception ex)
		{
			
		}
	}
	 	
	public ArrayList<Material> getConfBlockList(String confKey)
	{
		ArrayList<Material> tmpBlockList = new ArrayList<Material>();
		String[] tmpBlockListStr = getConfStringArr(confKey);
		
		for ( String tmpBlockStr : tmpBlockListStr)
		{
			tmpBlockList.add(Material.getMaterial(tmpBlockStr));
		}
		
		if ( ! tmpBlockList.contains(Material.AIR))
			tmpBlockList.add(Material.AIR);
		
		return tmpBlockList;
	}

}
