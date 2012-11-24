package me.TfT02.Assassin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
					player.sendMessage(ChatColor.RED + "-----[]" + ChatColor.GREEN + "Assassin" + ChatColor.RED + "[]-----");
					player.sendMessage(ChatColor.GOLD + "This is how the plugin works!");
				}
			}
			return true;
		}
		return false;
	}
}
