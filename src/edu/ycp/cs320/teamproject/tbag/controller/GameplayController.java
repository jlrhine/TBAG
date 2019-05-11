package edu.ycp.cs320.teamproject.tbag.controller;


import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.teamproject.tbag.db.persist.DatabaseProvider;
import edu.ycp.cs320.teamproject.tbag.db.persist.DerbyDatabase;
import edu.ycp.cs320.teamproject.tbag.db.persist.IDatabase;
import edu.ycp.cs320.teamproject.tbag.model.Gameplay;
import edu.ycp.cs320.teamproject.tbag.model.Item;


public class GameplayController 
{
	private IDatabase db;
	private Gameplay model; 
	private ArrayList<String> output;
	private int userLocation;
	private List<Item> itemsInRoom;
	private List<Item> usersInventory; 
	
	public GameplayController(String username, Boolean newGame) {
		DatabaseProvider.setInstance(new DerbyDatabase());
		db = DatabaseProvider.getInstance();
		db.setUserFilePath(username);
		if (!newGame)
		{
			userLocation = db.getUserLocation();
			itemsInRoom = db.getItemsInLocation(userLocation);
			usersInventory = db.getItemsInLocation(0); 
		}
		else
		{
			db.resetGame();	//OH FUCK	
		}
		
	}
	

	
	public void gameLogic(String input) {
		db.addToCommands(input); 
		model.setInput(input);
		input = input.toLowerCase();
		
		
		// ___________________Movement_______________________
		if(input.contains("move") || input.contains("go")) {
			if(input.contains("north")) {
				moveTo(0);
			}
			else if(input.contains("south")) {
				moveTo(1);
			}
			else if(input.contains("east")) {
				moveTo(2);
			} 
			else if(input.contains("west")){
				moveTo(3);
			}
			else if(input.contains("up")){
				moveTo(4);
			}
			else if(input.contains("down")){
				moveTo(5);
			}
			else {
				System.out.println("Unknown direction");
				db.addToCommands("Unknown direction");
			}
		}
		else {
			System.out
		}
		
		//__________________Picking up items___________________
		
		else if (input.contains("pick up") || input.contains("grab") || input.contains("take")) 
		{
			 
			int itemsPickedUp = 0; 
			for (Item item1 : itemsInRoom)
			{
				String itemName1 = item1.getName(); 
				
				if (input.contains(itemName1))
				{
					db.setItemLocation(itemName1, 0);
					db.addToCommands("You picked up " + itemName1);
					itemsPickedUp++; 
				}
								
			}
			
			if (itemsPickedUp == 0)
			{
				db.addToCommands("Couldn't pickup item"); 
			}
			
		//_________________Drop Item_____________________
		} 
		if(input.contains("drop")) 
		{
			int itemsDropped = 0; 
			for (Item item2 : db.getItemsInLocation(0))
			{
				String itemName2 = item2.getName(); 
				
				if (input.contains(itemName2))
				{
					db.setItemLocation(itemName2, db.getUserLocation());
					db.addToCommands("You dropped " + itemName2 + " in " + "room " + userLocation);
					itemsDropped++; 
				}
			}
			
			if (itemsDropped == 0)
			{
				db.addToCommands("You don't have that item");
			}
			
		} 
		if(input.contains("examine"))
		{
			db.addToCommands(db.getLocationDescriptionLong(userLocation)); 
			
			if (itemsInRoom.size() == 0)
			{
				db.addToCommands("There are no items in the room"); 
			}
			else
			{
				for (Item item : itemsInRoom)
				{
					db.addToCommands("There is a " + item.getName() + " in the room"); 
				}
			}
		} 
		if (input.contains("inventory"))
		{
			List<String> itemNames = new ArrayList<String>(); 
			for (Item item : usersInventory)
			{
				itemNames.add(item.getName()); 
			}
			model.addInventory(itemNames);
		}
		
		// ____________________Agent Encounter ___________________
		for(int i = 1; i < 5; i++) {
			agentEncounter(db.getAgentLocation(i), db.getUserLocation());
		}
		
		output();
	}
	
	public void setModel(Gameplay model)
	{
		this.model = model; 
	}

	
	
	public void output(){
		output = db.getCommands();
		model.setOutput(output);
	}
	
	
	public void moveTo(int direction) {
	
		int nextLocation = -1;
		if(direction == 0) {
			nextLocation = db.getLocationNorth(db.getUserLocation());
		}
		else if(direction == 1) {
			nextLocation = db.getLocationSouth(db.getUserLocation());
		}
		else if(direction == 2) {
			nextLocation = db.getLocationEast(db.getUserLocation());
		}
		else if(direction == 3){
			nextLocation = db.getLocationWest(db.getUserLocation());
		}
		else if(direction == 4){
			nextLocation = db.getLocationUp(db.getUserLocation());
		} 
		else if(direction == 5){
			nextLocation = db.getLocationDown(db.getUserLocation());
		}
		
		if(userLocation == nextLocation) 
		{
			System.out.println("Can't move that way");
			db.addToCommands("Can't move that way");
		}
		//__________________Puzzle_______________________
		else if(puzzle(nextLocation) == false) {
			System.out.println("Room locked, need an item");
			db.addToCommands("Room locked, need an item");
		} else {
			
			db.setUserLocation(nextLocation);
			String roomDescription = null;
			if(db.getPlayerHasBeen(nextLocation) == 0) {
				roomDescription = db.getLocationDescriptionLong(nextLocation);
				db.setPlayerHasBeen(nextLocation);
			}
			else if(db.getPlayerHasBeen(nextLocation) == 1) {
				roomDescription = db.getLocationDescriptionShort(nextLocation);
			}
			db.addToCommands(roomDescription);			
		}
		if(nextLocation == -1) {
			System.out.println("Movement failed, legs gone.");
		}
	}
	
	
	public void agentEncounter(int agent_location, int user_location) {
		if(agent_location == user_location) {
			db.addToCommands(db.getAgentDescription(agent_location));
		}
	}
	
	public boolean puzzle(int nextLocation) {
		// Go through player inventory
		for(Item item : usersInventory) {
			// if playerHas item and NextLocation == puzzleRoom
			String itemName = item.getName();
			if(nextLocation == 11 && itemName.equals("sword")){
				return true;
			}
		}
		
		// IF USER INVENTORY EMPTY
		// Do not let the player enter these rooms
		if(nextLocation == 11) {
			return false;
		}
		return true;
	}
	
}
