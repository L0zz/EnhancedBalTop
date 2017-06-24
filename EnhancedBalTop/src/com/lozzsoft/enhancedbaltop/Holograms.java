package com.lozzsoft.enhancedbaltop;

import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.inventory.ItemStack;

import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.line.HologramLine;
import com.sainttx.holograms.api.line.ItemLine;
import com.sainttx.holograms.api.line.TextLine;

public class Holograms 
{
	public class HoloHologramLine
	{
		public HoloHologramLine HoloHologramLine(int lineNo)
		{
			HologramLine holoLine = hologram.getLine(lineNo);
			
			if ( holoLine instanceof TextLine)
				return new HoloTextLine(lineNo);
			
			if ( holoLine instanceof ItemLine)
				return new HoloItemLine(lineNo);
			
			return null;
		}
		
		public void removeLine(int lineNo)
		{
			HologramLine holoLine = hologram.getLine(lineNo);
			hologram.removeLine(holoLine);
		}
	}
	
	
	public class HoloTextLine extends HoloHologramLine
	{
		private TextLine textline;
		
		public HoloTextLine(int lineNo )
		{
			textline = (TextLine) hologram.getLine(lineNo);
		}
		
		public String getText()
		{
			return textline.getText();
		}
	
		public void setText(String text)
		{
			textline.setText(text);
		}
	}
	
	
	public class HoloItemLine extends HoloHologramLine
	{
		private ItemLine itemline;
		
		public HoloItemLine(int lineNo )
		{
			itemline = (ItemLine) hologram.getLine(lineNo);
		}
		
		public ItemStack getItem()
		{
			return itemline.getItem();
		}
		
		public void setItem(ItemStack item)
		{
			itemline.setItem(item);
		}
	}
	
	
	private Hologram hologram;
	
	public Holograms(String id, Location location)
	{
		hologram = new Hologram(id, location);
		EnhancedBalTop.hologramManager.addActiveHologram(hologram);	
	}
	
	
	public Hologram getHologram()
	{
		return hologram;
	}
	
	
	public int size()
	{
		return hologram.getLines().size();
	}

	
	public Location getLocation()
	{
		return hologram.getLocation();
	}
	
	
	public void delete()
	{
		EnhancedBalTop.hologramManager.deleteHologram(hologram);
	}
	
	
	public void removeLine(int lineNo)
	{
		HologramLine holoLine = hologram.getLine(lineNo);
		hologram.removeLine(holoLine);
	}
	

	public void updateHologramTextLine(int lineNo, String textLine)
	{
		HologramLine holoLine = hologram.getLine(lineNo);
		if ( holoLine instanceof TextLine)
			((TextLine) holoLine).setText(textLine);
	}
	
	
	public void updateHologramItemLine(int lineNo, ItemStack item)
	{
		HologramLine holoLine = hologram.getLine(lineNo);
		if ( holoLine instanceof ItemLine)
			((ItemLine) holoLine).setItem(item);
	}
	
	
	public void appendHologramTextLine(String textLine)
	{	
		HologramLine holoLine = new TextLine(hologram, textLine);
		hologram.addLine(holoLine);
	}
	
	
	public void appendHologramItemLine(ItemStack item)
	{	
		HologramLine holoLine2 = new ItemLine(hologram, item);
		hologram.addLine(holoLine2);
	}
	
	
	public void insertHologramItemLine(int lineNo, ItemStack item)
	{
		HologramLine holoLine = new ItemLine(hologram, item);
		hologram.addLine(holoLine, lineNo);
	}
	
	
	public void insertHologramTextLine(int lineNo, String textLine)
	{
		HologramLine holoLine = new TextLine(hologram, textLine);
		hologram.addLine(holoLine, lineNo);
	}

	
	public HoloHologramLine getLine(int lineNo)
	{
		return new HoloHologramLine().HoloHologramLine(lineNo);
	}
	
	
	public void teleport(Location location) 
	{
		hologram.teleport(location);
	}
}
