package com.lozzsoft.enhancedbaltop;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.text.WordUtils;

import me.clip.placeholderapi.PlaceholderAPI;

import static com.lozzsoft.enhancedbaltop.EnhancedBalTop.plugin;
import static com.lozzsoft.enhancedbaltop.EnhancedBalTop.console;

@SuppressWarnings("unused")
public class StringUtils 
{
	private HologramBT hologramBT = null;
	private ConfigUtils confUtils = new ConfigUtils();
	private OfflinePlayer player = null;
	
	private int topN=10;
	private int lineNumber=0;	
	
	private int pageNo=1;
	private int totalPages=1;
	private int holoTotalPages=1;
	
	private double playerBal=0;
	
	
	public StringUtils() 
	{
		
	}
	

	public void setHologramBT(HologramBT tmpHologramBT)
	{
		hologramBT = tmpHologramBT;
	}
	
	
	public HologramBT getHologramBT()
	{
		return hologramBT;
	}
	
	
	public void setPlayerBal(double tmpPlayerBal)
	{
		playerBal = tmpPlayerBal;
	}
	
	
	public double getPlayerBal()
	{
		return playerBal;
	}

	
	public void setTotalPages(int tmpTotalPages)
	{
		totalPages = tmpTotalPages;
	}
	
	
	public int getTotalPages()
	{
		return totalPages;
	}
	

