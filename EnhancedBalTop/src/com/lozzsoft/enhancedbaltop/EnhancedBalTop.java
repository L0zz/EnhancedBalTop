/*
 * Copyright (C) 2016 Lawrence Ackroyd (LozzSoft)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.lozzsoft.enhancedbaltop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.ItemStack;

import com.lozzsoft.enhancedbaltop.Balances;
import com.lozzsoft.enhancedbaltop.Messages;
import com.lozzsoft.enhancedbaltop.Holograms.HoloHologramLine;
import com.lozzsoft.enhancedbaltop.Holograms.HoloItemLine;
import com.lozzsoft.enhancedbaltop.Holograms.HoloTextLine;
import com.lozzsoft.enhancedbaltop.HolographicDisplays.HDHologramLine;
import com.lozzsoft.enhancedbaltop.HolographicDisplays.HDItemLine;
import com.lozzsoft.enhancedbaltop.HolographicDisplays.HDTextLine;

import net.milkbowl.vault.economy.Economy;

import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.HologramPlugin;


public class EnhancedBalTop extends JavaPlugin implements Listener 
{
	// private static final Logger log = Logger.getLogger("Minecraft");

	private int topnbal = ConfigUtils.defTopNBal;
	private int holoTopN = topnbal;
	
	private int pageNo = 1;
	private int totalPages = 1;
	
	/*
	private int holoPageNo = 1;
	private int holoTotalPages = 1;*/

	public static int excludeDays = 0;

	public static boolean hookEconomy = false;
	public static boolean hookPlaceHolderAPI = false;
	public static boolean hologramsEnabled = false;
	public static boolean enabledUUID = false;
	public static boolean balancesRunning = false;

	public static List<String> excludePlayerList;
	public static String cmdprefix;
	public static String excludePermNode;

	public static ConsoleCommandSender console;
	public static EnhancedBalTop plugin;
	public static Economy econ = null;
	public static HologramManager hologramManager;

	public static boolean hookHolographicDisplays = false;
	public static boolean hologramsSetup = false;
	public static boolean updatingHologramObj = false;
	
	public String cmdAlias = "baltop";

	private HashMap<String, HologramBT> balTopHolograms = new HashMap<String, HologramBT>();
	//private static Queue<HologramBT> hologramUpdateQueue = new LinkedList<HologramBT>();
	private static ArrayList<HologramBT> hologramUpdateQueue = new ArrayList<HologramBT>();
	
	/*private HashMap<String, Integer> hologramPageNo = new HashMap<String, Integer>();
	private HashMap<String, Integer> holoUpdateTaskId = new HashMap<String, Integer>();
	private HashMap<String, Integer> holoUpdateInterval = new HashMap<String, Integer>();*/
	private ConfigUtils confUtils;
	private StringUtils strUtils;

	
	public EnhancedBalTop() 
	{
	
	}

	
	private String hookPlugin(String plugin)
	{
		if ( getServer().getPluginManager().getPlugin(plugin) == null ) 
			return null;
	    	
		return getServer().getPluginManager().getPlugin(plugin).getDescription().getVersion();
	}
	
	
	private boolean setupEconomy() 
	{
		if ( getServer().getPluginManager().getPlugin("Vault") == null )
			return false;

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

		if ( rsp == null )
			return false;

		econ = rsp.getProvider();

		return econ != null;
	}

	
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) 
	{
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	
	
	/*
	private String formatLine(String tmpLine, int tmpPlayerCnt, String tmpPlayerName, double tmpPlayerBal,boolean isHologram) 
	{
		// colour code & fix
		tmpLine = tmpLine.replaceAll("&", "§");
		tmpLine = tmpLine.replaceAll("§§", "&");

		// now replace {variable}'s
		Pattern p = Pattern.compile("(\\{\\w+\\})");
		Matcher m = p.matcher(tmpLine);

		int decimalPlaces;
		try 
		{
			decimalPlaces = Integer.parseInt(confUtils.getConfStr(ConfigUtils.confDecimalPlaces));
		} 
		catch (NumberFormatException e) 
		{
			decimalPlaces = ConfigUtils.defDecimalPlaces;
		}

		while (m.find()) 
		{
			String varStr = m.group().replace("{", "").replace("}", "");
			// log.info("DEBUG: varStr=" + varStr);

			switch (varStr.toLowerCase()) 
			{
			case "servername":
				tmpLine = tmpLine.replaceAll("\\{servername\\}", getServer().getServerName());
				break;

			case "topnbal":
				if (isHologram) 
				{
					tmpLine = tmpLine.replaceAll("\\{topnbal\\}", Integer.toString(holoTopN));
				} 
				else 
				{
					tmpLine = tmpLine.replaceAll("\\{topnbal\\}", Integer.toString(topnbal));
				}
				break;

			case "date":
				String date = new SimpleDateFormat(confUtils.getConfStr(ConfigUtils.confDateFormat)).format(new Date());
				tmpLine = tmpLine.replaceAll("\\{date\\}", date);
				break;

			case "player":
				tmpLine = tmpLine.replaceAll("\\{player\\}", tmpPlayerName);
				break;

			case "playerpadr":
				tmpLine = tmpLine.replaceAll("\\{playerpadr\\}", padRight(tmpPlayerName, 16));
				break;

			case "playerbal":
				tmpLine = tmpLine.replaceAll("\\{playerbal\\}", String.format("%." + decimalPlaces + "f", tmpPlayerBal));
				break;

			case "playerbalfmt":
			case "balance":
				tmpLine = tmpLine.replaceAll("\\{" + varStr.toLowerCase() + "\\}", formatMoney(tmpPlayerBal));
				break;

			case "excludedays":
				tmpLine = tmpLine.replaceAll("\\{" + varStr.toLowerCase() + "\\}", Integer.toString(EnhancedBalTop.excludeDays));
				break;

			case "lineno":
				tmpLine = tmpLine.replaceAll("\\{lineno\\}", padLeft(Integer.toString(tmpPlayerCnt), Integer.toString(Balances.offlinePlayerSize()).length()));
				break;

			case "pageno":
				if (isHologram) 
					tmpLine = tmpLine.replaceAll("\\{pageno\\}", Integer.toString(holoPageNo));
				else 
					tmpLine = tmpLine.replaceAll("\\{pageno\\}", Integer.toString(pageNo));
				break;

			case "totalpages":
				if (isHologram) 
					tmpLine = tmpLine.replaceAll("\\{totalpages\\}", Integer.toString(holoTotalPages));
				else 
					tmpLine = tmpLine.replaceAll("\\{totalpages\\}", Integer.toString(totalPages));
				break;

			case "totalonlineplayers":
				tmpLine = tmpLine.replaceAll("\\{totalonlineplayers\\}", Integer.toString(Balances.onlinePlayerSize()));
				break;

			case "totalofflineplayers":
				tmpLine = tmpLine.replaceAll("\\{totalofflineplayers\\}", Integer.toString(Balances.offlinePlayerSize()));
				break;

			case "maxplayerbalvalue":
				tmpLine = tmpLine.replaceAll("\\{maxplayerbalvalue\\}",
						String.format("%." + decimalPlaces + "f", Balances.getBalance(0)));
				break;

			case "maxplayerbalvaluefmt":
			case "maxbalvalue":
				tmpLine = tmpLine.replaceAll("\\{" + varStr.toLowerCase() + "\\}", formatMoney(Balances.getBalance(0)));
				break;

			case "minplayerbalvalue":
				int tmpBalPos2 = topnbal - 1;

				if (isHologram)
					tmpBalPos2 = holoTopN - 1;

				tmpLine = tmpLine.replaceAll("\\{minplayerbalvalue\\}",
						String.format("%." + decimalPlaces + "f", Balances.getBalance(tmpBalPos2)));
				break;

			case "minplayerbalvaluefmt":
			case "minbalvalue":
				int tmpBalPos3 = topnbal - 1;

				if (isHologram)
					tmpBalPos3 = holoTopN - 1;

				tmpLine = tmpLine.replaceAll("\\{" + varStr.toLowerCase() + "\\}",
						formatMoney(Balances.getBalance(tmpBalPos3)));
				break;

			case "maxbalplayer":
				String[] tmpBalPlayerNames = Balances.getPlayerBalNames();
				tmpLine = tmpLine.replaceAll("\\{maxbalplayer\\}", tmpBalPlayerNames[0]);
				break;

			case "minbalplayer":
				int tmpBalPos4 = topnbal - 1;

				if (isHologram)
					tmpBalPos4 = holoTopN - 1;

				String[] tmpBalPlayerNames2 = Balances.getPlayerBalNames();

				if (tmpBalPos4 > tmpBalPlayerNames2.length)
					tmpBalPos4 = tmpBalPlayerNames2.length - 1;

				tmpLine = tmpLine.replaceAll("\\{minbalplayer\\}", tmpBalPlayerNames2[tmpBalPos4]);
				break;

			case "uuid":
				tmpLine = tmpLine.replaceAll("\\{uuid\\}", Balances.getUUIDFromString(tmpPlayerName).toString());
				break;

			case "serverbalonlinetotal":
				double totalBal = 0.0;
				HashMap<String, Double> tmpBals = Balances.getPlayerBals();

				for (String tmpPlayer : Balances.getOnlinePlayerList())
					totalBal += tmpBals.get(tmpPlayer);

				tmpLine = tmpLine.replaceAll("\\{" + varStr.toLowerCase() + "\\}", String.format("%." + decimalPlaces + "f", totalBal));
				break;

			case "serverbalonlinetotalfmt":
			case "serveronlinetotal":
				double totalBal2 = 0.0;
				HashMap<String, Double> tmpBals2 = Balances.getPlayerBals();

				for (String tmpPlayer : Balances.getOnlinePlayerList())
					totalBal2 += tmpBals2.get(tmpPlayer);

				tmpLine = tmpLine.replaceAll("\\{" + varStr.toLowerCase() + "\\}", formatMoney(totalBal2));
				break;

			case "serverbaltotal":
				double totalBal3 = 0.0;
				HashMap<String, Double> tmpBals3 = Balances.getPlayerBals();

				for (String tmpPlayer : tmpBals3.keySet())
					totalBal3 += tmpBals3.get(tmpPlayer);

				tmpLine = tmpLine.replaceAll("\\{" + varStr.toLowerCase() + "\\}", String.format("%." + decimalPlaces + "f", totalBal3));
				break;

			case "serverbaltotalfmt":
			case "servertotal":
				double totalBal4 = 0.0;
				HashMap<String, Double> tmpBals4 = Balances.getPlayerBals();

				for (String tmpPlayer : tmpBals4.keySet())
					totalBal4 += tmpBals4.get(tmpPlayer);

				tmpLine = tmpLine.replaceAll("\\{" + varStr.toLowerCase() + "\\}", formatMoney(totalBal4));
				break;
			}
		}
		

		if (tmpPlayerName != "") 
		{
			OfflinePlayer tmpPlayer = Balances.getOfflinePlayer(tmpPlayerName);

			if (tmpPlayer.isOnline() && hookPlaceHolderAPI) 
			{
				Player tmpPlayerOnline = Balances.getOnlinePlayer(tmpPlayerName);
				tmpLine = PlaceholderAPI.setPlaceholders(tmpPlayerOnline, tmpLine);
			}
		}

		tmpLine = tmpLine.replaceAll("%.*%", confUtils.getConfStr(ConfigUtils.confPlaceHolderEmpty));

		return tmpLine;
		
	}*/
	
	
	/*
	private Object formatHoloLine(String tmpLine, int tmpPlayerCnt, String tmpPlayerName, double tmpPlayerBal) 
	{
		tmpLine = formatLine(tmpLine, tmpPlayerCnt, tmpPlayerName, tmpPlayerBal, true);

		// now replace {Hologram Specific variable}'s
		Pattern p = Pattern.compile("(\\{icon:\\w+\\})");
		Matcher m = p.matcher(tmpLine);

		while (m.find()) 
		{
			String varStr = m.group().replace("{", "").replace("}", "");

			if (varStr.toLowerCase().startsWith("icon:")) 
			{
				String tmpItemStr = varStr.split("icon:")[1];
				ItemStack tmpItemStack = new ItemStack(Material.getMaterial(tmpItemStr));
				return tmpItemStack;
			}
		}

		return tmpLine;
	}*/

	
	private void saveHologramConfig(FileConfiguration tmpHologramConfigFile) 
	{
		File hologramYml = new File(getDataFolder() + "/" + ConfigUtils.hologramConfFile);

		try 
		{
			tmpHologramConfigFile.save(hologramYml);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	
	private FileConfiguration getHolograms() 
	{
		FileConfiguration tmpHologramConfigFile;
		File hologramYml = new File(getDataFolder() + "/" + ConfigUtils.hologramConfFile);

		if ( ! hologramYml.exists() ) 
		{
			console.sendMessage(Messages.getMessage("hologramfilenf", hologramYml.getPath()));

			InputStream tmpResource = getResource(ConfigUtils.hologramConfFile);
			Reader tmpReader = new InputStreamReader(tmpResource);
			tmpHologramConfigFile = YamlConfiguration.loadConfiguration(tmpReader);

			saveHologramConfig(tmpHologramConfigFile);
		} 
		else 
			tmpHologramConfigFile = YamlConfiguration.loadConfiguration(hologramYml);

		return tmpHologramConfigFile;
	}

	
	private boolean checkInt(String IntStr, CommandSender tmpSender, String errorMsg1, String errorMsg2) 
	{
		try 
		{
			Integer.parseInt(IntStr);
		} 
		catch (NullPointerException npe) 
		{
			tmpSender.sendMessage(errorMsg1);
			return false;
		} 
		catch (NumberFormatException nfe) 
		{
			tmpSender.sendMessage(errorMsg2);
			return false;
		}

		return true;
	}

	
	private boolean checkDouble(String doubleStr, CommandSender tmpSender, String errorMsg1, String errorMsg2) 
	{
		try 
		{
			Double.parseDouble(doubleStr);
		} 
		catch (NullPointerException npe) 
		{
			tmpSender.sendMessage(errorMsg1);
			return false;
		} 
		catch (NumberFormatException nfe) 
		{
			tmpSender.sendMessage(errorMsg2);
			return false;
		}

		return true;
	}

	
	public boolean isHDHologram(Object hologram)
	{
		return (hologram instanceof HolographicDisplays);
	}
	
	
	public boolean isHologramsHologram(Object hologram)
	{
		return (hologram instanceof Holograms);
	}
	
	
	public boolean appendHologramTextLine(Object hologram, String textLine)
	{	
		boolean success = false;
		
		if ( isHDHologram(hologram) )
		{	
			((HolographicDisplays) hologram).appendHologramTextLine(textLine);
			success = true;
		}
		else if ( isHologramsHologram(hologram) )
		{	
			((Holograms) hologram).appendHologramTextLine(textLine);
			success = true;
		}
		
		return success;
	}
	
	
	public boolean appendHologramItemLine(Object hologram, ItemStack itemLine)
	{	
		boolean success = false;
		
		if ( isHDHologram(hologram) )
		{
			((HolographicDisplays) hologram).appendHologramItemLine(itemLine);
			success = true;
		}
		else if ( isHologramsHologram(hologram) )
		{
			((Holograms) hologram).appendHologramItemLine(itemLine);
			success = true;
		}		
		
		return success;
	}
	
	
	public Object createHologram(Location location)
	{
		Object hologram;
		
		if ( hookHolographicDisplays)
			hologram = new HolographicDisplays(plugin, location);
		else
			hologram = new Holograms(String.valueOf(System.currentTimeMillis()), location);	
		
		return hologram;
	}
	

	private HologramBT createHologram(Location holoLocation, String hologramBoard, int holoUpdateInt, int topN)
	{
		confUtils.setHologramBoard(hologramBoard);
		
		List<String> holoHeader = confUtils.getConfStringArrList(ConfigUtils.confHoloHeader);
		List<String> holoFooter = confUtils.getConfStringArrList(ConfigUtils.confHoloFooter);
		List<String> holoDetail = confUtils.getConfStringArrList(ConfigUtils.confHoloDetail);
		
		Object hologram = createHologram(holoLocation);
		String[] playerNameKeys = Balances.getPlayerBalNames();

		int holoPageSize = confUtils.getConfInt(ConfigUtils.confHoloPageSize);

		int holoTotalPages = (int) Math.ceil(playerNameKeys.length / (double) holoPageSize);
		int holoPageNo = 1;

		if (holoTotalPages * holoPageSize > topN)
			holoTotalPages = (int) Math.ceil(holoTopN / (double) holoPageSize);

		HologramBT hologramBT = new HologramBT(hologram, holoUpdateInt, hologramBoard);
		hologramBT.setPageNo(holoPageNo);
		hologramBT.setTopN(topN);
		
		strUtils.setHologramBT(hologramBT);
		strUtils.setHoloTotalPages(holoTotalPages);
		
		// Setup Hologram Header
		for (String holoHeaderLine : holoHeader) 
		{
			Object holoHeaderLineObj = strUtils.formatHoloLine(holoHeaderLine).getFmtStr();

			if (holoHeaderLineObj instanceof String) 
				appendHologramTextLine(hologram, (String) holoHeaderLineObj);
			else if (holoHeaderLineObj instanceof ItemStack) 
				appendHologramItemLine(hologram, (ItemStack) holoHeaderLineObj);
		}

		// Setup Hologram Detail/Player Balance Lines
		for (int tmpPlayerNo = (holoPageNo - 1) * holoPageSize; tmpPlayerNo < (holoPageNo) * holoPageSize; tmpPlayerNo++) 
		{
			if (tmpPlayerNo >= topN || tmpPlayerNo >= playerNameKeys.length)
				break;

			String tmpPlayerName = playerNameKeys[tmpPlayerNo];
			Double tmpPlayerBal = Balances.getBalance(tmpPlayerName);
			
			strUtils.setPlayer(Balances.getOfflinePlayer(tmpPlayerName));
			strUtils.setLineNumber(tmpPlayerNo + 1);
			strUtils.setPlayerBal(tmpPlayerBal);
			
			for (int lineNo = 0; lineNo < holoDetail.size(); lineNo++) 
			{
				Object holoDetailLine = (String) strUtils.formatHoloLine(holoDetail.get(lineNo)).getFmtStr();
				
				if (holoDetailLine instanceof String) 
					appendHologramTextLine(hologram, (String) holoDetailLine);
				else if (holoDetailLine instanceof ItemStack) 
					appendHologramItemLine(hologram, (ItemStack) holoDetailLine);
			}
		}

		// Setup Hologram Footer
		for (String holoFooterLine : holoFooter) 
		{
			Object holoFooterLineObj = strUtils.formatHoloLine(holoFooterLine).getFmtStr();

			if (holoFooterLineObj instanceof String) 
				appendHologramTextLine(hologram, (String) holoFooterLineObj);
			else if (holoFooterLineObj instanceof ItemStack) 
				appendHologramItemLine(hologram, (ItemStack) holoFooterLineObj);
		}

		return hologramBT;
	}
	
	
	private void setupHolograms() 
	{
		if ( ! Balances.balancesInitialised )
			return;

		holoTopN = confUtils.getConfInt(ConfigUtils.confHoloTopN);

		FileConfiguration hologramConfig = getHolograms();
		Set<String> hologramList = hologramConfig.getKeys(false);

		// exit if no holograms defined in holgrams.yml
		if ( hologramList.size() == 0 ) 
			return;

		console.sendMessage(Messages.getMessage("hologramscr", ConfigUtils.hologramConfFile));

		List<World> WorldList = getServer().getWorlds();
		World hologramWorld;
		boolean saveConfig = false;
		
		for (String hologramName : hologramList) 
		{
			hologramWorld = null;
			String[] holoInfo = hologramConfig.getString(hologramName).split(",");

			if (holoInfo.length < 5)
				continue;

			String hologramBoard = ConfigUtils.hologramBoardFiles[0];
			int topN = holoTopN;
			
			if ( holoInfo.length == 6)
			{
				hologramBoard = holoInfo[5];
				saveConfig = true;
			}
			else if (holoInfo.length == 7)
			{
				topN = Integer.parseInt(holoInfo[5]);
				hologramBoard = holoInfo[6];
			}
			else
				saveConfig = true;
				
			
			for (World world : WorldList) 
			{
				if (world.getName().equals(holoInfo[0])) 
				{
					hologramWorld = world;
					break;
				}
			}

			double hologramXLoc = 0;
			double hologramYLoc = 0;
			double hologramZLoc = 0;
			int holoUpdateInt;

			if ( holoInfo.length >= 5 ) 
				holoUpdateInt = getHoloUpdateInterval(holoInfo[4], console);
			else 
				holoUpdateInt = getHoloUpdateInterval("", console);
			
			boolean hologramXYZValid = false;

			if ( checkDouble(holoInfo[1], console, Messages.getMessage("holocoordnf", "X=", ConfigUtils.hologramConfFile), Messages.getMessage("holocoordnv", "X=", ConfigUtils.hologramConfFile)) ) 
			{
				hologramXLoc = Double.parseDouble(holoInfo[1]);
				if ( checkDouble(holoInfo[2], console, Messages.getMessage("holocoordnf", "Y=", ConfigUtils.hologramConfFile), Messages.getMessage("holocoordnv", "Y=", ConfigUtils.hologramConfFile)) ) 
				{
					hologramYLoc = Double.parseDouble(holoInfo[2]);

					if ( checkDouble(holoInfo[3], console, Messages.getMessage("holocoordnf", "Z=", ConfigUtils.hologramConfFile), Messages.getMessage("holocoordnv", "Z=", ConfigUtils.hologramConfFile)) ) 
					{
						hologramZLoc = Double.parseDouble(holoInfo[3]);
						hologramXYZValid = true;
					}
				}
			}

			if ( (hologramWorld != null) && hologramXYZValid && (ConfigUtils.configFiles.containsKey(ConfigUtils.confHologramBoards + hologramBoard)) ) 
			{
				
				Location holoLocation = new Location(hologramWorld, hologramXLoc, hologramYLoc, hologramZLoc);
				HologramBT hologramBT = createHologram(holoLocation, hologramBoard, holoUpdateInt, topN);
				
				if ( saveConfig )
				{
					console.sendMessage(Messages.getMessage("holoconvinfo", hologramName, ConfigUtils.hologramConfFile));
					String holoConfigLine = hologramWorld.getName() + "," + hologramXLoc + "," + hologramYLoc + "," + hologramZLoc + "," + holoUpdateInt + "," + topN + "," + hologramBoard;
					hologramConfig.set(hologramName, holoConfigLine);
				}
				
				console.sendMessage(Messages.getMessage("holocrinfo2", hologramName, hologramWorld.getName(), hologramXLoc, hologramYLoc, hologramZLoc, holoUpdateInt, hologramBoard));

				balTopHolograms.put(hologramName, hologramBT);
				startHologramUpdateThread(hologramBT);
			}
		}
		
		if (saveConfig)
			saveHologramConfig(hologramConfig);
	}

	
	private void stopHologramUpdateThread(HologramBT hologramBT) 
	{
		if ( hologramBT.getUpdateTaskId() != 0 ) 
		{
			getServer().getScheduler().cancelTask(hologramBT.getUpdateTaskId());
			hologramBT.setUpdateTaskId(0);
		}
	}

	
	private void startHologramUpdateThread(HologramBT hologramBT) 
	{
		if ( hologramBT.getUpdateTaskId() != 0 ) 
			return;

		int tmpTaskId = new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				// disable thread if stopped
				if ( hologramBT.getUpdateTaskId() == 0 ) 
					this.cancel();
				else
					updateHologram(hologramBT);
			}
		}.runTaskTimerAsynchronously(this, 20, hologramBT.getUpdateInterval() * 20).getTaskId();

		hologramBT.setUpdateTaskId(tmpTaskId);
	}

	
	private void updateOnlineBals() 
	{
		if ( ! Balances.balancesInitialised )
			return;

		ArrayList<Player> onlinePlayers = Balances.getOnlinePlayers();

		for (Player onlinePlayer : onlinePlayers)
		{
			String playerName = onlinePlayer.getName();
		
			if (Balances.checkPerm(onlinePlayer,EnhancedBalTop.excludePermNode))
			{
				Balances.removeOfflinePlayer(onlinePlayer,false);
				Balances.removeOnlinePlayer(playerName);
			}
			else
				Balances.updateBalance(playerName);
		}
		
		//if (Balances.balancesChanged)
		//	Balances.sortBalances();
	}

	
	private void updateOfflineBals() 
	{
		if ( ! Balances.balancesInitialised )
			return;

		OfflinePlayer[] offlinePlayerList = plugin.getServer().getOfflinePlayers();
		for ( OfflinePlayer offlinePlayer : offlinePlayerList)
		{
			if ( Balances.hasOfflinePlayer(offlinePlayer))
			{
				if ( Balances.checkPerm(offlinePlayer,EnhancedBalTop.excludePermNode) )
				{
					Balances.removeOnlinePlayer(offlinePlayer.getName());
					Balances.removeOfflinePlayer(offlinePlayer,false);
				}
				else
					Balances.updateBalance(offlinePlayer.getName());
			}
			else
				Balances.addOfflinePlayer(offlinePlayer);
		}
	}

	
	private void updateHologramHDLine(Object hologram, int hologramLineNo, Object holoLineObj)
	{
		if (isHDHologram(hologram))
		{	
			HolographicDisplays hologramHD = (HolographicDisplays) hologram;

			if ( hologramLineNo < hologramHD.size() ) 
			{
				HDHologramLine hologramLine = hologramHD.getLine(hologramLineNo);

				if ( (hologramLine instanceof HDItemLine) && (holoLineObj instanceof ItemStack)) 
				{
					HDItemLine tmpHoloHeaderLine = (HDItemLine) hologramLine;
					ItemStack tmpHoloHeaderIStack = tmpHoloHeaderLine.getItem();

					if ( ! tmpHoloHeaderIStack.equals((ItemStack) holoLineObj) ) 
						hologramHD.updateHologramItemLine(hologramLineNo, (ItemStack) holoLineObj);
						//hologramHD.removeLine(hologramLineNo);
						//hologramHD.insertHologramItemLine(hologramLineNo, (ItemStack) holoLineObj);
					
				} 
				else if ( (hologramLine instanceof HDTextLine) && (holoLineObj instanceof String) ) 
				{
					HDTextLine tmpHoloHeaderLine = (HDTextLine) hologramLine;
					String tmpHoloHeaderStr = tmpHoloHeaderLine.getText();

					if ( ! tmpHoloHeaderStr.equals((String) holoLineObj) ) 
						hologramHD.updateHologramTextLine(hologramLineNo, (String) holoLineObj);	
						//hologramHD.removeLine(hologramLineNo);
						//hologramHD.insertHologramTextLine(hologramLineNo, (String) holoLineObj);					
				} 
				else 
				{
					hologramHD.removeLine(hologramLineNo);

					if (holoLineObj instanceof ItemStack)
						hologramHD.insertHologramItemLine(hologramLineNo, (ItemStack) holoLineObj);
					else if (holoLineObj instanceof String)
						hologramHD.insertHologramTextLine(hologramLineNo, (String) holoLineObj);
				}
			} 
			else 
			{
				if ( holoLineObj instanceof String )
					hologramHD.appendHologramTextLine((String) holoLineObj);
				else if ( holoLineObj instanceof ItemStack )
					hologramHD.appendHologramItemLine((ItemStack) holoLineObj);
			}
		}
	}

	
	private void updateHologramHologramsLine(Object hologram, int hologramLineNo, Object holoLineObj)
	{
		Holograms holograms = (Holograms) hologram;
		
		if ( hologramLineNo < holograms.size() ) 
		{
			HoloHologramLine hologramLine = holograms.getLine(hologramLineNo);

			if ((hologramLine instanceof HoloItemLine) && (holoLineObj instanceof ItemStack)) 
			{
				HoloItemLine tmpHoloHeaderLine = (HoloItemLine) hologramLine;
				ItemStack tmpHoloHeaderIStack = tmpHoloHeaderLine.getItem();

				if (! tmpHoloHeaderIStack.equals((ItemStack) holoLineObj)) 
					holograms.updateHologramItemLine(hologramLineNo, (ItemStack) holoLineObj);
			} 
			else if ( (hologramLine instanceof HoloTextLine) && (holoLineObj instanceof String) ) 
			{
				HoloTextLine tmpHoloHeaderLine = (HoloTextLine) hologramLine;
				String tmpHoloHeaderStr = tmpHoloHeaderLine.getText();

				if (! tmpHoloHeaderStr.equals((String) holoLineObj)) 
					holograms.updateHologramTextLine(hologramLineNo, (String) holoLineObj);
			} 
			else 
			{
				holograms.removeLine(hologramLineNo);
				
				if (holoLineObj instanceof ItemStack)
					holograms.insertHologramItemLine(hologramLineNo, (ItemStack) holoLineObj);
				else if (holoLineObj instanceof String)
					holograms.insertHologramTextLine(hologramLineNo, (String) holoLineObj);
			}
		} 
		else 
		{
			if (holoLineObj instanceof String)
				holograms.appendHologramTextLine((String) holoLineObj);
			else if (holoLineObj instanceof ItemStack)
				holograms.appendHologramItemLine((ItemStack) holoLineObj);
		}			
	}	
	
	private void updateHologram(HologramBT hologramBT) 
	{
		if ( ! hologramsEnabled )
			return;

		if ( ! Balances.balancesInitialised )
			return;

		if ( (hologramBT.getBoardName().isEmpty()) || (hologramBT.getBoardName() == null) )
			hologramBT.setBoardName(ConfigUtils.hologramBoardFiles[0]);
		
		if ( ! ConfigUtils.configFiles.containsKey(ConfigUtils.confHologramBoards + hologramBT.getBoardName()) )
			return;
		
		if ( hologramUpdateQueue.contains(hologramBT))
			return;
		
		ConfigUtils confUtils2 = new ConfigUtils();
		confUtils2.setHologramBoard(hologramBT.getBoardName());
		
		List<String> holoHeader = confUtils2.getConfStringArrList(ConfigUtils.confHoloHeader);
		List<String> holoFooter = confUtils2.getConfStringArrList(ConfigUtils.confHoloFooter);
		List<String> holoDetail = confUtils2.getConfStringArrList(ConfigUtils.confHoloDetail);
		
		int holoTopN = hologramBT.getTopN();
		int holoPageSize = confUtils2.getConfInt(ConfigUtils.confHoloPageSize);

		//if ( Balances.balancesChanged )
		//	Balances.sortBalances();

		// get player names sorted by balance
		String[] playerNameKeys = Balances.getPlayerBalNames(); 

		int holoPageNo = hologramBT.getPageNo();
		holoPageNo++;

		int holoTotalPages = (int) Math.ceil(playerNameKeys.length / (double) holoPageSize);

		if ( holoTotalPages * holoPageSize > holoTopN )
			holoTotalPages = (int) Math.ceil(holoTopN / (double) holoPageSize);

		if ( holoPageNo > holoTotalPages )
			holoPageNo = 1;

		hologramBT.setPageNo(holoPageNo);
		
		StringUtils strUtils2 = new StringUtils();
		strUtils2.setHoloTotalPages(holoTotalPages);
		strUtils2.setHologramBT(hologramBT);
	
		hologramBT.setHologramLines(new ArrayList<Object>());
		
		//int hologramLineNo = 0;

		for (String holoHeaderLine : holoHeader) 
			hologramBT.addHologramLines(strUtils2.formatHoloLine(holoHeaderLine).getFmtStr());
			
		for (int tmpPlayerNo = (holoPageNo - 1) * holoPageSize; tmpPlayerNo < (holoPageNo * holoPageSize); tmpPlayerNo++) 
		{
			if (tmpPlayerNo >= holoTopN || tmpPlayerNo >= playerNameKeys.length)
				break;

			String tmpPlayerName = playerNameKeys[tmpPlayerNo];
			double tmpPlayerBal = Balances.getBalance(tmpPlayerName);
			strUtils2.setLineNumber(tmpPlayerNo + 1);
			strUtils2.setPlayer(Balances.getOfflinePlayer(tmpPlayerName));
			strUtils2.setPlayerBal(tmpPlayerBal);
			
			for (int lineNo = 0; lineNo < holoDetail.size(); lineNo++) 
				hologramBT.addHologramLines(strUtils2.formatHoloLine(holoDetail.get(lineNo)).getFmtStr());				
		}

		// Update Hologram Footer Line
		for (String holoFooterLine : holoFooter) 
			hologramBT.addHologramLines(strUtils2.formatHoloLine(holoFooterLine).getFmtStr());				
		
		//hologramUpdateQueue.offer(hologramBT);
		hologramUpdateQueue.add(hologramBT);
	}

	
	public static synchronized void updateHologramObjs()
	{
		while (hologramUpdateQueue.size() > 0)
		{
			if ( ! updatingHologramObj)
			{
				HologramBT hologramBT = hologramUpdateQueue.get(0);
				plugin.updateHologramObj(hologramBT);
				hologramUpdateQueue.remove(0);
			}
		}
	}
	
	
	public void updateHologramObj(HologramBT hologramBT)
	{
		updatingHologramObj = true;
		
		Object hologram = hologramBT.getHologram();
		int lineCnt = hologramBT.getHologramLines().size();
		
		if (isHDHologram(hologram))
		{
			for (int hologramLineNo = 0; hologramLineNo < lineCnt; hologramLineNo++)
				updateHologramHDLine(hologram, hologramLineNo, hologramBT.getHologramLine(hologramLineNo));
			
			// remove any extra lines (if current page shrank!)
			HolographicDisplays hologramHD = (HolographicDisplays) hologram;
			int holoSize = hologramHD.size();
			
			for (int hologramLineNo = lineCnt; hologramLineNo < holoSize; hologramLineNo++)
				hologramHD.removeLine(lineCnt);
		} 
		else if (isHologramsHologram(hologram))
		{
			for (int hologramLineNo = 0; hologramLineNo < lineCnt; hologramLineNo++)
				updateHologramHologramsLine(hologram, hologramLineNo, hologramBT.getHologramLine(hologramLineNo));
			
			// remove any extra lines (if current page shrank!)
			Holograms holograms = (Holograms) hologram;
			int holoSize = holograms.size();
				
			for (int hologramLineNo = lineCnt; hologramLineNo < holoSize; hologramLineNo++)
				holograms.removeLine(lineCnt);
		}
		
		updatingHologramObj = false;
	}
	
	
	private void updateHologram2(HologramBT hologramBT) 
	{
		if ( ! hologramsEnabled )
			return;

		if ( ! Balances.balancesInitialised )
			return;

		if ( (hologramBT.getBoardName().isEmpty()) || (hologramBT.getBoardName() == null) )
			hologramBT.setBoardName(ConfigUtils.hologramBoardFiles[0]);
		
		if ( ! ConfigUtils.configFiles.containsKey(ConfigUtils.confHologramBoards + hologramBT.getBoardName()) )
			return;
		
		confUtils.setHologramBoard(hologramBT.getBoardName());
		
		List<String> holoHeader = confUtils.getConfStringArrList(ConfigUtils.confHoloHeader);
		List<String> holoFooter = confUtils.getConfStringArrList(ConfigUtils.confHoloFooter);
		List<String> holoDetail = confUtils.getConfStringArrList(ConfigUtils.confHoloDetail);
		
		int holoTopN = hologramBT.getTopN();
		int holoPageSize = confUtils.getConfInt(ConfigUtils.confHoloPageSize);

		//if ( Balances.balancesChanged )
		//	Balances.sortBalances();

		// get player names sorted by balance
		String[] playerNameKeys = Balances.getPlayerBalNames(); 

		int holoPageNo = hologramBT.getPageNo();
		holoPageNo++;

		int holoTotalPages = (int) Math.ceil(playerNameKeys.length / (double) holoPageSize);

		if ( holoTotalPages * holoPageSize > holoTopN )
			holoTotalPages = (int) Math.ceil(holoTopN / (double) holoPageSize);

		if ( holoPageNo > holoTotalPages )
			holoPageNo = 1;

		hologramBT.setPageNo(holoPageNo);
		strUtils.setHoloTotalPages(holoTotalPages);
		strUtils.setHologramBT(hologramBT);
		
		Object hologram = hologramBT.getHologram();
		if (isHDHologram(hologram))
			((HolographicDisplays) hologram).clearLines();
		
		else if (isHologramsHologram(hologram))
		{
			((Holograms) hologram).delete();
			hologram = createHologram(hologramBT.getLocation());
			hologramBT.setHologram(hologram);
		}
		
		int hologramLineNo = 0;
	
		for (String holoHeaderLine : holoHeader) 
		{
			Object holoHeaderLineObj = strUtils.formatHoloLine(holoHeaderLine).getFmtStr();

			if (holoHeaderLineObj instanceof ItemStack)
				appendHologramItemLine(hologram, (ItemStack) holoHeaderLineObj);
			else if (holoHeaderLineObj instanceof String)
				appendHologramTextLine(hologram, (String) holoHeaderLineObj);
				
			hologramLineNo++;
		}

		for (int tmpPlayerNo = (holoPageNo - 1) * holoPageSize; tmpPlayerNo < (holoPageNo * holoPageSize); tmpPlayerNo++) 
		{
			if (tmpPlayerNo >= holoTopN || tmpPlayerNo >= playerNameKeys.length)
				break;

			String tmpPlayerName = playerNameKeys[tmpPlayerNo];
			double tmpPlayerBal = Balances.getBalance(tmpPlayerName);
			strUtils.setLineNumber(tmpPlayerNo + 1);
			strUtils.setPlayer(Balances.getOfflinePlayer(tmpPlayerName));
			strUtils.setPlayerBal(tmpPlayerBal);
			
			for (int lineNo = 0; lineNo < holoDetail.size(); lineNo++) 
			{
				Object holoDetailLineObj = strUtils.formatHoloLine(holoDetail.get(lineNo)).getFmtStr();

				if (holoDetailLineObj instanceof ItemStack)
					appendHologramItemLine(hologram, (ItemStack) holoDetailLineObj);
				else if (holoDetailLineObj instanceof String)
					appendHologramTextLine(hologram, (String) holoDetailLineObj);
				
				hologramLineNo++;
			}

			if ( tmpPlayerNo > holoTopN )
				break;
		}

		// Update Hologram Footer Line
		for (String holoFooterLine : holoFooter) 
		{
			Object holoFooterLineObj = strUtils.formatHoloLine(holoFooterLine).getFmtStr();

			if (holoFooterLineObj instanceof ItemStack)
				appendHologramItemLine(hologram, (ItemStack) holoFooterLineObj);
			else if (holoFooterLineObj instanceof String)
				appendHologramTextLine(hologram, (String) holoFooterLineObj);
			hologramLineNo++;
		}
	}

	
	
	
	private int getHoloUpdateInterval(String intervalStr, CommandSender tmpSender) 
	{
		if (intervalStr != "") 
		{
			String IntervalNotValidMsg = Messages.getMessage("holointnv", intervalStr);

			if (checkInt(intervalStr, tmpSender, IntervalNotValidMsg, IntervalNotValidMsg)) 
				return Integer.parseInt(intervalStr);
		}

		String holoUpdateIntStr = confUtils.getConfStr(ConfigUtils.confHoloUpdateInterval);
		String holoUpdateNVMsg = Messages.getMessage("holoupdatenv", holoUpdateIntStr, ConfigUtils.defHoloUpdateInterval);

		if (checkInt(holoUpdateIntStr, tmpSender, holoUpdateNVMsg, holoUpdateNVMsg))
			return Integer.parseInt(holoUpdateIntStr);
		else
			return ConfigUtils.defHoloUpdateInterval;
	}

	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) 
	{
		Player tmpPlayer = event.getPlayer();
		Balances.addOnlinePlayer(tmpPlayer);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) 
	{
		Player tmpPlayer = event.getPlayer();
		Balances.removeOnlinePlayer(tmpPlayer);
	}

	
	@EventHandler
	public void onConsoleCommand(ServerCommandEvent event) 
	{
		String[] cmd = event.getCommand().split(" ");
		String cmdArgs = "";

		for (int i = 1; i < cmd.length; i++) 
			cmdArgs += " " + cmd[i];
		

		List<String> cmdAliases = confUtils.getConfStringArrList(ConfigUtils.confCmdAliases);

		for (int i = 0; i < cmdAliases.size(); i++) 
		{
			String tmpCmdAlias = cmdAliases.get(i).toLowerCase().replace("/", "");

			if ( cmd[0].equalsIgnoreCase(tmpCmdAlias) ) 
			{
				cmdAlias = tmpCmdAlias;
				event.setCommand("baltop" + cmdArgs);
			}
		}
	}

	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) 
	{
		String[] cmd = e.getMessage().substring(1).split("\\s+");
		String cmdArgs = "";

		for (int i = 1; i < cmd.length; i++) 
			cmdArgs += " " + cmd[i];
	
		List<String> cmdAliases = confUtils.getConfStringArrList(ConfigUtils.confCmdAliases);

		for (int i = 0; i < cmdAliases.size(); i++) 
		{
			String tmpCmdAlias = cmdAliases.get(i).toLowerCase().replace("/", "");

			if (cmd[0].equalsIgnoreCase(tmpCmdAlias)) 
			{
				cmdAlias = tmpCmdAlias;
				e.getPlayer().chat("/baltop" + cmdArgs);
				e.setCancelled(true);
			}
		}
	}

	
	// Fired when plugin is first enabled
	@Override
	public void onEnable() 
	{
		console = this.getServer().getConsoleSender();
		plugin = this;
		
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		
		// create default config from jar if it don't exist!
		//updateConfig();
		confUtils = new ConfigUtils(true);
		strUtils = new StringUtils();
				
		@SuppressWarnings("unused")
		Messages messages = new Messages(this);
		cmdprefix = Messages.getMessage("cmdprefix");

		// check if plugin disabled from config 'enable'
		if ( ! confUtils.getConfBoolean(ConfigUtils.confEnable) )
		{
				this.setEnabled(false);
				return;
		}

		if ( confUtils.getConfBoolean(ConfigUtils.confCheckUpdates) )
		{
			String currentVersion = this.getDescription().getVersion();

			if ( Updater.checkUpdate(currentVersion) ) 
				console.sendMessage(Messages.getMessage("newversion", Updater.latestVersion));			
		}

		topnbal = confUtils.getConfInt(ConfigUtils.confdefTopN);
		excludeDays = confUtils.getConfInt(ConfigUtils.confExcludeDays);
		
		excludePlayerList = confUtils.getConfStringArrList(ConfigUtils.confExcludedPlayers);
		excludePermNode = confUtils.getConfStr(ConfigUtils.confExcludePermNode);

		hookEconomy = setupEconomy();

		if ( hookEconomy ) 
			console.sendMessage(Messages.getMessage("hookvaultecon2", econ.getName()));
		else 
		{
			console.sendMessage(Messages.getMessage("vaultnf"));
			// getServer().getPluginManager().disablePlugin(this);

			// disable plugin
			setEnabled(false);
			return;
		}

		Balances.hookPermissions = Balances.setupPermissions();
		if ( Balances.hookPermissions ) 
			console.sendMessage(Messages.getMessage("hookvaultperm", Balances.permsProvider));
		
		enabledUUID = confUtils.getConfBoolean(ConfigUtils.confEnableUUID);

		// get offline/All player balances
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				if ( Balances.balancesInitialised )
					this.cancel();
			
				if ( ! balancesRunning ) 
				{
					balancesRunning = true;
					Balances.getBalances(true);
					balancesRunning = false;
					this.cancel();
				}
			}
		}.runTaskTimer(this, 100, ConfigUtils.initBalInterval);


		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				String pluginVer = hookPlugin("PlaceholderAPI");
				if (pluginVer != null) 
				{
					hookPlaceHolderAPI = true;
					console.sendMessage(Messages.getMessage("hookplaceholder2", pluginVer));
					this.cancel();
				}

			}
		}.runTaskTimerAsynchronously(this, 0, 100);
		
		
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				if ( hologramsEnabled)
					this.cancel();
				else
				{
					String pluginVer = hookPlugin("HolographicDisplays");
					if ( pluginVer != null) 
					{
						hologramsEnabled = true;
						hookHolographicDisplays = true;
						console.sendMessage(Messages.getMessage("hookholodisp", pluginVer));
						this.cancel();
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 0, 100);

		
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				if ( hologramsEnabled)
					this.cancel();
				else
				{
					String pluginVer = hookPlugin("Holograms");
					if ( pluginVer != null) 
					{
						hologramsEnabled = true;
						hookHolographicDisplays = false;
						
						hologramManager = EnhancedBalTop.getPlugin(HologramPlugin.class).getHologramManager();
						
						console.sendMessage(Messages.getMessage("hookholograms", pluginVer));
						this.cancel();
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 0, 100);

		if ( ! hologramsSetup)
		{
			new BukkitRunnable() 
			{
				@Override
				public void run() 
				{
					if ( Balances.balancesInitialised && hologramsEnabled )
					{
						setupHolograms();
						hologramsSetup=true;
						this.cancel();
					}
				}
			}.runTaskTimer(this, 100, ConfigUtils.hologramSetupInterval);	
		}
			
		
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				updateHologramObjs();
			}
		}.runTaskTimer(this, 100, 5);
		
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				// Log.info("DEBUG: Economy Provider=" +
				// getServer().getServicesManager().getRegistration(Economy.class).getProvider());
				updateOnlineBals();
			}
		}.runTaskTimerAsynchronously(this, 0, confUtils.getConfInt(ConfigUtils.confUpdateOnlineBalInterval));
		
		
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				// Log.info("DEBUG: Economy Provider=" +
				// getServer().getServicesManager().getRegistration(Economy.class).getProvider());
				updateOfflineBals();
			}
		}.runTaskTimerAsynchronously(this, 0, confUtils.getConfInt(ConfigUtils.confUpdateOfflineBalInterval));

		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				if (Balances.balancesChanged)
					Balances.sortBalances();
			}
		}.runTaskTimerAsynchronously(this, 100, confUtils.getConfInt(ConfigUtils.confSortBalInterval));
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (cmd.getName().equalsIgnoreCase(ConfigUtils.balTopCmd)) 
		{
			pageNo = 1;

			if ( args.length >= 1 ) 
			{
				if ( ! sender.hasPermission(ConfigUtils.enableCmdsPerm) ) 
				{
					try 
					{
						Integer.parseInt(args[0]);
					} 
					catch (Exception e1) 
					{
						args[0] = "";
					}
				}

				switch (args[0].toLowerCase()) 
				{
					case "help":
						sender.sendMessage(Messages.getMessage("usage", cmdAlias));
						sender.sendMessage(Messages.getMessage("reloadusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("topnbalusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holotopnusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("excllistusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("excladdusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("exclremusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("hololistusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holoaddusage2", cmdAlias));
						sender.sendMessage(Messages.getMessage("holoremusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holomoveusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holotpusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("hologramtopnusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holoboardfileusage", cmdAlias));
						return true;
						
					case "topnbal":
	
						if ( args.length == 1 ) 
						{
							sender.sendMessage(Messages.getMessage("topnbalnp"));
							sender.sendMessage(Messages.getMessage("topnbalusage", cmdAlias));
							return false;
						}
	
						if ( args.length > 2 ) 
						{
							sender.sendMessage(Messages.getMessage("topnbaltp", Arrays.asList(args).subList(1, args.length)));
							sender.sendMessage(Messages.getMessage("topnbalusage", cmdAlias));
							return false;
						}
	
						if ( ! checkInt(args[1], sender, Messages.getMessage("topnbalnv", args[1]), Messages.getMessage("topnbalnv", args[1])) ) 
						{
							sender.sendMessage(Messages.getMessage("topnbalusage", cmdAlias));
							return false;
						}
	
						int tmpTopNBal = Integer.parseInt(args[1]);
	
						if ( tmpTopNBal < 1 ) 
						{
							sender.sendMessage(Messages.getMessage("topnbalnv", args[1]));
							return false;
						}
	
						if ( (! sender.hasPermission(ConfigUtils.topnbalPerm) ) && (!sender.hasPermission(ConfigUtils.adminPerm) ) && 
							 (! sender.hasPermission(ConfigUtils.oldTopnbalPerm) ) && (! sender.hasPermission(ConfigUtils.oldAdminPerm) ) ) 
						{
							sender.sendMessage(Messages.getMessage("topnbalperm", cmdAlias));
							return false;
						}
	
						topnbal = tmpTopNBal;
	
						confUtils.setConf(ConfigUtils.confdefTopN, this.topnbal);
						saveConfig();
						
						sender.sendMessage(Messages.getMessage("topnbalset", this.topnbal));
						return true;

				case "holotopn":

					if ( args.length == 1 ) 
					{
						sender.sendMessage(Messages.getMessage("holotopnnp"));
						sender.sendMessage(Messages.getMessage("holotopnusage", cmdAlias));
						return false;
					}

					if ( args.length > 2 ) 
					{
						sender.sendMessage(Messages.getMessage("holotopntp", Arrays.asList(args).subList(1, args.length)));
						sender.sendMessage(Messages.getMessage("holotopnusage", cmdAlias));
						return false;
					}

					int tmpHoloTopN;

					if ( ! checkInt(args[1], sender, Messages.getMessage("holotopnnv", args[1]), Messages.getMessage("holotopnnv", args[1])) ) 
					{
						sender.sendMessage(Messages.getMessage("holotopnusage", cmdAlias));
						return false;
					}

					tmpHoloTopN = Integer.parseInt(args[1]);

					if (tmpHoloTopN < 1) 
					{
						sender.sendMessage(Messages.getMessage("holotopnnv", args[1]));
						return false;
					}

					if ( (! sender.hasPermission(ConfigUtils.holoTopNPerm) ) && (! sender.hasPermission(ConfigUtils.adminPerm)) && 
						 (! sender.hasPermission(ConfigUtils.oldHoloTopNPerm) ) && (! sender.hasPermission(ConfigUtils.oldAdminPerm)) ) 
					{
						sender.sendMessage(Messages.getMessage("holotopnperm", cmdAlias));
						return false;
					}

					holoTopN = tmpHoloTopN;
					confUtils.setConf(ConfigUtils.confHoloTopN, holoTopN);
					saveConfig();
					
					// reset ALL Holograms to Page 1
					// update all holograms immediately to reflect new top N
					// board (holotopn)
					
					for (String hologramName : balTopHolograms.keySet()) 
					{
						HologramBT hologramBT = balTopHolograms.get(hologramName);
						hologramBT.setPageNo(0);
												
						updateHologram(hologramBT);
					}
					
					sender.sendMessage(Messages.getMessage("holotopnset", this.holoTopN));
					return true;

				case "reload":
					if ( args.length > 1 ) 
					{
						sender.sendMessage(Messages.getMessage("reloadtp", Arrays.asList(args).subList(1, args.length)));
						sender.sendMessage(Messages.getMessage("reloadusage", cmdAlias));
						return false;
					}

					if ( (! sender.hasPermission(ConfigUtils.adminPerm)) && (! sender.hasPermission(ConfigUtils.oldAdminPerm)) ) 
					{
						sender.sendMessage(Messages.getMessage("reloadperm", cmdAlias));
						return false;
					}

					reloadConfig();
					setEnabled(false);
					setEnabled(true);
					console.sendMessage(Messages.getMessage("reloadplugin"));
					sender.sendMessage(Messages.getMessage("reloadconfig"));
					return true;

				case "hologram":
					if ( ! hologramsEnabled ) 
					{
						sender.sendMessage(Messages.getMessage("holopluginnf"));
						return false;
					}

					if (sender instanceof Player) 
					{
						Player player = (Player) sender;
						if ( (! player.hasPermission(ConfigUtils.hologramPerm)) && (! player.hasPermission(ConfigUtils.adminPerm)) && 
						     (! player.hasPermission(ConfigUtils.oldHologramPerm) ) && (! player.hasPermission(ConfigUtils.oldAdminPerm)) ) 
						{
							sender.sendMessage(Messages.getMessage("holoperm", cmdAlias));
							return false;
						}
					}

					if (args.length < 2) 
					{
						sender.sendMessage(Messages.getMessage("holonp"));
						sender.sendMessage(Messages.getMessage("hololistusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holoaddusage2", cmdAlias));
						sender.sendMessage(Messages.getMessage("holoremusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holoremusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holomoveusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holotpusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("hologramtopnusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holoboardfileusage", cmdAlias));
						return false;
					} 
					else 
					{
						FileConfiguration hologramConfig = getHolograms();
						Set<String> hologramList = balTopHolograms.keySet();

						switch (args[1].toLowerCase()) 
						{
							case "tp":
								if (args.length == 2) 
								{
									sender.sendMessage(Messages.getMessage("holotpnh"));
									sender.sendMessage(Messages.getMessage("holotpusage", cmdAlias));
									return false;
								}
	
								if (args.length > 3) 
								{
									sender.sendMessage(Messages.getMessage("holotptp", Arrays.asList(args).subList(1, args.length)));
									sender.sendMessage(Messages.getMessage("holotpusage", cmdAlias));
									return false;
								}
	
								String holoName = args[2];
	
								if ( sender instanceof Player ) 
								{
									Player player = (Player) sender;
									if ( hologramList.contains(holoName) ) 
									{
										HologramBT hologramBT = balTopHolograms.get(holoName);
										Object hologram = hologramBT.getHologram();
										int holoSize = 0;
										
										if ( isHDHologram(hologram))
											holoSize = ((HolographicDisplays) hologram).size();
										else if (isHologramsHologram(hologram))
											holoSize = ((Holograms) hologram).size();
										
										Location holoLoc = hologramBT.getLocation();
	
										DecimalFormat df = new DecimalFormat("#.###");
	
										double holoXLoc = holoLoc.getX();
										double holoYLoc = holoLoc.getY();
										double holoYLoc2 = holoYLoc - (holoSize / 3);
										// Log.info("DEBUG: hologram tp, name = " +
										// holoName+ ",size = " + hologram.size());
										double holoZLoc = holoLoc.getZ();
										double holoZLoc2 = holoZLoc - (holoSize / 5);
	
										String holoXLocStr = df.format(holoXLoc);
										String holoYLocStr = df.format(holoYLoc);
										String holoZLocStr = df.format(holoZLoc);
										World holoWorld = holoLoc.getWorld();
										String holoWorldStr = holoWorld.getName();
	
										Location tpLoc = new Location(holoWorld, holoXLoc, holoYLoc2, holoZLoc2);
										player.teleport(tpLoc);
	
										sender.sendMessage(Messages.getMessage("holotpinfo", holoName, holoWorldStr, holoXLocStr, holoYLocStr, holoZLocStr));
										return true;
									} 
									else 
									{
										sender.sendMessage(Messages.getMessage("holotpnf", holoName));
										return false;
									}
								} 
								else 
								{
									sender.sendMessage(Messages.getMessage("holotpcs", cmdAlias));
									return false;
								}

							case "list":
								if (args.length > 2) 
								{
									sender.sendMessage(Messages.getMessage("hololisttp", Arrays.asList(args).subList(2, args.length)));
									sender.sendMessage(Messages.getMessage("hololistusage", cmdAlias));
									return false;
								}
	
								int hologramCnt = 1;
	
								sender.sendMessage(Messages.getMessage("hololist"));
	
								for (String hologramName : hologramList) 
								{
									HologramBT hologramBT = balTopHolograms.get(hologramName);
									
									Location holoLoc = hologramBT.getLocation();
									
									DecimalFormat df = new DecimalFormat("#.###");
									String hologramXLoc = df.format(holoLoc.getX());
									String hologramYLoc = df.format(holoLoc.getY());
									String hologramZLoc = df.format(holoLoc.getZ());
	
									String holoWorld = holoLoc.getWorld().getName();
									int holoUpdateInt = hologramBT.getUpdateInterval();
									String holoBoard = hologramBT.getBoardName();
									
									sender.sendMessage(Messages.getMessage("holoinfo2", hologramCnt, hologramName, holoWorld, hologramXLoc, hologramYLoc, hologramZLoc, holoUpdateInt, holoBoard));
									hologramCnt++;
								}
	
								return true;
	
							case "add":
								if (args.length == 2) 
								{
									sender.sendMessage(Messages.getMessage("holoaddnh"));
									sender.sendMessage(Messages.getMessage("holoaddusage2", cmdAlias));
									return false;
								} 
								else 
								{
									if (args.length > 5) 
									{
										sender.sendMessage(Messages.getMessage("holoaddtp", Arrays.asList(args).subList(3, args.length)));
										sender.sendMessage(Messages.getMessage("holoaddusage2", cmdAlias));
										return false;
									}
	
									String hologramName = args[2];
									int holoUpdateInterval;
	
									// Now Get and validate hologram update interval
									if (args.length > 3) 
										holoUpdateInterval = getHoloUpdateInterval(args[3], sender);
									else 
										holoUpdateInterval = getHoloUpdateInterval("", sender);
									
									String holoBoardFile = ConfigUtils.hologramBoardFiles[0];
									
									if ( args.length > 4)
									{
										holoBoardFile = args[4];
										if (! holoBoardFile.endsWith(".yml"))
											holoBoardFile+= ".yml";
										
										if (! ConfigUtils.configFiles.containsKey(ConfigUtils.confHologramBoards + holoBoardFile))
										{
											sender.sendMessage(Messages.getMessage("holoconfnf", holoBoardFile));
											return false;
										}	
									}
										
									if ( hologramList.contains(hologramName) ) 
									{
										sender.sendMessage(Messages.getMessage("holoaddae", hologramName));
										return false;
									}
	
									if (sender instanceof Player) 
									{
										Player player = (Player) sender;
	
										Location holoLocation = player.getLocation().add(0.0, 6.0, 0.0);
	
										DecimalFormat df = new DecimalFormat("#.###");
	
										String holoWorld = holoLocation.getWorld().getName();
	
										String hologramXLoc = df.format(holoLocation.getX());
										String hologramYLoc = df.format(holoLocation.getY());
										String hologramZLoc = df.format(holoLocation.getZ());
	
										String holoConfigLine = holoWorld + "," + hologramXLoc + "," + hologramYLoc + "," + hologramZLoc + "," + String.valueOf(holoUpdateInterval) + "," + holoTopN + "," + holoBoardFile;
	
										holoTopN = confUtils.getConfInt(ConfigUtils.confHoloTopN);
	
										//ArrayList<String> tmpPlayerList = Balances.getOfflinePlayerList();
	
										HologramBT hologramBT = createHologram(holoLocation, holoBoardFile, holoUpdateInterval, holoTopN);	
										balTopHolograms.put(hologramName, hologramBT);
										
										hologramConfig.set(hologramName, holoConfigLine);
										saveHologramConfig(hologramConfig);
	
										startHologramUpdateThread(hologramBT);
	
										sender.sendMessage(Messages.getMessage("holocrinfo2", hologramName, holoWorld, hologramXLoc, hologramYLoc, hologramZLoc, holoUpdateInterval, holoBoardFile));
										
										return true;
									} 
									else 
									{
										sender.sendMessage(Messages.getMessage("holoaddcs", cmdAlias));
										return false;
									}
	
								}
	
							case "remove":
								if (args.length == 2) 
								{
									sender.sendMessage(Messages.getMessage("holoremnh"));
									sender.sendMessage(Messages.getMessage("holoremusage", cmdAlias));
									return false;
								} 
								else 
								{
									if (args.length > 3) 
									{
										sender.sendMessage(Messages.getMessage("holoremtp", Arrays.asList(args).subList(3, args.length)));
										sender.sendMessage(Messages.getMessage("holoremusage", cmdAlias));
										return false;
									}
	
									String hologramName = args[2];
	
									if ( hologramList.contains(hologramName) ) 
									{
										HologramBT hologramBT = balTopHolograms.get(hologramName);
										Location holoLoc = hologramBT.getLocation();
										
										String holoWorld = holoLoc.getWorld().getName();
										DecimalFormat df = new DecimalFormat("#.###");
										String hologramXLoc = df.format(holoLoc.getX());
										String hologramYLoc = df.format(holoLoc.getY());
										String hologramZLoc = df.format(holoLoc.getZ());
	
										hologramConfig.set(hologramName, null);
										saveHologramConfig(hologramConfig);

										Object hologram = hologramBT.getHologram();
										if (isHDHologram(hologram))
											((HolographicDisplays) hologram).delete();
										else if (isHologramsHologram(hologram))
											((Holograms) hologram).delete();
										
										balTopHolograms.remove(hologramName);
	
										stopHologramUpdateThread(hologramBT);
	
										sender.sendMessage(Messages.getMessage("holoreminfo", hologramName, holoWorld, hologramXLoc, hologramYLoc, hologramZLoc));
										return true;
									} 
									else 
									{
										sender.sendMessage(Messages.getMessage("holoremnf", hologramName));
										return false;
									}
								}
	
							case "topn" :
								if (args.length == 2) 
								{
									sender.sendMessage(Messages.getMessage("hologramtopnnh"));
									sender.sendMessage(Messages.getMessage("hologramtopnusage", cmdAlias));
									return false;
								} 
								else 
								{
									if (args.length > 4) 
									{
										sender.sendMessage(Messages.getMessage("hologramtopntp", Arrays.asList(args).subList(4, args.length)));
										sender.sendMessage(Messages.getMessage("hologramtopnusage", cmdAlias));
										return false;
									}
									
									String hologramName = args[2];
									
									int topN=holoTopN;

									if ( ! checkInt(args[3], sender, Messages.getMessage("hologramtopnnv", args[3]), Messages.getMessage("hologramtopnnv", args[3])) ) 
									{
										sender.sendMessage(Messages.getMessage("hologramtopnusage", cmdAlias));
										return false;
									}

									topN = Integer.parseInt(args[3]);

									if (topN < 1) 
									{
										sender.sendMessage(Messages.getMessage("hologramtopnnv", topN));
										return false;
									}
									
									if ( hologramList.contains(hologramName) ) 
									{
										HologramBT hologramBT = balTopHolograms.get(hologramName);
										hologramBT.setTopN(topN);
										
										int holoUpdateInterval = hologramBT.getUpdateInterval();
										String holoBoardFileName = hologramBT.getBoardName();
										Location holoLocation = hologramBT.getLocation();
										DecimalFormat df = new DecimalFormat("#.###");
										
										String holoWorld = holoLocation.getWorld().getName();
	
										String hologramXLoc = df.format(holoLocation.getX());
										String hologramYLoc = df.format(holoLocation.getY());
										String hologramZLoc = df.format(holoLocation.getZ());
	
										String holoConfigLine = holoWorld + "," + hologramXLoc + "," + hologramYLoc + "," + hologramZLoc + "," + String.valueOf(holoUpdateInterval) + "," + topN + "," + holoBoardFileName;
										hologramConfig.set(hologramName, holoConfigLine);
										saveHologramConfig(hologramConfig);
										
										sender.sendMessage(Messages.getMessage("hologramtopninfo", hologramName, topN));
										return true;
									}
									else 
									{
										sender.sendMessage(Messages.getMessage("hologramtopnnf", hologramName));
										return false;
									}
								}
							
							case "boardfile":
								if (args.length == 2) 
								{
									sender.sendMessage(Messages.getMessage("holoboardfilenh"));
									sender.sendMessage(Messages.getMessage("holoboardfilenusage", cmdAlias));
									return false;
								} 
								else if (args.length == 3) 
								{
									sender.sendMessage(Messages.getMessage("holoboardfilenbf"));
									sender.sendMessage(Messages.getMessage("holoboardfilenusage", cmdAlias));
									return false;
								} 
								else if (args.length > 5) 
								{
									sender.sendMessage(Messages.getMessage("holoboardfilentp", Arrays.asList(args).subList(5, args.length)));
									sender.sendMessage(Messages.getMessage("holoboardfileusage", cmdAlias));
									return false;
								}
								else 
								{	
									String hologramName = args[2];
									String boardFile = args[3];
								
									if ( hologramList.contains(hologramName) ) 
									{
										if (! boardFile.endsWith(".yml"))
											boardFile += ".yml";
										
										if (ConfigUtils.configFiles.containsKey(ConfigUtils.confHologramBoards + boardFile))
										{
											HologramBT hologramBT = balTopHolograms.get(hologramName);
											hologramBT.setBoardName(boardFile);
											
											int topN = hologramBT.getTopN();
											int holoUpdateInterval = hologramBT.getUpdateInterval();
											
											Location holoLocation = hologramBT.getLocation();
											DecimalFormat df = new DecimalFormat("#.###");
											
											String holoWorld = holoLocation.getWorld().getName();
		
											String hologramXLoc = df.format(holoLocation.getX());
											String hologramYLoc = df.format(holoLocation.getY());
											String hologramZLoc = df.format(holoLocation.getZ());
		
											String holoConfigLine = holoWorld + "," + hologramXLoc + "," + hologramYLoc + "," + hologramZLoc + "," + String.valueOf(holoUpdateInterval) + "," + topN + "," + boardFile;
											hologramConfig.set(hologramName, holoConfigLine);
											saveHologramConfig(hologramConfig);
											updateHologram(hologramBT);
											
											sender.sendMessage(Messages.getMessage("holoboardfileinfo", hologramName, boardFile)); 
											return true;
										}
										else
										{
											sender.sendMessage(Messages.getMessage("holoboardfilebfnf", boardFile));
										
											return false;
										}
									}
									else
									{
										sender.sendMessage(Messages.getMessage("holoboardfilehnf", hologramName));
										return false;
									}
								}
								
								
							case "movehere":
								if (args.length == 2) 
								{
									sender.sendMessage(Messages.getMessage("holomovenh"));
									sender.sendMessage(Messages.getMessage("holomoveusage", cmdAlias));
									return false;
								} 
								else 
								{
									if (args.length > 3) 
									{
										sender.sendMessage(Messages.getMessage("holomovetp", Arrays.asList(args).subList(3, args.length)));
										sender.sendMessage(Messages.getMessage("holomoveusage", cmdAlias));
										return false;
									}
	
									if ( sender instanceof Player) 
									{
										Player player = (Player) sender;
	
										Location holoLocation = player.getLocation().add(0.0, 6.0, 0.0);
										String hologramName = args[2];
	
										if ( hologramList.contains(hologramName) ) 
										{
											HologramBT hologramBT = balTopHolograms.get(hologramName);
											hologramBT.setLocation(holoLocation);
											
											int holoUpdInt = hologramBT.getUpdateInterval();
											int topN = hologramBT.getTopN();
											String holoBoardFileName = hologramBT.getBoardName();
											
											//balTopHolograms.put(hologramName, hologramBT);
	
											String holoWorld = holoLocation.getWorld().getName();
											DecimalFormat df = new DecimalFormat("#.###");
											String hologramXLoc = df.format(holoLocation.getX());
											String hologramYLoc = df.format(holoLocation.getY());
											String hologramZLoc = df.format(holoLocation.getZ());
	
											String holoConfigLine2 = holoWorld + "," + hologramXLoc + "," + hologramYLoc + "," + hologramZLoc + "," + String.valueOf(holoUpdInt) + "," + topN + "," + holoBoardFileName;
											hologramConfig.set(hologramName, holoConfigLine2);
											saveHologramConfig(hologramConfig);

											sender.sendMessage(Messages.getMessage("holomoveinfo", hologramName, holoWorld, hologramXLoc, hologramYLoc, hologramZLoc));
	
											return true;
										} 
										else 
										{
											sender.sendMessage(Messages.getMessage("holmovenf", hologramName));
											return false;
										}
	
									} 
									else 
									{
										sender.sendMessage(Messages.getMessage("holomovecs"));
										return false;
									}
								}
	
							default:
								sender.sendMessage(Messages.getMessage("holotp", Arrays.asList(args).subList(1, args.length)));
								sender.sendMessage(Messages.getMessage("hololistusage", cmdAlias));
								sender.sendMessage(Messages.getMessage("holoaddusage2", cmdAlias));
								sender.sendMessage(Messages.getMessage("holoremusage", cmdAlias));
								sender.sendMessage(Messages.getMessage("holoremusage", cmdAlias));
								sender.sendMessage(Messages.getMessage("holomoveusage", cmdAlias));
								sender.sendMessage(Messages.getMessage("holotpusage", cmdAlias));
								sender.sendMessage(Messages.getMessage("hologramtopnusage", cmdAlias));
								sender.sendMessage(Messages.getMessage("holoboardfileusage", cmdAlias));
								return false;
						}
					}

				case "exclude":
					if (sender instanceof Player) 
					{
						Player player = (Player) sender;
						if ( (! player.hasPermission(ConfigUtils.excludePerm)) && (! player.hasPermission(ConfigUtils.adminPerm)) && 
							 (! player.hasPermission(ConfigUtils.oldExcludePerm)) && (! player.hasPermission(ConfigUtils.oldAdminPerm)) ) 
						{
							sender.sendMessage(Messages.getMessage("exclperm", cmdAlias));
							return false;
						}
					}

					if (args.length < 2) 
					{
						sender.sendMessage(Messages.getMessage("exclnp"));
						sender.sendMessage(Messages.getMessage("excllistusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("excladdusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("exclremusage", cmdAlias));
						return false;
					} 
					else 
					{
						UUID excludeUUID;
						OfflinePlayer excludePlayer = null;
						String excludePlayerName = "";

						switch (args[1].toLowerCase()) 
						{
							case "list":
								if ( args.length > 2 ) 
								{
									sender.sendMessage(Messages.getMessage("excllisttp", Arrays.asList(args).subList(2, args.length)));
									sender.sendMessage(Messages.getMessage("excllistusage", cmdAlias));
									return false;
								}

								sender.sendMessage(Messages.getMessage("excllist"));

								for (int i = 0; i < excludePlayerList.size(); i++) 
								{
									if (Balances.isUUID(excludePlayerList.get(i)))
										excludeUUID = Balances.convertToUUID(excludePlayerList.get(i));
									else
										excludeUUID = Balances.getUUIDFromString(excludePlayerList.get(i));
	
									excludePlayer = Balances.getOfflinePlayer(excludeUUID);
									excludePlayerName = excludePlayer.getName();
	
									if ( enabledUUID ) 
									{
										// String spaceStr = StringUtils.repeat("
										// ",Integer.toString(i+1).length()+2);
										sender.sendMessage(Messages.getMessage("excllistinfo1", i + 1, excludePlayerName));
										sender.sendMessage(Messages.getMessage("excllistinfo2", excludeUUID));
									} 
									else 
										sender.sendMessage(Messages.getMessage("excllistinfo1", i + 1, excludePlayerName));
								}

								return true;

							case "add":
								if (args.length == 2) 
								{
									sender.sendMessage(Messages.getMessage("excladdnp"));
									sender.sendMessage(Messages.getMessage("excladdusage", cmdAlias));
									return false;
								} 
								else 
								{
									if (args.length > 3) 
									{
										sender.sendMessage(Messages.getMessage("excladdtp",Arrays.asList(args).subList(3, args.length)));
										sender.sendMessage(Messages.getMessage("excladdusage", cmdAlias));
										return false;
									}
	
									String excludePlayerParam = args[2];
	
									if (Balances.isUUID(excludePlayerParam)) 
									{
										excludeUUID = Balances.convertToUUID(excludePlayerParam);
										if (excludeUUID != null)
										{
											excludePlayer = Balances.getOfflinePlayer(excludeUUID);
											excludePlayerName = excludePlayer.getName();
										}
									} 
									else 
									{
										excludeUUID = Balances.getUUIDFromString(excludePlayerParam);
										if (excludeUUID != null)
										{
											excludePlayer = Balances.getOfflinePlayer(excludeUUID);
											excludePlayerName = excludePlayer.getName();
										}
									}
	
									if (Balances.hasOfflinePlayer(excludePlayerName)) 
									{
										if (enabledUUID)
											excludePlayerList.add(excludeUUID.toString());
										else
											excludePlayerList.add(excludePlayerName);
	
										confUtils.setConf(ConfigUtils.confExcludedPlayers, excludePlayerList);
										saveConfig();
										
										Balances.removeOnlinePlayer(excludePlayerName);
										Balances.removeOfflinePlayer(excludePlayerName);
										// Balances.removeUUID(excludePlayerName);
	
										// reset ALL Holograms to Page 1
										for (String hologramName : balTopHolograms.keySet()) 
										{
											HologramBT hologramBT = balTopHolograms.get(hologramName);
											hologramBT.setPageNo(0);
																	
											updateHologram(hologramBT);
										}
										
										if (enabledUUID) 
										{
											sender.sendMessage(Messages.getMessage("excladdinfo1", excludePlayerName));
											sender.sendMessage(Messages.getMessage("excladdinfo2", excludeUUID));
											sender.sendMessage(Messages.getMessage("excladdinfo3"));
										} 
										else 
										{
											sender.sendMessage(Messages.getMessage("excladdinfo1", excludePlayerName));
											sender.sendMessage(Messages.getMessage("excladdinfo3"));
										}
										return true;
										
									} 
									else 
									{
										if (enabledUUID) 
										{
											sender.sendMessage(Messages.getMessage("excladdnf1", excludePlayerName));
											sender.sendMessage(Messages.getMessage("excladdnf2", excludeUUID));
										} 
										else 
											sender.sendMessage(Messages.getMessage("excladdnf2", excludePlayerName));
	
										return false;
									}
								}

						case "remove":
							if ( args.length == 2 ) 
							{
								sender.sendMessage(Messages.getMessage("exclremnp"));
								sender.sendMessage(Messages.getMessage("exclremusage", cmdAlias));
								return false;
							} 
							else 
							{
								if ( args.length > 3 ) 
								{
									sender.sendMessage(Messages.getMessage("exclremtp", Arrays.asList(args).subList(3, args.length)));
									sender.sendMessage(Messages.getMessage("exclremusage", cmdAlias));
									return false;
								}

								String excludePlayerParam = args[2];
								
								if ( Balances.isUUID(excludePlayerParam) ) 
								{
									excludeUUID = Balances.convertToUUID(excludePlayerParam);
									if (excludeUUID != null)
									{
										excludePlayer = Balances.getOfflinePlayer(excludeUUID);
										excludePlayerName = excludePlayer.getName();
									}
								} 
								else 
								{
									excludeUUID = Balances.getUUIDFromString(excludePlayerParam);
									if (excludeUUID != null)
									{
										excludePlayer = Balances.getOfflinePlayer(excludeUUID);
										excludePlayerName = excludePlayer.getName();
									}
								}

								if ( (excludeUUID != null) && (excludePlayerList.contains(excludePlayerName) || excludePlayerList.contains(excludeUUID.toString())) ) 
								{
									if ( excludePlayerList.contains(excludePlayerName) ) 
										excludePlayerList.remove(excludePlayerName);
									else 
										excludePlayerList.remove(excludeUUID.toString());
									
									confUtils.setConf(ConfigUtils.confExcludedPlayers, excludePlayerList);
									saveConfig();
									
									Balances.addOfflinePlayer(excludePlayer);

									if ( excludePlayer.isOnline() )
										Balances.addOnlinePlayer(excludeUUID);
									
									Balances.sortBalances();

									// reset ALL Holograms to Page 1
									for (String hologramName : balTopHolograms.keySet()) 
									{
										HologramBT hologramBT = balTopHolograms.get(hologramName);
										hologramBT.setPageNo(0);
																
										updateHologram(hologramBT);
									}
									
									if (enabledUUID) 
									{
										sender.sendMessage(Messages.getMessage("exclreminfo1", excludePlayerName));
										sender.sendMessage(Messages.getMessage("exclreminfo2", excludeUUID));
										sender.sendMessage(Messages.getMessage("exclreminfo3"));
									} 
									else 
									{
										sender.sendMessage(Messages.getMessage("exclreminfo1", excludePlayerName));
										sender.sendMessage(Messages.getMessage("exclreminfo3"));
									}
									return true;
									
								} 
								else 
								{
									if (enabledUUID) 
									{
										sender.sendMessage(Messages.getMessage("exclremnf1", excludePlayerName));
										sender.sendMessage(Messages.getMessage("exclremnf2", excludeUUID));
									} 
									else 
										sender.sendMessage(Messages.getMessage("excladdnf1", excludePlayerName));
									
									return false;
								}
							}

						default:
							sender.sendMessage(Messages.getMessage("excltp", Arrays.asList(args).subList(1, args.length)));
							sender.sendMessage(Messages.getMessage("excllistusage", cmdAlias));
							sender.sendMessage(Messages.getMessage("excladdusage", cmdAlias));
							sender.sendMessage(Messages.getMessage("exclremusage", cmdAlias));

							return false;
						}
					}

				default:
					if ( (args.length > 1) && (sender.hasPermission(ConfigUtils.enableCmdsPerm)) ) 
					{
						sender.sendMessage(Messages.getMessage("baltoptp", Arrays.asList(args).subList(1, args.length)));
						sender.sendMessage(Messages.getMessage("usage", cmdAlias));
						sender.sendMessage(Messages.getMessage("reloadusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("topnbalusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holotopnusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("excllistusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("excladdusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("exclremusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("hololistusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holoaddusage2", cmdAlias));
						sender.sendMessage(Messages.getMessage("holoremusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holomoveusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holotpusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("hologramtopnusage", cmdAlias));
						sender.sendMessage(Messages.getMessage("holoboardfileusage", cmdAlias));
						return false;
					}

					if ( (! sender.hasPermission(ConfigUtils.baltopPerm)) && (! sender.hasPermission(ConfigUtils.adminPerm)) && 
					     (! sender.hasPermission(ConfigUtils.oldBaltopPerm)) && (! sender.hasPermission(ConfigUtils.oldAdminPerm)) ) 
					{
						sender.sendMessage(Messages.getMessage("baltopperm", cmdAlias));
						return false;
					}

					if ( ! checkInt(args[0], sender, Messages.getMessage("pagenv", args[0]),Messages.getMessage("pagenv", args[0])) ) 
					{
						sender.sendMessage(Messages.getMessage("usage", cmdAlias));
						return false;
					}

					int tmpPageNo = Integer.parseInt(args[0]);

					if (tmpPageNo < 1) 
					{
						sender.sendMessage(Messages.getMessage("pagenv", tmpPageNo));
						return false;
					}

					this.pageNo = tmpPageNo;
					break;
				}
			} 
			else 
			{
				if ( (! sender.hasPermission(ConfigUtils.baltopPerm)) && (! sender.hasPermission(ConfigUtils.adminPerm)) && 
				     (! sender.hasPermission(ConfigUtils.oldBaltopPerm)) && (! sender.hasPermission(ConfigUtils.oldAdminPerm)) ) 
				{
					sender.sendMessage(Messages.getMessage("baltopperm", cmdAlias));
					return false;
				}
			}

			String tmpPlayerName;
			Player tmpPlayer;

			if (sender instanceof Player) 
			{
				tmpPlayer = (Player) sender;
				tmpPlayerName = tmpPlayer.getName();
			} 
			else 
			{
				tmpPlayerName = "";
				tmpPlayer = null;
			}

			// Extract Player Balances
			if (! Balances.balancesInitialised)
				Balances.getBalances(true);

			String[] playerNameKeys = Balances.getPlayerBalNames();

			if (playerNameKeys.length == 0) 
			{
				sender.sendMessage(Messages.getMessage("nobalsavail", cmdAlias));
				return false;
			}

			List<String> headerLines = confUtils.getConfStringArrList(ConfigUtils.confHeader);
			List<String> detailLines = confUtils.getConfStringArrList(ConfigUtils.confDetail);
			List<String> footerLines = confUtils.getConfStringArrList(ConfigUtils.confFooter);

			int pageSize = confUtils.getConfInt(ConfigUtils.confPageSize);
			totalPages = (int) Math.ceil(playerNameKeys.length / (double) pageSize);

			if (totalPages * pageSize > topnbal)
				totalPages = (int) Math.ceil(topnbal / (double) pageSize);

			if (pageNo > totalPages)
				pageNo = totalPages;

			strUtils.setPageNo(pageNo);
			strUtils.setTotalPages(totalPages);
			strUtils.setTopN(topnbal);
			
			// print BalTop header
			for (int i = 0; i < headerLines.size(); i++) 
			{
				String headerLine = headerLines.get(i);
				sender.sendMessage((String) strUtils.formatLine(headerLine, false).getFmtStr());
			}

			// print BalTop player detail / balance lines
			for (int playerNum = (pageNo - 1) * pageSize; playerNum < (pageNo) * pageSize; playerNum++) 
			{
				if (playerNum >= topnbal || playerNum >= playerNameKeys.length)
					break;

				String playerName = playerNameKeys[playerNum];
				Double playerBal = Balances.getBalance(playerName);

				strUtils.setPlayer(Balances.getOfflinePlayer(playerName));
				strUtils.setPlayerBal(playerBal);
				strUtils.setLineNumber(playerNum + 1);
				
				for (int lineNo = 0; lineNo < detailLines.size(); lineNo++) 
				{
					String playerLine = detailLines.get(lineNo);
					sender.sendMessage((String) strUtils.formatLine(playerLine, false).getFmtStr());
				}
				// Log.info("DEBUG: " + playerNum + ": player=" + playerName +
				// ", UUID=" + Balances.getUUIDFromString(playerName));
			}

			// print Baltop footer lines
			for (int i = 0; i < footerLines.size(); i++) 
			{
				String footerLine = footerLines.get(i);
				sender.sendMessage((String) strUtils.formatLine(footerLine, false).getFmtStr());
			}

			return true;
		}

		return false;
	}
}
