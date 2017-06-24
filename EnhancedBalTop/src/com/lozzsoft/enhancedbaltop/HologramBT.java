package com.lozzsoft.enhancedbaltop;

import java.util.ArrayList;

import org.bukkit.Location;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

public class HologramBT 
{
	Object hologram;
	ArrayList<Object> hologramLines = new ArrayList<Object>();
	int updateInterval = 10;
	int updateTaskId = 0;
	int pageNo = 1;
	int topN = 10;
	String boardName = "default.yml";
	
	
	
	public HologramBT(HologramBT tmpHologramBT)
	{
		hologram = tmpHologramBT.hologram;
		hologramLines = tmpHologramBT.hologramLines;
		updateInterval = tmpHologramBT.updateInterval;
		updateTaskId = tmpHologramBT.updateTaskId;
		pageNo = tmpHologramBT.pageNo;
		topN = tmpHologramBT.topN;
		boardName = tmpHologramBT.boardName;
	}
	
	
	public HologramBT(Object tmpHologram, int tmpUpdInt, String tmpBoardName)
	{
		hologram = tmpHologram;
		updateInterval = tmpUpdInt;
		boardName = tmpBoardName;
	}
	

	public void setHologramLine(int tmpLineNo, Object tmpObj)
	{
		hologramLines.set(tmpLineNo, tmpObj);
	}

	
	public Object getHologramLine(int tmpLineNo)
	{
		return hologramLines.get(tmpLineNo);
	}
	

	public void clearHologramLines()
	{
		hologramLines.clear();
	}
	
	
	public void addHologramLines(Object tmpHologramLine)
	{
		hologramLines.add(tmpHologramLine);
	}
	
	
	public void setHologramLines(ArrayList<Object> tmpHologramLines)
	{
		hologramLines = tmpHologramLines;
	}
	
	
	public ArrayList<Object> getHologramLines()
	{
		return hologramLines;
	}
	
	
	public void setTopN(int tmpTopN)
	{
		topN = tmpTopN;
	}
	
	
	public int getTopN()
	{
		return topN;
	}
	
	
	public void setHologram(Object tmpHologram)
	{
		hologram = tmpHologram;
	}
	
	
	public Object getHologram()
	{
		return hologram;
	}
	
	
	public void setLocation(Location tmpLoc)
	{
		if (hologram instanceof Holograms)
			((Holograms) hologram).teleport(tmpLoc);
		else if (hologram instanceof HolographicDisplays)
			((HolographicDisplays) hologram).teleport(tmpLoc);
	}
	
	
	public Location getLocation()
	{
		if (hologram instanceof Holograms)
			return ((Holograms) hologram).getLocation();
		else if (hologram instanceof HolographicDisplays)
			return ((HolographicDisplays) hologram).getLocation();
		
		return null;
	}
	
	
	public void setUpdateInterval(int tmpUpdInt)
	{
		updateInterval = tmpUpdInt;
	}
	
	
	public int getUpdateInterval()
	{
		return updateInterval;
	}
	
	
	public void setUpdateTaskId(int tmpUpdTaskId)
	{
		updateTaskId = tmpUpdTaskId;
	}
	
	
	public int getUpdateTaskId()
	{
		return updateTaskId;
	}
	
	
	public void setBoardName(String tmpBoardName)
	{
		boardName = tmpBoardName;
	}
	
	
	public String getBoardName()
	{
		return boardName;
	}
	
	
	public void setPageNo(int tmpPageNo)
	{
		pageNo = tmpPageNo;
	}
	
	
	public int getPageNo()
	{
		return pageNo;
	}
}
