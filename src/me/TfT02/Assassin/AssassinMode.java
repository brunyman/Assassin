package me.TfT02.Assassin;

import me.TfT02.Assassin.Listeners.ChatListener;
import me.TfT02.Assassin.runnables.EndCooldownTimer;
import me.TfT02.Assassin.util.LocationData;
import me.TfT02.Assassin.util.PlayerData;
import me.TfT02.Assassin.util.itemNamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.kitteh.tag.TagAPI;

public class AssassinMode {

	Assassin plugin;

	public AssassinMode(Assassin instance) {
		plugin = instance;
	}

	private PlayerData data = new PlayerData(plugin);
//	private LocationData locationutil = new LocationData(null);
	private ChatListener chat = new ChatListener(plugin);

	public void activateAssassin(final Player player) {
		//add player to data file
		data.addAssassin(player);
		data.addLoginTime(player);
		Location location = player.getLocation();
		PlayerData.playerLocation.put(player.getName(), location);
		//send message to player who activated with how long he is stuck in assassin mode
		Bukkit.getScheduler().scheduleSyncDelayedTask(Assassin.getInstance(), new Runnable() {
			@Override
			public void run() {
				player.sendMessage(ChatColor.DARK_RED + "YOU ARE NOW AN ASSASSIN");
				long time = 60;
				player.sendMessage(ChatColor.DARK_RED + "Time left: " + time + " seconds");
			}
		}, 20 * 2);

		//send message to near players that an assassin is near + thunder for dramatic effect :)
		//Make a thunder sound
		Location loc = player.getLocation();
		loc.setY(player.getWorld().getMaxHeight() + 30D);
		player.getWorld().strikeLightningEffect(loc);

//		double messageDistance = 250;
		double messageDistance = Assassin.getInstance().getConfig().getDouble("Assassin.messages_distance");
		// Message Distance Stuff
		for (Player players : player.getWorld().getPlayers()) {
			if (messageDistance > 0) {
				if (players != player && players.getLocation().distance(player.getLocation()) < messageDistance) {
					players.sendMessage(ChatColor.DARK_RED + "SOMEONE JUST PUT ON HIS MASK!");
					players.sendMessage(ChatColor.YELLOW + "[DEBUG] RANGE = " + messageDistance);
				} else {
				}
			}
		}

		//change name tag + display name
		player.setDisplayName(ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.RESET);
		changeName(player);
		TagAPI.refreshPlayer(player);
		applyMask(player);
		data.addCooldownTimer(player);
	}

	private void changeName(Player player) {
		String playername = player.getName();
		String newName = "[ASSASSIN]";

		chat.overridenNames.put(playername, newName);
	}

	public void deactivateAssassin(Player player) {
		String playername = player.getName();
		data.setNeutral(player);
		player.sendMessage(ChatColor.GRAY + "DEACTIVATED");

		player.setDisplayName(player.getName());
		TagAPI.refreshPlayer(player);
		removeMask(player);
//		chat.overridenNames.remove(playername);

//		Location previousloc = PlayerData.playerLocation.get(playername);
//		player.teleport(previousloc);
	}

	@SuppressWarnings("deprecation")
	public void applyMask(Player player) {
		//put block on head
		PlayerInventory inventory = player.getInventory();
		ItemStack blackWool = new ItemStack(Material.WOOL, 1, (short) 0, (byte) 15);
		ItemStack mask = itemNamer.setName(blackWool, ChatColor.DARK_RED + "Assassin Mask");
		ItemStack mask1 = itemNamer.addLore(mask, ChatColor.GRAY + "Allows PVP");
		//Sets hand item to air, can only activate assassin mode if you are holding a mask
		//This will remove a whole stack of masks...

		//give back helmet if player was wearing one
		ItemStack itemHead = inventory.getHelmet();
		if (itemHead != null)
			inventory.setItemInHand(itemHead);
		else
			inventory.setItemInHand(new ItemStack(Material.AIR));

		inventory.setHelmet(mask1);
		player.updateInventory();   // Needed until replacement available
	}

	@SuppressWarnings("deprecation")
	public void removeMask(Player player) {
		PlayerInventory inventory = player.getInventory();
		ItemStack itemHead = inventory.getHelmet();
		if (itemHead.getTypeId() != 0) inventory.setHelmet(new ItemStack(0));
		//Gives back the mask if config says so
//		if (plugin.getConfig().getBoolean("Assassin.return_mask")) spawnMask(player);

		//If the player was wearing a helmet, put it back on
		int helmetindex = -1;
		if (inventory.contains(Material.DIAMOND_HELMET))
			helmetindex = inventory.first(Material.DIAMOND_HELMET);
		else if (inventory.contains(Material.IRON_HELMET))
			helmetindex = inventory.first(Material.IRON_HELMET);
		else if (inventory.contains(Material.GOLD_HELMET))
			helmetindex = inventory.first(Material.GOLD_HELMET);
		else if (inventory.contains(Material.LEATHER_HELMET))
			helmetindex = inventory.first(Material.LEATHER_HELMET);
		if (helmetindex >= 0) {
			ItemStack helmet = inventory.getItem(helmetindex);
			inventory.setItem(helmetindex, new ItemStack(0));
			inventory.setHelmet(helmet);
		}
		player.updateInventory();   // Needed until replacement available
	}

	@SuppressWarnings("deprecation")
	public void spawnMask(Player player) {
		PlayerInventory inventory = player.getInventory();
		ItemStack blackWool = new ItemStack(Material.WOOL, 1, (short) 0, (byte) 15);
		ItemStack mask = itemNamer.setName(blackWool, ChatColor.DARK_RED + "Assassin Mask");
		ItemStack mask1 = itemNamer.addLore(mask, ChatColor.GRAY + "Allows PVP");
		int emptySlot = inventory.firstEmpty();
		inventory.setItem(emptySlot, mask1);
		player.updateInventory();
	}
}
