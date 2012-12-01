package me.TfT02.Assassin;

import me.TfT02.Assassin.util.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

public class Commands implements CommandExecutor {
	Assassin plugin;

	public Commands(Assassin instance) {
		plugin = instance;
	}

	private AssassinMode assassin = new AssassinMode(plugin);
	private PlayerData data = new PlayerData(plugin);
//	private AssassinRangeTimer range = new AssassinRangeTimer(plugin);

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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
						player.sendMessage(ChatColor.RED + "Correct usage /assassin [activate/deactivate/info/refresh]");
						return true;
					case 1:
						if (args[0].equalsIgnoreCase("deactivate")) {
							if (data.isAssassin(player)) {
								assassin.deactivateAssassin(player);
							} else
								player.sendMessage("You aren't an assassin.");
							return true;
						}
						if (args[0].equalsIgnoreCase("info")) {
							String status = data.getStatus(player);
							player.sendMessage(ChatColor.YELLOW + "Your status = " + ChatColor.RED + status);

							player.sendMessage(ChatColor.YELLOW + "Cooldown done = " + ChatColor.RED + data.cooledDown(player));
//							range.checkIfAssassinNear(player);
							return true;
						}
						if (args[0].equalsIgnoreCase("refresh")) {
							TagAPI.refreshPlayer(player);
							data.removeCooldown(player);
							return true;
						}
						if (args[0].equalsIgnoreCase("mask")) {
							assassin.spawnMask(player);
							return true;
						}
						if (args[0].equalsIgnoreCase("activetime")) {
							long activetime = 0;
							if (PlayerData.playerActiveTime.containsKey(player.getName())) activetime = PlayerData.playerActiveTime.get(player.getName());
							player.sendMessage(ChatColor.YELLOW + "Active time " + ChatColor.RED + activetime);
							return true;
						}
						return true;
					}
				}
			}
		}
		return false;
	}
}