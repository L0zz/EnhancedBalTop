package com.lozzsoft.enhancedbaltop;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

public class HolographicDisplays 
{
	/*
	public interface a
	{
		
	}
	
	public interface b extends a
	{
		
		
	}
	
	
	public interface c extends a
	{
		
		
	}
	
	
	public class d implements a
	{
		public a d(int a)
		{
			if (a == 0)
				return new e();
			
			if (a == 1)
				return new f();
			
			return null;
			
		}
	}
	
	public class e implements b
	{
		
	}
	
	public class f implements c
	{
		
	}
	*/
	
	public class HDHologramLine
	{
		public HDHologramLine HDHologramLine(int lineNo)
		{
			HologramLine holoLine = hologram.getLine(lineNo);
			if ( holoLine instanceof TextLine)
				return new HDTextLine(lineNo);
			
			if ( holoLine instanceof ItemLine)
				return new HDItemLine(lineNo);
			
			return null;
		}
	}
	
	
	public class HDTextLine extends HDHologramLine
	{
		private TextLine textline;
		
		public HDTextLine(int lineNo )
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
		
		public void removeLine()
		{
			textline.removeLine();
		}
	}
	
	
	public class HDItemLine extends HDHologramLine
	{
		private ItemLine itemline;
		
		public HDItemLine(int lineNo )
		{
			itemline = (ItemLine) hologram.getLine(lineNo);
		}
		
		public ItemStack getItem()
		{
			return itemline.getItemStack();
		}
		
		public void setItem(ItemStack item)
		{
			itemline.setItemStack(item);
		}
		
		public void removeLine()
		{
			itemline.removeLine();
		}
	}
	
	
	private Hologram hologram;
	
	
	public HolographicDisplays(Plugin plugin, Location location)
	{
		hologram = HologramsAPI.createHologram(plugin, location);
	}
	
	
	public Hologram getHologram()
	{
		return hologram;
	}
	
	
	public int size()
	{
		return hologram.size();
	}
	
	
	public Location getLocation()
	{
		return hologram.getLocation();
	}
	
	
	public void delete()
	{
		hologram.delete();
	}
	
	
	public void clearLines()
	{
		hologram.clearLines();
	}
	
	
	public void removeLine(int lineNo)
	{
		hologram.removeLine(lineNo);
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
			((ItemLine) holoLine).setItemStack(item);
	}
	
	
	public void appendHologramTextLine(String textLine)
	{	
		hologram.appendTextLine(textLine);
	}
	
	
	public void appendHologramItemLine(ItemStack item)
	{	
		hologram.appendItemLine(item);
	}
	
	
	public HDHologramLine getLine(int lineNo)
	{
		return new HDHologramLine().HDHologramLine(lineNo);
	}
	
	
	public void insertHologramItemLine(int lineNo, ItemStack item)
	{
		hologram.insertItemLine(lineNo, item);
	}
	
	
	public void insertHologramTextLine(int lineNo, String text)
	{
		hologram.insertTextLine(lineNo, text);
	}
	
	
	public void teleport(Location location)
	{
		hologram.teleport(location);
	}
}
