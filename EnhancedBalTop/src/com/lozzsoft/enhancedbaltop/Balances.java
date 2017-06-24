package com.lozzsoft.enhancedbaltop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
//import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.lozzsoft.enhancedbaltop.Messages;

import net.milkbowl.vault.permission.Permission;

import static com.lozzsoft.enhancedbaltop.EnhancedBalTop.econ;
import static com.lozzsoft.enhancedbaltop.EnhancedBalTop.plugin;
//import static com.lozzsoft.enhancedbaltop.EnhancedBalTop.checkExcludeList;


public class Balances 
{	
	private static HashMap<String,OfflinePlayer> playerOffline = new HashMap<String,OfflinePlayer>();
	private static HashMap<String,Player> playerOnline = new HashMap<String,Player>();;
	
	private static HashMap<String,Double> playerBal = new HashMap<String,Double>();
	//private static HashMap<String,Double> holoPlayerBal;
	private static HashMap<String,UUID> playerUUID = new HashMap<String,UUID>();

	public static boolean hookPermissions = false;
	public static Boolean balancesInitialised = false;
	public static Boolean balancesChanged = false;
	public static Permission perms = null;
	public static String permsProvider;

	
	public static <K extends Comparable<? super K>, V> Map<K, V> SortByKey(Map<K, V> map) {
		 
		Map<K, V> result = new LinkedHashMap<>();
		Stream<Map.Entry<K, V>> sequentialStream = map.entrySet().parallelStream();
 
		// comparingByKey() returns a comparator that compares Map.Entry in natural order on key.
		sequentialStream.sorted(Map.Entry.comparingByKey()).forEachOrdered(c -> result.put(c.getKey(), c.getValue()));
		
		return  result;
	}
	
	
	public static <K, V extends Comparable<? super V>> Map<K, V> crunchifySortByValue(Map<K, V> crunchifyMap) {
		 
		Map<K, V> crunchifyResult = new LinkedHashMap<>();
		Stream<Map.Entry<K, V>> sequentialStream = crunchifyMap.entrySet().parallelStream();
 
		// comparingByValue() returns a comparator that compares Map.Entry in natural order on value.
		sequentialStream.sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEachOrdered(c -> crunchifyResult.put(c.getKey(), c.getValue()));
		return crunchifyResult;
	}
	
	
	public static HashMap<String, Double> sortByValue(HashMap<String, Double> map )
	{
		HashMap<String,Double> result = new LinkedHashMap<>();	    
		HashMap<String,Double> result2 = new LinkedHashMap<>();
		
		map.entrySet().parallelStream().sorted(Map.Entry.<String,Double>comparingByKey()).forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
		result.entrySet().parallelStream().sorted(Collections.reverseOrder(Map.Entry.<String,Double>comparingByValue())).forEachOrdered(e ->result2.put(e.getKey(),e.getValue()));
	    return result2;
	}
	
	
	public static void updateBalance(String playerName)
	{
		Double tmpBal = playerBal.get(playerName);
		Double tmpBal2 = tmpBal;
		UUID tmpUUID = null;
		
		if (hasOnlinePlayer(playerName))
		{
			Player tmpPlayer = getOnlinePlayer(playerName);
			tmpUUID = tmpPlayer.getUniqueId();
			
			try
	    	{
				tmpBal2 = econ.getBalance(tmpPlayer);
				//Log.info("DEBUG: updateBalance, updating Balance For " + playerName + ", UUID=" + tmpPlayer.getUniqueId());
				if ( tmpBal2 == null )
	        		tmpBal2 = 0D;
	    	}
	    	catch (Exception e)
	    	{
	    		ConsoleCommandSender tmpConsole = plugin.getServer().getConsoleSender(); 
	    		String errMsg = Messages.getMessage("econgetbalnv", playerName, econ.getName());
	    		tmpConsole.sendMessage(errMsg);
	    		e.printStackTrace();
	    	}
		}
		else if (hasOfflinePlayer(playerName))
		{			
			OfflinePlayer tmpPlayer = getOfflinePlayer(playerName);
			tmpUUID = tmpPlayer.getUniqueId();
			
			try
    		{
				tmpBal2 = econ.getBalance(tmpPlayer);
				//Log.info("DEBUG: updateBalance, updating Balance For " + playerName + ", UUID=" + tmpPlayer.getUniqueId());
				if ( tmpBal2 == null )
        			tmpBal2 = 0D;
    		}
    		catch (Exception e)
    		{
    			ConsoleCommandSender tmpConsole = plugin.getServer().getConsoleSender(); 
    			String errMsg = Messages.getMessage("econgetbalnv", playerName, econ.getName());
    			tmpConsole.sendMessage(errMsg);
    			e.printStackTrace();
    		}
		}
		
		if ( tmpUUID != null)
			playerUUID.put(playerName, tmpUUID);
		
		if ( (tmpBal == null) || (! tmpBal.equals(tmpBal2)) )
		{	
			playerBal.put(playerName, tmpBal2);
			balancesChanged = true;
		}
	}
		
	
    @SuppressWarnings("unused")
	public static Double getBalance(OfflinePlayer playerParam) 
    {
    	Double tmpBal = 0D;
    	String tmpPlayerName = playerParam.getName();
    	
    	//if ( hasOfflinePlayer(playerParam) )
    	//{   
    		if ( hasOnlinePlayer(tmpPlayerName) )
    		{
    			tmpBal = playerBal.get(tmpPlayerName);
    			return tmpBal;
    		}
    	    
    		try
    		{
    			//Log.info("DEBUG: getting Balance for " + playerParam + ", UUID=" + playerParam.getUniqueId());
    			tmpBal = econ.getBalance(playerParam);
    			
    			if ( tmpBal == null )
        			tmpBal = 0D;
    		}
    		catch (Exception e)
    		{
    			ConsoleCommandSender tmpConsole = plugin.getServer().getConsoleSender(); 
    			String errMsg = Messages.getMessage("econgetbalnv", tmpPlayerName, econ.getName());
    			tmpConsole.sendMessage(errMsg);
    			e.printStackTrace();
    		}
    		
    		
    		if (! tmpBal.equals(playerBal.get(tmpPlayerName)) )
    		{
    			balancesChanged = true;
    			playerBal.put(tmpPlayerName, tmpBal);
    		}
    		
    		return tmpBal;
    	//}
    	//else
    	//{	
    	//	tmpBal = addOfflinePlayer(tmpPlayerName,playerParam);
    	//	return tmpBal;	
    	//}	
    }
    
   
    public static Double getBalance(OfflinePlayer playerParam, Boolean checkUpdated)
	{
		if ( checkUpdated )
			return getBalance(playerParam);
		
		String tmpPlayerName = playerParam.getName();
		return playerBal.get(tmpPlayerName);
	}
	
    
    public static Double getBalance(String tmpPlayerName, Boolean checkUpdated)
   	{
   		if ( checkUpdated )
   			return getBalance(tmpPlayerName);
   		
   		return playerBal.get(tmpPlayerName);
   	}
   	
       
    public static Double getBalance(String tmpPlayerName) 
    {
    	Double tmpBal = (double) 0;
       	OfflinePlayer tmpOfflinePlayer = getOfflinePlayer(tmpPlayerName);
       	
       	tmpBal = getBalance(tmpOfflinePlayer);
       	
       	return tmpBal;
    }
       
    
    public static Double getBalance(int balancePos)
    {
    	ArrayList<Double> tmpPlayerBals = new ArrayList<Double>(playerBal.values());
    	
    	if ( balancePos<0 )
    		balancePos=0;
    	else if ( balancePos >= tmpPlayerBals.size())
    	  balancePos = tmpPlayerBals.size()-1;
		
    	return tmpPlayerBals.get(balancePos);
    }
    
    
    public static void initOfflinePlayers()
    {	
    	OfflinePlayer[] tmpPlayers = plugin.getServer().getOfflinePlayers();
    
    	for (int i=0; i<tmpPlayers.length;i++)
    	{
    		String tmpPlayerName = tmpPlayers[i].getName();
    		OfflinePlayer tmpPlayer = tmpPlayers[i];
    		UUID tmpUUID = tmpPlayer.getUniqueId();
    		
    		//Log.info("DEBUG: initOfflinePlayers | tmpPlayerName= " + tmpPlayerName + ", UUID=" + tmpUUID);
    		
    		if ( playerUUID.containsKey(tmpPlayerName) )
    		{
    			/*
    			if ( tmpPlayer.hasPlayedBefore() )
    			{
    				long daysDiff = EnhancedBalTop.getDateDiff(new Date(tmpPlayer.getLastPlayed()), new Date(), TimeUnit.DAYS);
    				int excludeDays = plugin.getConfig().getInt(EnhancedBalTop.confExcludeDays);
        		
    				Log.info("DEBUG: initOfflinePlayers, tmpPlayerName = " + tmpPlayerName, ", excludeDays=" + excludeDays + ", daysDiff=" + daysDiff);
    				if ( ( excludeDays != 0 ) && ( daysDiff > excludeDays) )
    					continue;
    			}
    			*/
    			
    			//Log.info("DEBUG: initoffLinePlayers playerUUID.get(tmpPlayerName)=" + playerUUID.get(tmpPlayerName));
    			
    			if ( (! playerUUID.get(tmpPlayerName).equals(tmpUUID)) )
				{
    				if ( ! hasOfflinePlayer(tmpPlayerName))
    				{
    					addOfflinePlayer(tmpPlayerName,tmpPlayer);
    					if ( hasOfflinePlayer(tmpPlayer) )
    						playerUUID.replace(tmpPlayerName,tmpUUID); 						
    				}
    				else
    				{
    					long tmpLastPlayed = tmpPlayer.getLastPlayed();
    					long tmpLastPlayed2 = playerOffline.get(tmpPlayerName).getLastPlayed();
    					
    					if ( tmpLastPlayed > tmpLastPlayed2 )
    					{
       						playerOffline.replace(tmpPlayerName, tmpPlayer);
    						//playerUUID.replace(tmpPlayerName,tmpUUID);
    						updateBalance(tmpPlayerName);
    					}
    				}
				}
    				
    		}
    		else
    		{
    			playerUUID.put(tmpPlayerName, tmpUUID);
    			addOfflinePlayer(tmpPlayerName,tmpPlayer);
    		}	
    	}
 	
    }
    
    
    public static Boolean getBalances()
    {	
    	return getBalances(true);
    }
    
    
    public static Boolean getBalances(Boolean init)
    {	
    	if ( ! econ.isEnabled() )
    		return false;
    	
    	if ( init )
    		initOfflinePlayers();
    	
        if ( balancesChanged )
        	sortBalances();
        
        balancesInitialised = true;
        
        ConsoleCommandSender console = plugin.getServer().getConsoleSender();
        console.sendMessage(Messages.getMessage("getbalinfo", Balances.playerBal.size()));
        return true;
    }
    
    
    public static void sortBalances()
    {
    	playerBal = sortByValue(playerBal);
    	//holoPlayerBal = sortByValue(Balances.holoPlayerBal);
    	balancesChanged = false;
    }
    
    
    public static Boolean hasOfflinePlayer(String playerName)
    {
    	return hasOfflinePlayer(playerName, false);
    }
    
    
    public static Boolean hasOfflinePlayer(String playerName, boolean ignoreCase)
    {
    	if (ignoreCase)
    	{
    		for ( String offlinePlayerName : playerOffline.keySet())
    		{
    			if (offlinePlayerName.equalsIgnoreCase(playerName))
    				return true;
    		}
    		return false;
    	}
    	else
    		return playerOffline.containsKey(playerName);
    }
    
    
    public static Boolean hasOfflinePlayer(OfflinePlayer player)
    {
    	return playerOffline.containsValue(player);
    }
    
    
    public static Boolean hasOnlinePlayer(String playerName)
    {
    	playerName = playerName.toLowerCase();
    	return playerOnline.containsKey(playerName);
    }
    
    
    public static Boolean hasOnlinePlayer(OfflinePlayer player)
    {
    	return playerOnline.containsValue(player);
    }
    
    
    public static HashMap<String,Double> getPlayerBals()
    {
    	return playerBal;
    }
    
    
    public static String[] getPlayerBalNames()
    {
    	return playerBal.keySet().toArray(new String[playerBal.size()]);
    }
    
    
    public static OfflinePlayer getOfflinePlayer(UUID tmpUUID)
    {
    	if ( ! playerUUID.containsValue(tmpUUID) )
    		return plugin.getServer().getOfflinePlayer(tmpUUID);
    		
    	for (String tmpKey : playerUUID.keySet())
    	{
    		if ( playerUUID.get(tmpKey).equals(tmpUUID))
    			return getOfflinePlayer(tmpKey);
    	}
    	
    	return plugin.getServer().getOfflinePlayer(tmpUUID);
    }
    
    
    public static OfflinePlayer getOfflinePlayer(String playerName)
    {
    	if ( playerOffline.containsKey(playerName) )
    		return playerOffline.get(playerName);
    	else if ( playerUUID.containsKey(playerName))
    		return plugin.getServer().getOfflinePlayer(playerUUID.get(playerName));
    	else
    		return plugin.getServer().getOfflinePlayer(playerName);
    }
    
    
    public static ArrayList<OfflinePlayer> getOfflinePlayers()
    {
    	ArrayList<OfflinePlayer> tmpOfflinePlayers = new ArrayList<OfflinePlayer>(playerOffline.values());
    	return tmpOfflinePlayers;
    }
    
  
    public static ArrayList<String> getOfflinePlayerList()
    {
    	ArrayList<String> tmpPlayerList = new ArrayList<String>(playerOffline.keySet());
    	return tmpPlayerList;
    }
  
    
    public static Player getOnlinePlayer(String playerName)
    {
    	return playerOnline.get(playerName);
    }
    
   
    public static Player getOnlinePlayer(UUID playerUUID)
    {
    	return plugin.getServer().getPlayer(playerUUID);	
    }
    
    
    public static ArrayList<String> getOnlinePlayerList()
    {
    	ArrayList<String> tmpPlayerList = new ArrayList<String>(playerOnline.keySet());
    	return tmpPlayerList;
    }
    
    
    public static ArrayList<Player> getOnlinePlayers()
    {
    	ArrayList<Player> tmpOnlinePlayers = new ArrayList<Player>(playerOnline.values());
    	return tmpOnlinePlayers;
    }

    
    public static boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) 
        {
            perms = permissionProvider.getProvider();
            permsProvider = perms.getName();
        }
        
        return (perms != null);
    }
    
    
    public static boolean checkPerm(OfflinePlayer tmpPlayer, String tmpPerm)
    {
    	if ( tmpPerm == "" || tmpPerm == null)
    		return false;
    	
    	if ( hookPermissions )
    	{   
    		switch(permsProvider)
    		{	
    			//case "PermissionsEx" : 
    			//	return PermissionsEx.getUser(tmpPlayer.getName()).has(tmpPerm);
    			
    			//case "GroupManager":
    			//	GroupManager groupManager = (GroupManager) getServer().getPluginManager().getPlugin("GroupManager"); 
        		//	AnjoPermissionsHandler GMhandler = groupManager.getWorldsHolder().getWorldPermissions(tmpPlayer.getName());
        			
        		//	return GMhandler.permission(tmpPlayer.getName(), tmpPerm);
    				 				
    			default:
    				boolean playerOnlinePerms = false;
    				boolean playerOfflinePerms = false;
    				
    				if ( tmpPlayer.isOnline() )
    				{
    					String tmpWorld = Balances.getOnlinePlayer(tmpPlayer.getUniqueId()).getWorld().toString();
    					playerOnlinePerms = perms.playerHas(tmpWorld, tmpPlayer, tmpPerm);
    				}
    				
    				playerOfflinePerms = perms.playerHas(null, tmpPlayer, tmpPerm);
    					
    				return (playerOfflinePerms || playerOnlinePerms);
    		}	
    	}
    	return false;	
    }
    
    
    public static Boolean checkExcludeList(String playerName, OfflinePlayer offlinePlayer )
    {
    	String playerUUIDStr = getUUIDFromString(playerName).toString();
    	
    	for ( String tmpPlayerName : EnhancedBalTop.excludePlayerList)
    	{
    		if ( tmpPlayerName.equals(playerName) || tmpPlayerName.equals(playerUUIDStr) )
    			return true;
    	}
    	
    	return false;
    }
    
    
    public static void addOfflinePlayer(OfflinePlayer tmpPlayer)
    {
    	addOfflinePlayer(tmpPlayer.getName(), tmpPlayer);
    }
    

    public static void addOfflinePlayer(String tmpPlayerName, OfflinePlayer tmpPlayer)
    {
    	Double tmpBal = 0d;
    	
    	if ( tmpPlayer.hasPlayedBefore() )
    	{	
    		long daysDiff = 0;
    		
    		if ( EnhancedBalTop.excludeDays != 0)
    		{
    			daysDiff = EnhancedBalTop.getDateDiff(new Date(tmpPlayer.getLastPlayed()), new Date(), TimeUnit.DAYS);
    			if (daysDiff > EnhancedBalTop.excludeDays )
    				return;
    		}
    	}
    			
    	if ( (! checkExcludeList(tmpPlayerName, tmpPlayer)) && (! checkPerm(tmpPlayer,EnhancedBalTop.excludePermNode)) )
    	{
    		playerOffline.put(tmpPlayerName, tmpPlayer);
    		tmpBal = getBalance(tmpPlayer);
    		//sortBalances();
    	}	
    } 
  
    
    public static void addOnlinePlayer(UUID tmpPlayerUUID)
    {
    	Player tmpPlayer = plugin.getServer().getPlayer(tmpPlayerUUID);
    	addOnlinePlayer(tmpPlayer);
    }
    
    
    public static void addOnlinePlayer(Player tmpPlayer)
    {
    	addOnlinePlayer(tmpPlayer.getName(),tmpPlayer);
    }
    

    public static void addOnlinePlayer(String tmpPlayerName, Player tmpPlayer)
    {
    	playerOnline.put(tmpPlayerName, tmpPlayer);
    	
    	if (! hasOfflinePlayer(tmpPlayerName,true))
    		addOfflinePlayer(tmpPlayerName,getOfflinePlayer(tmpPlayer.getUniqueId()));
    }
    
    
    public static void removeOfflinePlayer(OfflinePlayer tmpPlayer)
    {
    	removeOfflinePlayer(tmpPlayer, true);
    }
    
    
	public static void removeOfflinePlayer(OfflinePlayer tmpPlayer, boolean sortBal)
    {
    	String tmpPlayerName = tmpPlayer.getName();
    	playerOffline.remove(tmpPlayerName);
    	
    	if ( playerBal.containsKey(tmpPlayerName))
    	{
    		playerBal.remove(tmpPlayerName);
    		sortBalances();
    	}
    }
    
    
    public static void removeOfflinePlayer(String tmpPlayerName)
    {
    	playerOffline.remove(tmpPlayerName);
    	
    	if ( playerBal.containsKey(tmpPlayerName))
    	{
    		playerBal.remove(tmpPlayerName);
    		sortBalances();
    	}
    }
    
    
    public static void removeOnlinePlayer(Player tmpPlayer)
    {
    	String tmpPlayerName = tmpPlayer.getName();
    	playerOnline.remove(tmpPlayerName);	
    }
    
    
    public static void removeOnlinePlayer(String tmpPlayerName)
    {
    	playerOnline.remove(tmpPlayerName);
    }
    
    
    public static int offlinePlayerSize()
    {
    	return playerOffline.size();
    }
    
    
    public static int onlinePlayerSize()
    {
    	return playerOnline.size();
    }
    
    
    public static UUID convertToUUID(String tmpUUID)
    {
    	if ( isUUID(tmpUUID) )
    		return UUID.fromString(tmpUUID);
    	
    	return null;
    }
    
    
    public static UUID getUUIDFromString(String tmpPlayer)
    {
    	for ( String offlinePlayer : playerUUID.keySet())
    		if (offlinePlayer.equalsIgnoreCase(tmpPlayer))
    			return playerUUID.get(offlinePlayer);
    	
    	return getOfflinePlayer(tmpPlayer).getUniqueId();
    }
    
    
    public static UUID getUUIDFromPlayer(OfflinePlayer tmpPlayer)
    {
    	return getUUIDFromString(tmpPlayer.getName());
    	/*
    	String tmpPlayerName = "";
    	
    	if ( hasOfflinePlayer(tmpPlayer) )
    	{
    		for ( String tmpKey : getOfflinePlayerList() )
    		{
    			if ( playerOffline.get(tmpKey).equals(tmpPlayer) )
    			{
    				tmpPlayerName = tmpKey;
    				break;
    			}
    		}
    	}
    	else
    		tmpPlayerName = tmpPlayer.getName();
    	
    	if ( playerUUID.containsKey(tmpPlayer) )
    		return playerUUID.get(tmpPlayerName);
    	else
    		return tmpPlayer.getUniqueId();
    	*/
    }
    
    
    public static void addUUID(String tmpPlayerName,UUID tmpUUID)
    {
    	OfflinePlayer tmpPlayer = getOfflinePlayer(tmpUUID);
    	
    	if ( playerUUID.containsKey(tmpPlayerName) )
    	{
    		OfflinePlayer tmpPlayer2 = getOfflinePlayer(tmpPlayerName);
    		if ( tmpPlayer.getLastPlayed() > tmpPlayer2.getLastPlayed() )
    			playerUUID.replace(tmpPlayerName, tmpUUID);	
    	}
    	else
    		playerUUID.put(tmpPlayerName,tmpUUID);
    }
    
    
    public static void removeUUID(String tmpPlayerName)
    {
    	playerUUID.remove(tmpPlayerName);
    }
    	
    
    public static boolean isUUID(String string) 
    {
        try 
        {
            UUID.fromString(string);
            return true;
        } 
        catch (Exception ex) 
        {
            return false;
        }
    }
}
