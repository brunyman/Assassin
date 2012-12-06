package me.TfT02.Assassin;

import me.TfT02.Assassin.util.PlayerData;

import org.bukkit.Bukkit;
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
						player.sendMessage(ChatColor.RED + "-----[]" + ChatColor.GREEN + "Assassin" + ChatColor.RED + "[]-----");
						player.sendMessage(ChatColor.GOLD + "Become an " + ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.GOLD + " and kill other players anonymously:");
						player.sendMessage(ChatColor.GREEN + "[1] Grab an Assassin Mask.");
						player.sendMessage(ChatColor.GREEN + "[2] Right click while holding it, to put it on.");
						player.sendMessage(ChatColor.GREEN + "[3] Your name will be hidden.");
						player.sendMessage(ChatColor.RED + "Type /assassin [help] for more information.");
						return true;
					case 1:
						if (args[0].equalsIgnoreCase("help")) {
							player.sendMessage(ChatColor.RED + "-----[]" + ChatColor.GREEN + "Assassin Help" + ChatColor.RED + "[]-----");
							player.sendMessage(ChatColor.GOLD + "Commands:");
							if (player.hasPermission("assassin.info")) {
								player.sendMessage(ChatColor.GREEN + "/assassin [info]" + ChatColor.GRAY + " Check your status");
							}
							if (player.hasPermission("assassin.spawnmask")) {
								player.sendMessage(ChatColor.GREEN + "/assassin [mask] <amount>" + ChatColor.GRAY + " Spawn Assassin mask");
							}
							if (player.hasPermission("assassin.refresh")) {
								player.sendMessage(ChatColor.GREEN + "/assassin [refresh] <player>" + ChatColor.GRAY + " Reset cooldown time for <player>");
							}
							if (player.hasPermission("assassin.deactivate")) {
								player.sendMessage(ChatColor.GREEN + "/assassin [deactivate] <player>" + ChatColor.GRAY + " Deactivate Assassin mode for <player>");
							}
							return true;
						}
						if (args[0].equalsIgnoreCase("info")) {
							String status = data.getStatus(player);
							player.sendMessage(ChatColor.YELLOW + "Your status = " + ChatColor.RED + status);
							if (data.isAssassin(player)) {
								long activetime = 0;
								if (PlayerData.playerActiveTime.containsKey(player.getName())) activetime = PlayerData.playerActiveTime.get(player.getName());
								player.sendMessage(ChatColor.YELLOW + "Time left in Assassin Mode = " + ChatColor.RED + activetime);
							}
							return true;
						}
						if (args[0].equalsIgnoreCase("chat")) {
							if (data.isAssassin(player)) {
								if (!data.getAssassinChatMode(player)){
									data.enterAssassinChat(player);
									player.sendMessage(ChatColor.GRAY + "Assassin Chat " + ChatColor.GREEN + "ON");
								}
								else{
									data.leaveAssassinChat(player);
									player.sendMessage(ChatColor.GRAY + "Assassin Chat " + ChatColor.RED + "OFF");
								}
							}
							else player.sendMessage(ChatColor.RED + "You must be an Assassin to use this.");
							return true;
						}
					case 2:
						if (args[0].equalsIgnoreCase("mask") && player.hasPermission("assassin.spawnmask")) {
							if(args.length == 2){
								assassin.spawnMask(player, Integer.parseInt(args[1]));
								return true;
							}
							else {
								assassin.spawnMask(player, 1);
								return true;
							}
						}
						if (args[0].equalsIgnoreCase("refresh") && player.hasPermission("assassin.refresh")) {
							if(args.length == 2){
								Player target = Bukkit.getServer().getPlayer(args[1]);
								if (target == null) {
									sender.sendMessage(args[0] + " is not online!");
									return false;
								}
								else {
									TagAPI.refreshPlayer(target);
									data.removeCooldown(target);
									player.sendMessage(ChatColor.RED + "Refreshed cooldowns for " + target);
									return true;
								}
							}
							else {
								TagAPI.refreshPlayer(player);
								data.removeCooldown(player);
								player.sendMessage(ChatColor.RED + "Refreshed cooldowns for " + player.getName());
								return true;
							}
						}
						if (args[0].equalsIgnoreCase("deactivate") && player.hasPermission("assassin.deactivate")) {
							if(args.length == 2){
								Player target = Bukkit.getServer().getPlayer(args[1]);
								if (data.isAssassin(target)) {
									assassin.deactivateAssassin(target);
									data.resetActiveTime(target);
									return true;
								} else {
									player.sendMessage(ChatColor.RED + "Not an Assassin.");
									return true;
								}
							}
							else {
								if (data.isAssassin(player)) {
									assassin.deactivateAssassin(player);
									data.resetActiveTime(player);
									return true;
								} else {
									player.sendMessage(ChatColor.RED + "You aren't an assassin.");
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
}