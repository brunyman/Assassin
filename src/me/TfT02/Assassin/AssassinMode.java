package me.TfT02.Assassin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.kitteh.tag.TagAPI;

public class AssassinMode {

	Assassin plugin;

	public AssassinMode(final Assassin instance) {
		plugin = instance;
	}

	private final PlayerData data = new PlayerData(plugin);

	@SuppressWarnings("deprecation")
	public void activateAssassin(Player player) {
		//add player to data file
		data.addAssassin(player);

		//send message to player who activated with how long he is stuck in assassin mode
		player.sendMessage(ChatColor.DARK_RED + "YOU ARE NOW AN ASSASSIN");

		double messageDistance = 25; //TODO Make configurable
		// Message Distance Stuff
		for (Player players : player.getWorld().getPlayers()) {
			if (messageDistance > 0) {
				if (players != player || players.getLocation().distance(player.getLocation()) < messageDistance) {
					players.sendMessage(ChatColor.DARK_RED + "ASSASSIN SIGHTED!" + ChatColor.YELLOW + " - debug: you are within range -");
				} else {
//					players.sendMessage(ChatColor.DARK_RED + "ASSASSIN SIGHTED! " + ChatColor.YELLOW + " - debug: messagedistance = 0 -");
				}
			}
		}

		int time = 120;
		player.sendMessage(ChatColor.DARK_RED + "Time left: " + time + "mins");

		//change name tag + display name
		player.setDisplayName(ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.WHITE);
		TagAPI.refreshPlayer(player);

		//put block on head
		PlayerInventory inventory = player.getInventory();
		ItemStack blackWool = new ItemStack(Material.WOOL, 1, (short) 0, (byte) 15);
		inventory.removeItem(new ItemStack[] { blackWool });

		ItemStack itemHead = inventory.getHelmet();
		if (itemHead.getTypeId() != 1)
			inventory.setItemInHand(itemHead); //give back helmet if player was wearing one
		inventory.setHelmet(blackWool);
		player.updateInventory();   // Needed until replacement available
		//send message to near players that an assassin is near + thunder for dramatic effect :)
	}

	@SuppressWarnings("deprecation")
	public void deactivateAssassin(Player player) {
		data.setNeutral(player);
		player.sendMessage(ChatColor.GRAY + "DEACTIVATED");
		player.setDisplayName(player.getName());
		TagAPI.refreshPlayer(player);
		PlayerInventory inventory = player.getInventory();
		ItemStack itemHead = inventory.getHelmet();
		if (itemHead.getTypeId() != 1) inventory.setHelmet(new ItemStack(0)); //Remove mask
		player.updateInventory();   // Needed until replacement available
	}
}
