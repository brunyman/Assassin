package me.TfT02.Assassin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;
import org.kitteh.tag.TagAPI;

public class Commands implements CommandExecutor {
	Assassin plugin;

	public Commands(final Assassin instance) {
		plugin = instance;
	}

	private final AssassinMode assassin = new AssassinMode(plugin);
	private final PlayerData data = new PlayerData(plugin);

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
							if (data.isAssassin(player)){
								player.sendMessage(ChatColor.RED + "You already are an Assassin.");
							}
							if (inHandID == 35 || !data.isAssassin(player)) {
								assassin.activateAssassin(player);
							}
						}
						if (args[0].equalsIgnoreCase("deactivate")) {
							if (data.isAssassin(player)){
								assassin.deactivateAssassin(player);
							}
						}
						if (args[0].equalsIgnoreCase("info")) {
							String playername = player.getName();
							boolean checkStatus1 = data.isAssassin(player);
							player.sendMessage(ChatColor.YELLOW + "Is " + ChatColor.RED + playername + ChatColor.YELLOW + " an Assassin? " + ChatColor.RED + checkStatus1);

							boolean checkStatus2 = data.isNeutral(player);
							player.sendMessage(ChatColor.YELLOW + "Is " + ChatColor.RED + playername + ChatColor.YELLOW + " Neutral? " + ChatColor.RED + checkStatus2);

							String status = data.getStatus(player);
							player.sendMessage(ChatColor.YELLOW + "Status = " + ChatColor.RED + status);
						}
						if (args[0].equalsIgnoreCase("refresh")) {
							TagAPI.refreshPlayer(player);
						}
						return true;
					}
				}
			}
		}
		return false;
	}
}