package me.TfT02.Assassin;

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
//	private ChatListener chat = new ChatListener(plugin);

	/**
	 * Applies all the Assassin traits,
	 * such as a different display name, nametag and helmet item.
	 * 
	 * @param player Player whom will be given the traits.
	 */
	public void applyTraits(final Player player){
		data.addLoginTime(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Assassin.getInstance(), new Runnable() {
			@Override
			public void run() {
				player.sendMessage(ChatColor.DARK_RED + "YOU ARE NOW AN ASSASSIN");
				long activetime = data.getActiveTime(player);
				player.sendMessage(ChatColor.DARK_RED + "Time left: " + activetime + " seconds");
			}
		}, 20 * 1);

		player.setDisplayName(ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.RESET);
//		changeName(player); Doesnt work properly
		TagAPI.refreshPlayer(player);
		applyMask(player);
	}

	/**
	 * Activate Assassin mode.
	 * 
	 * @param player Player who's mode will be changed.
	 */
	public void activateAssassin(Player player) {
		data.addAssassin(player);
		applyTraits(player);
		Location location = player.getLocation();
		PlayerData.playerLocation.put(player.getName(), location);
		Location loc = player.getLocation();
		loc.setY(player.getWorld().getMaxHeight() + 30D);
		player.getWorld().strikeLightningEffect(loc);
//		double messageDistance = 250;
		double messageDistance = Assassin.getInstance().getConfig().getDouble("Assassin.messages_distance");
		for (Player players : player.getWorld().getPlayers()) {
			if (messageDistance > 0) {
				if (players != player && players.getLocation().distance(player.getLocation()) < messageDistance) {
					players.sendMessage(ChatColor.DARK_RED + "SOMEONE JUST PUT ON HIS MASK!");
					players.sendMessage(ChatColor.YELLOW + "[DEBUG] RANGE = " + messageDistance);
				} else {
				}
			}
		}
		data.addCooldownTimer(player);
	}
//
//	private void changeName(Player player) {
//		String playername = player.getName();
//		String newName = "[ASSASSIN]";
//
//		chat.overridenNames.put(playername, newName);
//	}

	/**
	 * Deactivate Assassin mode.
	 * 
	 * @param player Player who's mode will be changed.
	 */
	public void deactivateAssassin(Player player) {
		String playername = player.getName();
		data.setNeutral(player);
		player.sendMessage(ChatColor.GRAY + "DEACTIVATED");

		player.setDisplayName(playername);
		TagAPI.refreshPlayer(player);
		removeMask(player);
//		chat.overridenNames.remove(playername);

//		Location previousloc = PlayerData.playerLocation.get(playername);
//		player.teleport(previousloc);
	}

	/**
	 * Applies a mask on the players head.
	 * Also gives back the helmet the player was wearing, if any.
	 * 
	 * @param player Player who will get a mask.
	 */
	@SuppressWarnings("deprecation")
	public void applyMask(Player player) {
		PlayerInventory inventory = player.getInventory();
		ItemStack blackWool = new ItemStack(Material.WOOL, 1, (short) 0, (byte) 15);
		ItemStack mask = itemNamer.setName(blackWool, ChatColor.DARK_RED + "Assassin Mask");
		ItemStack mask1 = itemNamer.addLore(mask, ChatColor.GRAY + "Allows PVP");

		//Sets hand item to air, can only activate assassin mode if you are holding a mask
		//This will remove a whole stack of masks...

		//give back helmet if player was wearing one
		ItemStack itemHead = inventory.getHelmet();
		int maskindex = inventory.first(mask1);

		int emptySlot = inventory.firstEmpty();
		if (itemHead != null)
			inventory.setItem(emptySlot, itemHead);
		else
			inventory.setItem(maskindex, new ItemStack(Material.AIR));
//			inventory.setItemInHand(new ItemStack(Material.AIR));

		inventory.setHelmet(mask1);
		player.updateInventory();   // Needed until replacement available
	}

	/**
	 * Removes a mask on the players head.
	 * Also puts back the helmet on the player, if any.
	 * 
	 * @param player Player who will lose a mask.
	 */
	@SuppressWarnings("deprecation")
	public void removeMask(Player player) {
		PlayerInventory inventory = player.getInventory();
		ItemStack itemHead = inventory.getHelmet();
		if (itemHead.getTypeId() != 0) inventory.setHelmet(new ItemStack(0));
		//Gives back the mask if config says so
		if (Assassin.getInstance().getConfig().getBoolean("Assassin.return_mask")) spawnMask(player);

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

	/**
	 * Spawns a mask in inventory.
	 * 
	 * @param player Player who will receive a mask.
	 */
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
