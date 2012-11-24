package me.TfT02.Assassin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import org.bukkit.command.CommandExecutor;
import org.kitteh.tag.TagAPI;

public class Commands implements CommandExecutor {
	Assassin plugin;

	public Commands(final Assassin instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		Player player = null;

		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (cmd.getName().equalsIgnoreCase("assassin")) {
			if (player == null) {
				sender.sendMessage("Assassin adds a new way of PVP.");
			} else {
				if (player.hasPermission("assassin.assassin")) {
					switch (args.length) {
					case 0:
						player.sendMessage(ChatColor.RED + "Correct usage /assassin activate");
						return true;
					case 1:
						if (args[0].equalsIgnoreCase("activate")) {
							int inHandID = player.getItemInHand().getTypeId();
							if (inHandID != 35) {
								player.sendMessage(ChatColor.RED + "You need to have black wool in your hand to use this.");
							}
							if (inHandID == 35) {
								player.sendMessage(ChatColor.DARK_RED + "YOU ARE NOW AN ASSASSIN");
								player.setDisplayName(ChatColor.DARK_RED + "[ASSASSIN]");
								TagAPI.refreshPlayer(player);
								PlayerInventory inventory = player.getInventory();
								ItemStack blackWool = new ItemStack(Material.WOOL, 5, (short) 0, (byte) 15);
								inventory.removeItem(new ItemStack[] { blackWool });
								inventory.setHelmet(blackWool);
							}
						}
						if (args[0].equalsIgnoreCase("deactivate")) {
								player.sendMessage(ChatColor.GRAY + "DEACTIVATED");
								player.setDisplayName(player.getName());
								TagAPI.refreshPlayer(player);
								PlayerInventory inventory = player.getInventory();
								ItemStack itemHead = inventory.getHelmet();
								if (itemHead.getTypeId() != 0) {
									inventory.setItemInHand(itemHead);
								}
						}
						return true;
					}
				}
			}
		}
		return false;
	}
}