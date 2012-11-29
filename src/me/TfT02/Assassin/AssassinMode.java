package me.TfT02.Assassin;

import me.TfT02.Assassin.Listeners.ChatListener;
import me.TfT02.Assassin.util.PlayerData;
import me.TfT02.Assassin.util.itemNamer;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
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
	private final ChatListener chat = new ChatListener(plugin);

	public void activateAssassin(final Player player) {
		//add player to data file
		data.addAssassin(player);
		data.addTimestamp(player);

		//send message to player who activated with how long he is stuck in assassin mode
		Bukkit.getScheduler().scheduleSyncDelayedTask(Assassin.getInstance(), new Runnable() {
			@Override
			public void run() {
				player.sendMessage(ChatColor.DARK_RED + "YOU ARE NOW AN ASSASSIN");
				long time = data.cooldown;
				player.sendMessage(ChatColor.DARK_RED + "Time left: " + time + "seconds");
			}
		}, 20 * 2);

		//send message to near players that an assassin is near + thunder for dramatic effect :)
		//Make a thunder sound
		final Location loc = player.getLocation();
		loc.setY(player.getWorld().getMaxHeight() + 30D);
		player.getWorld().strikeLightningEffect(loc);

		double messageDistance = 250; //TODO Make configurable
		// Message Distance Stuff
		for (Player players : player.getWorld().getPlayers()) {
			if (messageDistance > 0) {
				if (players != player && players.getLocation().distance(player.getLocation()) < messageDistance) {
					players.sendMessage(ChatColor.DARK_RED + "SOMEONE JUST PUT ON HIS MASK!");
					players.sendMessage(ChatColor.YELLOW + "[DEBUG] You have received this message, because you are within range. RANGE= " + messageDistance);
				} else {
//					players.sendMessage(ChatColor.DARK_RED + "ASSASSIN SIGHTED! " + ChatColor.YELLOW + " - debug: messagedistance = 0 -");
				}
			}
		}

		//change name tag + display name
//		player.setDisplayName(ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.RESET);
		changeName(player);
		TagAPI.refreshPlayer(player);
		applyMask(player);
	}

//    private void performAction(CommandSender sender, String[] args) {
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
		chat.overridenNames.remove(playername);
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
		if (itemHead.getTypeId() != 1)
			inventory.setHelmet(new ItemStack(0)); //Remove mask
//		spawnMask(player);//Gives back the mask
		player.updateInventory();   // Needed until replacement available
	}

	public void spawnMask(Player player) {
		PlayerInventory inventory = player.getInventory();
		ItemStack blackWool = new ItemStack(Material.WOOL, 1, (short) 0, (byte) 15);
		ItemStack mask = itemNamer.setName(blackWool, ChatColor.DARK_RED + "Assassin Mask");
		ItemStack mask1 = itemNamer.addLore(mask, ChatColor.GRAY + "Allows PVP");
		int emptySlot = inventory.firstEmpty();
		inventory.setItem(emptySlot, mask1);
		player.updateInventory();
//		Item item = world.dropItemNaturally(loc, myNamedItem);
//		item.setItemStack(myNamedItem);
	}
}
