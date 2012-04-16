package com.gravitymonkey.minecraft.bukkit.simpleworlds;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleWorlds extends JavaPlugin {

	Logger log;	
	World creative_world;
	World default_world;
	HashMap<String, Location> lastLocation;
	HashMap<String, Integer> lastExp;
	
	public void onEnable(){ 
		log = this.getLogger();
		log.info("SimpleWorlds has been enabled!");		
		lastLocation = new HashMap<String, Location>();
		
		World c = getServer().createWorld(new WorldCreator("creative"));
		creative_world = c;
		
		
	  	List<World> worlds = getServer().getWorlds();
		this.getConfig().set("numberOfWorlds", worlds.size());
		for (int w = 0; w < worlds.size(); w++){
	       String name = ((World)worlds.get(w)).getName();
	       String environment = ((World)worlds.get(w)).getEnvironment().name();
	       long seed = (((World)worlds.get(w)).getSeed());
	       log.info("found " + name + "  " + environment + "  " + seed);
	       if (name.equals("new_world_natural") && environment.equals("NORMAL")){
	    	   default_world = ((World)worlds.get(w));
	       }
	     }
		
//		PluginManager pm = this.getServer().getPluginManager();
//		pm.registerEvent(BlockBreakEvent.class, , EventPriority.NORMAL,null, this, false);
		
	}
	 
	public void onDisable(){ 
		log.info("SimpleWorlds has been disabled!");	 
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
			
			if (!player.hasPermission("SimpleWorlds.goto")){
				return false;
			}
			
			if(cmd.getName().equalsIgnoreCase("goto")){ 
				boolean showDefault = false;
				if (args == null){
					showDefault = true;
				} else {
					if (args.length == 0){
						showDefault = true;
					} else {
						if (args[0].toLowerCase().equals("creative")){
							showDefault = false;
						} else {
							showDefault = true;
						}
					}
				}
				
				PlayerInventory i = player.getInventory();
				ItemStack[] ii = i.getContents();
				int totalItems = 0;
				for (int w = 0; w < ii.length; w++){
					ItemStack is = ii[w];
					if (is != null){
						totalItems = totalItems + is.getAmount();
					}
				}
				if (showDefault){
					if (totalItems > 0){
				      player.sendMessage(ChatColor.DARK_RED + "You can't take items from creative to survival.");
				      player.sendMessage(ChatColor.DARK_RED + "You must empty your inventory, m'kay?");
				      return false;
					} else {					
						Location loc = default_world.getSpawnLocation();
						if (lastLocation.containsKey(player.getName())){
							loc = lastLocation.get(player.getName());
						}
						player.teleport(loc);
						player.setGameMode(GameMode.SURVIVAL);
						player.setExp(0.0f);
						return true;
					}
				} else {
					if (totalItems > 0){
					      player.sendMessage(ChatColor.DARK_RED + "If you take any items to the creative world");
					      player.sendMessage(ChatColor.DARK_RED + "  you can't bring them back.");
					      player.sendMessage(ChatColor.DARK_RED + "Please empty your inventory before you go.");
					      return false;
					} else {
					
						lastLocation.put(player.getName(), player.getLocation());
						
						Location loc = creative_world.getSpawnLocation();
				        player.teleport(loc);
						player.setGameMode(GameMode.CREATIVE);
						return true;
					}
				}
			}

		}
		return false; 
	}	
	
}
