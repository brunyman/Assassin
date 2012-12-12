package me.TfT02.Assassin;

import me.TfT02.Assassin.util.NamedItemStack;
import me.TfT02.Assassin.util.PlayerData;

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

	/**
	 * Applies all the Assassin traits,
	 * such as a different display name, nametag and helmet item.
	 * 
	 * @param player Player whom will be given the traits.
	 */
	public void applyTraits(final Player player) {
		data.addLoginTime(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Assassin.getInstance(), new Runnable() {
			@Override
			public void run() {
				player.sendMessage(ChatColor.DARK_RED + "YOU ARE NOW AN ASSASSIN");
				String timeleft = data.getStringTimeLeft(player);
				player.sendMessage(ChatColor.GOLD + "Time left in Assassin Mode = " + ChatColor.DARK_RED + timeleft);
			}
		}, 20 * 1);

		player.setDisplayName(ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.RESET);
		TagAPI.refreshPlayer(player);
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
		data.addLocation(player, location);
		Location loc = player.getLocation();
		loc.setY(player.getWorld().getMaxHeight() + 30D);
		player.getWorld().strikeLightningEffect(loc);
		if (Assassin.getInstance().getConfig().getBoolean("Assassin.warn_others_on_activation")) {
			double messageDistance = Assassin.getInstance().getConfig().getDouble("Assassin.messages_distance");
			for (Player players : player.getWorld().getPlayers()) {
				if (messageDistance > 0) {
					if (players != player && players.getLocation().distance(player.getLocation()) < messageDistance) {
						players.sendMessage(ChatColor.DARK_RED + "SOMEONE JUST PUT ON HIS MASK!");
					} else {
					}
				}
			}
		}
		applyMask(player);
		data.addCooldownTimer(player);
	}

	/**
	 * Deactivate Assassin mode.
	 * 
	 * @param player Player who's mode will be changed.
	 */
	public void deactivateAssassin(Player player) {
		String playername = player.getName();
		data.leaveAssassinChat(player);
		data.setNeutral(player);
		player.sendMessage(ChatColor.GRAY + "ASSASSIN MODE DEACTIVATED");

		player.setDisplayName(playername);
		TagAPI.refreshPlayer(player);
		removeMask(player);
		Location previousLocation = data.getLocation(player);
		player.teleport(previousLocation);
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
		ItemStack assassinMask = new NamedItemStack(new ItemStack(Material.WOOL, 1, (short) 0, (byte) 15)).setName(ChatColor.DARK_RED + "Assassin Mask").setLore(ChatColor.GRAY + "Allows PVP").getItemStack();

		//Sets hand item to air, can only activate assassin mode if you are holding a mask
		//TODO FIX: This will remove a whole stack of masks...

		//give back helmet if player was wearing one
		ItemStack itemHead = inventory.getHelmet();

		int emptySlot = inventory.firstEmpty();
		if (itemHead != null) {
			inventory.setItem(emptySlot, itemHead);
			inventory.setItemInHand(new ItemStack(Material.AIR));
		} else
			inventory.setItemInHand(new ItemStack(Material.AIR));

		inventory.setHelmet(assassinMask);
		player.updateInventory();
	}

	/**
	 * Applies a mask on the players head with force.
	 * 
	 * @param player Player who will get a mask.
	 */
	@SuppressWarnings("deprecation")
	public void applyMaskForce(Player player) {
		PlayerInventory inventory = player.getInventory();
		ItemStack assassinMask = new NamedItemStack(new ItemStack(Material.WOOL, 1, (short) 0, (byte) 15)).setName(ChatColor.DARK_RED + "Assassin Mask").setLore(ChatColor.GRAY + "Allows PVP").getItemStack();

		inventory.setHelmet(assassinMask);
		player.updateInventory();
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
		if (Assassin.getInstance().getConfig().getBoolean("Assassin.return_mask")) spawnMask(player, 1);

		//If the player was wearing a helmet, put it back on
		int helmetindex = -1;
		if (inventory.contains(Material.DIAMOND_HELMET))
			helmetindex = inventory.first(Material.DIAMOND_HELMET);
		else if (inventory.contains(Material.IRON_HELMET))
			helmetindex = inventory.first(Material.IRON_HELMET);
		else if (inventory.contains(Material.GOLD_HELMET))
			helmetindex = inventory.first(Material.GOLD_HELMET);
		else if (inventory.contains(Material.LEATHER_HELMET)) helmetindex = inventory.first(Material.LEATHER_HELMET);
		if (helmetindex >= 0) {
			ItemStack helmet = inventory.getItem(helmetindex);
			inventory.setItem(helmetindex, new ItemStack(0));
			inventory.setHelmet(helmet);
		}
		player.updateInventory();
	}

	/**
	 * Spawns a mask in inventory.
	 * 
	 * @param player Player who will receive a mask.
	 */
	@SuppressWarnings("deprecation")
	public void spawnMask(Player player, int amount) {
		PlayerInventory inventory = player.getInventory();
		ItemStack assassinMask = new NamedItemStack(new ItemStack(Material.WOOL, amount, (short) 0, (byte) 15)).setName(ChatColor.DARK_RED + "Assassin Mask").setLore(ChatColor.GRAY + "Allows PVP", "Hold in your hand and right-click", "to activate assassin mode.").getItemStack();

		int emptySlot = inventory.firstEmpty();
		inventory.setItem(emptySlot, assassinMask);
		player.updateInventory();
	}
}
