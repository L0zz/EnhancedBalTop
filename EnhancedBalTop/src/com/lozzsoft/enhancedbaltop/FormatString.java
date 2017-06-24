package com.lozzsoft.enhancedbaltop;

public class FormatString 
{
	private String origStr;
	private Object fmtStr;
	private int scrollPos;
	private boolean hasFunc;
	
	public FormatString(String tmpOrigStr)
	{
		origStr = tmpOrigStr;
		fmtStr = tmpOrigStr;
		scrollPos = 0;
	}
	
	public FormatString(String tmpOrigStr,String tmpFmtStr, int tmpScrollPos)
	{
		origStr = tmpOrigStr;
		fmtStr = tmpFmtStr;
		scrollPos = tmpScrollPos;
		hasFunc = false;
	}
	
	public FormatString(String tmpOrigStr,Object tmpFmtStr, int tmpScrollPos, boolean tmpHasFunc)
	{
		origStr = tmpOrigStr;
		fmtStr = tmpFmtStr;
		scrollPos = tmpScrollPos;
		hasFunc = tmpHasFunc;
	}

	public String getOrigStr()
	{
		return origStr;
	}
	
	public void setOrigStr(String tmpOrigStr)
	{
		origStr = tmpOrigStr;
	}	
	
	public Object getFmtStr()
	{
		return fmtStr;
	}
	
	public void setFmtStr(Object tmpFmtStr)
	{
		fmtStr = tmpFmtStr;
	}	
	
	public int getScrollPos()
	{
		return scrollPos;
	}
	
	public void setScrollPos(int tmpScrollPos)
	{
		scrollPos = tmpScrollPos;
	}	
	
	public void setFunc()
	{
		hasFunc = true;
	}
	
	public boolean hasFunc()
	{
		return hasFunc;
	}

	public static FormatString[] copyStrArr(String[] tmpStrArr)
	{
		if (tmpStrArr.length == 0)
			return null;
		
		FormatString[] tmpFmtStrArr = new FormatString[tmpStrArr.length];
		
		for (int i=0; i<tmpStrArr.length; i++)
			tmpFmtStrArr[i] = new FormatString(tmpStrArr[i], null, 0, false);
		
		return tmpFmtStrArr;
	
	}
}
