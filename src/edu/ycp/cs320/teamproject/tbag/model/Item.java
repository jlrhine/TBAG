package edu.ycp.cs320.teamproject.tbag.model;

public class Item 
{
	private int itemID; 
	private String name; 
	private int locationID;
	
	public void setItemID(int itemID)
	{
		this.itemID = itemID; 
	}
	
	public int getItemID()
	{
		return itemID; 
	}
	
	public void setName (String name)
	{
		this.name = name; 
	}
	
	public String getName()
	{
		return name; 
	}
	
	public void setLocationID(int locationID)
	{
		this.locationID = locationID; 
	}
	
	public int getLocationID()
	{
		return locationID; 
	}
}