	public void setHoloTotalPages(int tmpHoloTotalPages)
	{
		holoTotalPages = tmpHoloTotalPages;
	}
	
	
	public int getHoloTotalPages()
	{
		return holoTotalPages;
	}

	
	public void setPageNo(int tmpPageNo)
	{
		pageNo = tmpPageNo;
	}
	
	
	public int getPageNo()
	{
		return pageNo;
	}
	
	
	public void setTopN(int tmpTopN)
	{
		topN = tmpTopN;
	}
	
	
	public int getTopN()
	{
		return topN;
	}
	
	
	public OfflinePlayer getPlayer()
	{
		return player;
	}
	
	
	public void setPlayer(OfflinePlayer tmpPlayer)
	{
		player = tmpPlayer;
	}
	
	
	public int getLineNumber() 
	{
		return lineNumber;
	}

	
	public void setLineNumber(int lineNo) 
	{
		lineNumber = lineNo;
	}

	
	public String[] strToTitleTime(String str) 
	{
		String[] s = str.split("\\,");

		if (s.length != 3)
			return null;

		for (String tmpNo : s) 
		{
			try 
			{
				Integer.parseInt(tmpNo);
			} 
			catch (Exception npe) 
			{
				return null;
			} 
		}

		return s;
	}

	
	public Location[] str2DblLoc(String str) 
	{
		String locStr[] = str.split("\\,");

		if (locStr.length != 7)
			return null;

		String worldStr = locStr[0];
		Location loc1 = str2loc(worldStr + "," + locStr[1] + "," + locStr[2] + "," + locStr[3]);
		Location loc2 = str2loc(worldStr + "," + locStr[4] + "," + locStr[5] + "," + locStr[6]);

		Location[] retLoc = new Location[2];
		retLoc[0] = loc1;
		retLoc[1] = loc2;

		return retLoc;
	}

	
	public Location str2loc(String str) 
	{
		String str2loc[] = str.split("\\,");

		if (str2loc.length != 4)
			return null;

		World tmpWorld = plugin.getServer().getWorld(str2loc[0]);
		double xVal, yVal, zVal = 0;

		if (tmpWorld != null) 
		{
			try 
			{
				xVal = Double.parseDouble(str2loc[1]);
			}

			catch (Exception e) 
			{
				Log.info(e.getMessage());
				xVal = 0;
			}

			try 
			{
				yVal = Double.parseDouble(str2loc[2]);
			} 
			catch (Exception e) 
			{
				Log.info(e.getMessage());
				yVal = 0;
			}

			try 
			{
				zVal = Double.parseDouble(str2loc[3]);
			} 
			catch (Exception e) 
			{
				Log.info(e.getMessage());
				zVal = 0;
			}

			return new Location(tmpWorld, xVal, yVal, zVal);
		}

		return null;
	}

	
	public String loc2str(Location loc) 
	{
		return loc.getWorld().getName() + "," + String.format("%.2f",loc.getX()) + "," + String.format("%.2f",loc.getY()) + "," + String.format("%.2f",loc.getZ());
	}

	
	private String formatMoney(Double m) 
	{
		String moneyStr = "";
		boolean confUseMoneyDenom = confUtils.getConfBoolean(ConfigUtils.confUseMoneyDenom);
		int decimalPlaces;

		try 
		{
			decimalPlaces = Integer.parseInt(confUtils.getConfStr(ConfigUtils.confDecimalPlaces));
		}
		catch (NumberFormatException e) 
		{
			decimalPlaces = ConfigUtils.defDecimalPlaces;
		}

		if (confUseMoneyDenom) 
		{
			if (m >= 1e+23)
				moneyStr = String.format("%." + decimalPlaces + "f", m / 1e+23) + "SP";
			else if (m >= 1e+21)
				moneyStr = String.format("%." + decimalPlaces + "f", m / 1e+21) + "ST";
			else if (m >= 1e+18)
				moneyStr = String.format("%." + decimalPlaces + "f", m / 1e+18) + "QT";
			else if (m >= 1e+15)
				moneyStr = String.format("%." + decimalPlaces + "f", m / 1e+15) + "Q";
			else if (m >= 1e+12)
				moneyStr = String.format("%." + decimalPlaces + "f", m / 1e+12) + "T";
			else if (m >= 1e+9)
				moneyStr = String.format("%." + decimalPlaces + "f", m / 1e+9) + "B";
			else if (m >= 1e+6)
				moneyStr = String.format("%." + decimalPlaces + "f", m / 1e+6) + "M";
			else if (m >= 1e+3)
				moneyStr = String.format("%." + decimalPlaces + "f", m / 1e+3) + "K";
			else
				moneyStr = String.format("%." + decimalPlaces + "f", m);
		} 
		else
			moneyStr = String.format("%." + decimalPlaces + "f", m);

		return moneyStr;
	}

	
	private String padRight(String s, int n) 
	{
		return String.format("%1$-" + n + "s", s);
	}

	
	private String padLeft(String s, int n) 
	{
		return String.format("%1$" + n + "s", s);
	}

	
	public FormatString formatHoloLine(String tmpLine, FormatString... tmpFmtStr)
	{
		FormatString holoFmtStr = formatLine(tmpLine, true, tmpFmtStr);

		// now replace {Hologram Specific variable}'s
		
		Pattern p = Pattern.compile("\\{icon:(\\w+)[:]*(\\w)*\\}"); 
		Matcher m = p.matcher((String) holoFmtStr.getFmtStr());
		ItemStack tmpItemStack;
		
		while (m.find())
		{
			switch (m.group(1))
			{
				case "playerskull":
				case "playerhead":
					String playerName = "";
					if (m.group(2) != null)
						playerName = m.group(2);
					else if (player != null)
						playerName = player.getName();
					
					tmpItemStack = confUtils.getPlayerHead(playerName, playerName + "'s Skull");
					break;
				
				default:
					String itemStr = m.group(1);
					short dataValue = 0;
					 
					if (m.group(2) != null)
						dataValue = Short.valueOf(m.group(2));
					 
					tmpItemStack = new ItemStack(Material.getMaterial(itemStr), 1, (short) dataValue);
					break;		
			}
			 	 
			holoFmtStr.setFmtStr(tmpItemStack);
		}
		 
		return holoFmtStr;
	}
		
		
		/*
		Pattern p = Pattern.compile("(\\{icon:\\w+\\})");
		Matcher m = p.matcher((String) holoFmtStr.getFmtStr());

		while ( m.find() ) 
		{
			String varStr = m.group().replace("{", "").replace("}", "");

			if (varStr.toLowerCase().startsWith("icon:")) 
			{
				ItemStack tmpItemStack; 
				
				String tmpItemStr = varStr.split("(?i)icon:")[1];
				switch (tmpItemStr.toLowerCase())
				{
					case "playerskull":
					case "playerhead":
						String playerName = "";
						if ( player != null)
							playerName = player.getName();
						
						tmpItemStack = confUtils.getPlayerHead(playerName, playerName + "'s Skull");
						break;
					
					default:
						tmpItemStack = new ItemStack(Material.getMaterial(tmpItemStr));
						break;
				}
				
				holoFmtStr.setFmtStr(tmpItemStack);
				return holoFmtStr;
			}*/

	
	private String getColorCodes(String tmpLine, int startPos) 
	{
		Pattern p = Pattern.compile("(&[0-9a-fA-Fk-oK-OrR])");
		Matcher m = p.matcher(tmpLine);
		String colorCode = "";
		String fmtCode = "";

		while (m.find()) 
		{
			if (m.start() >= startPos)
				break;

			if (m.group().matches("(\\&[0-9a-fA-F])")) 
			{
				colorCode = m.group();
				fmtCode = "";
			} 
			else if (m.group().matches("(\\&[k-oK-OrR])")) 
			{
				fmtCode += m.group();
			}
		}

		return colorCode + fmtCode;
	}

	
	private int getCharPos(String tmpLine, int startPos) 
	{
		// Pattern p = Pattern.compile("(?!\\&[0-9a-fA-Fk-oK-OrR])");
		// Pattern p = Pattern.compile("(?!\\&[0-9a-fA-Fk-oK-OrR]{2}).*");
		Pattern p = Pattern.compile("(&[0-9a-fA-F])+");
		Matcher m = p.matcher(tmpLine);

		while (m.find()) 
		{
			if (m.end() >= startPos) 
			{
				if (m.start() > startPos)
					return startPos;
				else
					return m.end();
			}
		}

		if (tmpLine.length() > 0)
		{
			if (startPos > tmpLine.length())
				return 0;
			else
				return startPos;
		} else
			return -1;
	}

	
	private String scrollText(String inpStr, int numChars) 
	{
		if ((inpStr.length() <= 1) || (numChars == 0))
			return inpStr;

		String inpStrConv = ChatColor.translateAlternateColorCodes('&', inpStr).replaceAll("§§", "&");
		int inpStrLen = ChatColor.stripColor(inpStrConv).length();

		int scrollLen = numChars % inpStrLen;

		if (scrollLen < 0)
			scrollLen = inpStrLen + scrollLen;

		String scrollStr = inpStr;
		String retStr = "";
		String colStr = "";

		for (int i = 1; i <= scrollLen; i++) 
		{
			// pattern matcher for color codes
			Pattern p = Pattern.compile("(&[0-9a-fA-F])+");
			Matcher m = p.matcher(scrollStr);

			if (m.find()) 
			{
				int colorStartPos = m.start();
				int colorEndPos = m.end();

				if (colorStartPos > 0) 
				{
					// colStr = "";
					String leftTextStr = scrollStr.substring(0, colorStartPos);
					String rightTextStr = scrollStr.substring(colorStartPos);

					scrollStr = leftTextStr.substring(1) + rightTextStr + leftTextStr.charAt(0);
				} 
				else 
				{
					colStr = scrollStr.substring(colorStartPos, colorEndPos);
					String textStr = scrollStr.substring(colorEndPos);

					scrollStr = textStr.substring(1) + colStr + textStr.charAt(0);
				}
			}
			else 
				scrollStr = scrollStr.substring(1) + scrollStr.charAt(0);
		}

		retStr = colStr + scrollStr;
		return retStr;
	}

	
	private String[] extractCmdParams(String paramStr) 
	{
		boolean insideQuotes = false;
		int quotePos = -1;
		int commaPos = -1;
		Set<String> params = new HashSet<String>();

		while ((paramStr.length() > 0) && (paramStr != null)) 
		{
			commaPos = paramStr.indexOf(",", commaPos + 1);
			quotePos = paramStr.indexOf("\"", quotePos + 1);

			if (!insideQuotes) 
			{
				if (commaPos == -1) 
				{
					params.add(paramStr);
					break;
				}
				else 
				{
					if ((commaPos < quotePos) || (quotePos == -1)) 
					{
						params.add(paramStr.substring(0, commaPos));
						if ((commaPos + 1) >= paramStr.length())
							break;
						paramStr = paramStr.substring(commaPos + 1);
					} 
					else if (quotePos >= 0)
						insideQuotes = true;
				}
			} 
			else 
			{
				if (quotePos == -1) 
				{
					params.add(paramStr);
					break;
				} 
				else 
				{
					insideQuotes = false;
					if (commaPos == -1) 
					{
						params.add(paramStr);
						break;
					} 
					else 
					{
						params.add(paramStr.substring(0, commaPos));
						if ((commaPos + 1) >= paramStr.length())
							break;
						paramStr = paramStr.substring(commaPos + 1);
					}
				}
			}
		}

		String[] paramArr = params.toArray(new String[0]);
		return paramArr;
	}

	
	private FormatString processStrFunc(FormatString fmtStr) 
	{
		String retRawStr = fmtStr.getOrigStr();
		String retFmtStr = (String) fmtStr.getFmtStr();
		int scrollPos = fmtStr.getScrollPos();

		boolean retHasFunc = false;
		int firstPos, nextPos;
		int lScrollCnt = 0, rScrollCnt = 0, upperCnt = 0, lowerCnt = 0, capitolCnt = 0, subStrCnt = 0;
		int cmd1StartPos = 0;
		int cmd1EndPos = 0;
		String subStrIdx = "";
		String subStrLen = "";

		// Pattern p = Pattern.compile("(\\{\\w+\\})");
		// Pattern p = Pattern.compile("\\{(\\w+)?(?:,(\\w+))?(?:,(\\w+))?\\}");
		// Pattern p = Pattern.compile("\\{([\\w]+)?(?:\\s,)?(?:.)*?\\}");

		// Pattern Match function name, with up-to 2 parameters seperated by
		// commas

		Pattern p = Pattern.compile("\\{([\\w]+)?(?:[\\s]+)?(?:,)?([\\s\\w\\S&&[^}]]+)*?\\}");
		Matcher m = p.matcher(retFmtStr);

		while (m.find()) 
		{
			String funcStr = m.group(1);
			// String cmdStr=retFmtStr.substring(m.start(),m.end());

			switch (funcStr) 
			{
				case "lscroll":
					lScrollCnt++;
	
					if (lScrollCnt == 2) 
					{
						lScrollCnt = 0;
						firstPos = retFmtStr.indexOf("{lscroll}") + 9;
						nextPos = retFmtStr.indexOf("{lscroll}", firstPos);
	
						String scrollStr = retFmtStr.substring(firstPos, nextPos);
	
						scrollPos++;
						retFmtStr = retFmtStr.substring(0, firstPos - 9) + scrollText(scrollStr, scrollPos)
								+ retFmtStr.substring(nextPos + 9);
	
						retHasFunc = true;
					}
					break;
	
				case "rscroll":
					rScrollCnt++;
	
					if (rScrollCnt == 2) 
					{
						rScrollCnt = 0;
						firstPos = retRawStr.indexOf("{rscroll}") + 9;
						nextPos = retFmtStr.indexOf("{rscroll}", firstPos);
	
						String scrollStr = retFmtStr.substring(firstPos, nextPos);
	
						scrollPos = fmtStr.getScrollPos();
						scrollPos--;
						retFmtStr = retFmtStr.substring(0, firstPos - 9) + scrollText(scrollStr, scrollPos)
								+ retFmtStr.substring(nextPos + 9);
						retHasFunc = true;
					}
					break;
	
				case "upper":
					upperCnt++;
					if (upperCnt == 2) 
					{
						upperCnt = 0;
						firstPos = retFmtStr.indexOf("{upper}") + 7;
						nextPos = retFmtStr.indexOf("{upper}", firstPos);
	
						retFmtStr = retFmtStr.substring(0, firstPos - 7)
								+ retFmtStr.substring(firstPos, nextPos).toUpperCase() + retFmtStr.substring(nextPos + 7);
						retHasFunc = true;
					}
					break;
	
				case "lower":
					lowerCnt++;
					if (lowerCnt == 2) 
					{
						lowerCnt = 0;
						firstPos = retFmtStr.indexOf("{lower}") + 7;
						nextPos = retFmtStr.indexOf("{lower}", firstPos);
	
						retFmtStr = retFmtStr.substring(0, firstPos - 7)
								+ retFmtStr.substring(firstPos, nextPos).toLowerCase() + retFmtStr.substring(nextPos + 7);
	
						retHasFunc = true;
					}
					break;
	
				case "capsall":
					capitolCnt++;
					if (capitolCnt == 2) 
					{
						capitolCnt = 0;
						firstPos = retFmtStr.indexOf("{capsall}") + 9;
						nextPos = retFmtStr.indexOf("{capsall}", firstPos);
	
						retFmtStr = retFmtStr.substring(0, firstPos - 9)
								+ WordUtils.capitalizeFully(retFmtStr.substring(firstPos, nextPos))
								+ retFmtStr.substring(nextPos + 9);
	
						retHasFunc = true;
					}
					break;
	
				case "substr":
					subStrCnt++;
					if (subStrCnt == 1) 
					{
						cmd1StartPos = m.start();
						cmd1EndPos = m.end();
	
						if (m.group(2) != null) {
							// String paramStr =
							// cmdStr.substring(cmdStr.indexOf(",") + 1,
							// cmdStr.lastIndexOf("}"));
							String paramStr = m.group(2);
							// Log.info("DEBUG: processStrFun, paramStr = " +
							// paramStr);
	
							String[] params = extractCmdParams(paramStr);
	
							if (params.length > 0) 
							{
								subStrIdx = params[0];
	
								if (params.length > 1)
									subStrLen = params[1];
							}
						}
					}
	
					if (subStrCnt == 2) 
					{
						subStrCnt = 0;
						// Log.info("DEBUG: processStrFunc(substr) subStrIdx = " +
						// subStrIdx);
						// Log.info("DEBUG: processStrFunc(substr) subStrLen = " +
						// subStrLen);
	
						if ( ! subStrIdx.isEmpty() ) 
						{
							int subIdx = Integer.valueOf(subStrIdx);
							int cmd2StartPos = retFmtStr.indexOf(m.group(0), cmd1EndPos);
							int cmd2EndPos = cmd2StartPos + m.group(0).length();
	
							// Log.info("DEBUG: processStrFunc(substr) retFmtStr = "
							// + retFmtStr);
							// Log.info("DEBUG: processStrFunc(substr) cmd1StartPos
							// = " + cmd1StartPos);
							// Log.info("DEBUG: processStrFunc(substr) cmd1EndPos =
							// " + cmd1EndPos);
							// Log.info("DEBUG: processStrFunc(substr) cmd2StartPos
							// = " + cmd2StartPos);
							// Log.info("DEBUG: processStrFunc(substr) cmd2EndPos =
							// " + cmd2EndPos);
	
							String subStr1 = retFmtStr.substring(cmd1EndPos, cmd2StartPos);
							String subStr2 = subStr1;
	
							// Log.info("DEBUG: processStrFunc(substr) subStr1 = " +
							// subStr1);
	
							if ( ! subStrLen.isEmpty() ) 
							{
								int subLen = Integer.valueOf(subStrLen);
								if (subIdx < subStr2.length()) {
									if (subIdx + subLen <= subStr2.length())
										subStr1 = subStr2.substring(0, subIdx + subLen);
								}
							}
	
							if ( subIdx >= subStr2.length() )
								subIdx = 0;
	
							retFmtStr = retFmtStr.substring(0, cmd1StartPos) + subStr1.substring(subIdx)
									+ retFmtStr.substring(cmd2EndPos);
							retHasFunc = true;
							subStrIdx = "";
							subStrLen = "";
							cmd1StartPos = 0;
							cmd1EndPos = 0;
						}
					}
					break;
			}
		}

		return new FormatString(retRawStr, retFmtStr, scrollPos, retHasFunc);
	}

	
	public FormatString formatLine(String tmpLine, boolean isHologram, FormatString... tmpFmtStr) 
	{
		String retRawStr = tmpLine == null ? "" : tmpLine;

		boolean retHasFunc = false;
		int scrollPos = 0;

		if ((tmpFmtStr != null) && (tmpFmtStr.length > 0)) 
		{
			retRawStr = tmpFmtStr[0].getOrigStr();
			scrollPos = tmpFmtStr[0].getScrollPos();
		}

		int decimalPlaces = confUtils.getConfInt(ConfigUtils.confDecimalPlaces);
		
		String retFmtStr = retRawStr;

		// now replace {variable}'s
		Pattern p = Pattern.compile("(\\{\\w+\\})");
		Matcher m = p.matcher(retRawStr);

		while (m.find()) 
		{
			String varStr = m.group().replace("{", "").replace("}", "");
			// log.info("DEBUG: varStr=" + varStr);

			switch (varStr.toLowerCase()) 
			{
			case "servername":
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", plugin.getServer().getServerName());
				break;

			case "topnbal":
				if (isHologram) 
					retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", String.valueOf(hologramBT.getTopN()));
				else 
					retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", String.valueOf(topN));
				break;

			case "date":
				String date = new SimpleDateFormat(confUtils.getConfStr(ConfigUtils.confDateFormat)).format(new Date());
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", date);
				break;

			case "player":
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", player.getName());
				break;

			case "playerpadr":
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", padRight(player.getName(), 16));
				break;

			case "playerbal":
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", String.format("%." + decimalPlaces + "f", playerBal));
				break;

			case "playerbalfmt":
			case "balance":
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", formatMoney(playerBal));
				break;
				
			case "balancefmt":
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", String.format("%,." + decimalPlaces + "f", playerBal));
				break;
				
			case "boardfile":
			case "boardfilename":
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", hologramBT.getBoardName());
				break;
				
			case "excludedays":
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", Integer.toString(EnhancedBalTop.excludeDays));
				break;

			case "lineno":
				retFmtStr = retFmtStr.replaceAll("\\{lineno\\}", padLeft(String.valueOf(lineNumber), String.valueOf(Balances.offlinePlayerSize()).length()));
				break;

			case "pageno":
				if (isHologram) 
					retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", String.valueOf(hologramBT.getPageNo()));
				else 
					retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", Integer.toString(pageNo));
				break;

			case "totalpages":
				if (isHologram) 
					retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", Integer.toString(holoTotalPages));
				else 
					retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", Integer.toString(totalPages));
				break;

			case "totalonlineplayers":
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", Integer.toString(Balances.onlinePlayerSize()));
				break;

			case "totalofflineplayers":
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", Integer.toString(Balances.offlinePlayerSize()));
				break;

			case "maxplayerbalvalue":
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", String.format("%." + decimalPlaces + "f", Balances.getBalance(0)));
				break;

			case "maxplayerbalvaluefmt":
			case "maxbalvalue":
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", formatMoney(Balances.getBalance(0)));
				break;
				
			case "maxplayerbalvaluefmt2":
			case "maxbalvaluefmt":
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", String.format("%,." + decimalPlaces + "f", Balances.getBalance(0)));
				break;
				
			case "minplayerbalvalue":
				int tmpBalPos2 = isHologram ? hologramBT.topN - 1 : topN - 1;

				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}",String.format("%." + decimalPlaces + "f", Balances.getBalance(tmpBalPos2)));
				break;

			case "minplayerbalvaluefmt":
			case "minbalvalue":
				int tmpBalPos3 = isHologram ? hologramBT.topN - 1 : topN - 1;

				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", formatMoney(Balances.getBalance(tmpBalPos3)));
				break;

			case "minplayerbalvaluefmt2":
			case "minbalvaluefmt":
				int tmpBalPos9 = isHologram ? hologramBT.topN - 1 : topN - 1;
				
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", String.format("%,." + decimalPlaces + "f", Balances.getBalance(tmpBalPos9)));
				break;
				
			case "maxbalplayer":
				String[] tmpBalPlayerNames = Balances.getPlayerBalNames();
				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", tmpBalPlayerNames[0]);
				break;

			case "minbalplayer":
				int tmpBalPos4 = isHologram ? hologramBT.topN - 1 : topN - 1;

				String[] tmpBalPlayerNames2 = Balances.getPlayerBalNames();

				if (tmpBalPos4 > tmpBalPlayerNames2.length)
					tmpBalPos4 = tmpBalPlayerNames2.length - 1;

				retFmtStr = retFmtStr.replaceAll("\\{minbalplayer\\}", tmpBalPlayerNames2[tmpBalPos4]);
				break;

			case "uuid":
				retFmtStr = retFmtStr.replaceAll("\\{uuid\\}", Balances.getUUIDFromString(player.getName()).toString());
				break;

			case "serverbalonlinetotal":
				double totalBal = 0.0;
				HashMap<String, Double> tmpBals = Balances.getPlayerBals();

				for (String tmpPlayer : Balances.getOnlinePlayerList())
					totalBal += tmpBals.get(tmpPlayer);

				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", String.format("%." + decimalPlaces + "f", totalBal));
				break;

			case "serverbalonlinetotalfmt":
			case "serveronlinetotal":
				double totalBal2 = 0.0;
				HashMap<String, Double> tmpBals2 = Balances.getPlayerBals();

				for (String tmpPlayer : Balances.getOnlinePlayerList())
					totalBal2 += tmpBals2.get(tmpPlayer);

				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", formatMoney(totalBal2));
				break;

			case "serverbalonlinetotalfmt2":
			case "serveronlinetotalfmt":
				double totalBal9 = 0.0;
				HashMap<String, Double> tmpBals9 = Balances.getPlayerBals();

				for (String tmpPlayer : Balances.getOnlinePlayerList())
					totalBal9 += tmpBals9.get(tmpPlayer);

				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", String.format("%." + decimalPlaces + "f", totalBal9));
				break;
			
			case "serverbaltotal":
				double totalBal3 = 0.0;
				HashMap<String, Double> tmpBals3 = Balances.getPlayerBals();

				for (String tmpPlayer : tmpBals3.keySet())
					totalBal3 += tmpBals3.get(tmpPlayer);

				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", String.format("%." + decimalPlaces + "f", totalBal3));
				break;

			case "serverbaltotalfmt":
			case "servertotal":
				double totalBal4 = 0.0;
				HashMap<String, Double> tmpBals4 = Balances.getPlayerBals();

				for (String tmpPlayer : tmpBals4.keySet())
					totalBal4 += tmpBals4.get(tmpPlayer);

				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", formatMoney(totalBal4));
				break;
				
			case "serverbaltotalfmt2":
			case "servertotalfmt":
				double totalBal5 = 0.0;
				HashMap<String, Double> tmpBals5 = Balances.getPlayerBals();

				for (String tmpPlayer : tmpBals5.keySet())
					totalBal5 += tmpBals5.get(tmpPlayer);

				retFmtStr = retFmtStr.replaceAll("\\{" + varStr.toLowerCase() + "\\}", String.format("%,." + decimalPlaces + "f", totalBal5));
				break;
			
			}
		}
		
		//Process Any Clip's PlaceHolderAPI variables (for online player's only!)
		if ( (player != null) && EnhancedBalTop.hookPlaceHolderAPI && player.isOnline() && retFmtStr.contains("%") )
		{
			Player onlinePlayer = Balances.getOnlinePlayer(player.getName());
			retFmtStr = PlaceholderAPI.setPlaceholders(onlinePlayer, retFmtStr);

			retFmtStr = retFmtStr.replaceAll("%.*%", confUtils.getConfStr(ConfigUtils.confPlaceHolderEmpty));	
		}
		
		// process any special functions e.g. scrolling
		FormatString funcFmtStr = processStrFunc(new FormatString(retRawStr, retFmtStr, scrollPos, retHasFunc));
		if (funcFmtStr.hasFunc())
			retFmtStr = (String) funcFmtStr.getFmtStr();

		// colour code & fix
		retFmtStr = retFmtStr == null ? "" : retFmtStr;
		retFmtStr = ChatColor.translateAlternateColorCodes('&', retFmtStr).replaceAll("§§", "&");

		funcFmtStr.setFmtStr(retFmtStr);

		return funcFmtStr;
	}
}
