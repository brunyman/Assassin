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
						player.sendMessage(ChatColor.GOLD + "-----[ " + ChatColor.DARK_RED + "Assassin" + ChatColor.GOLD + " ]-----");
						player.sendMessage(ChatColor.GOLD + "Become an " + ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.GOLD + " and kill other players anonymously:");
						player.sendMessage(ChatColor.GREEN + "[1]" + ChatColor.GRAY + " Grab an Assassin Mask.");
						player.sendMessage(ChatColor.GREEN + "[2]" + ChatColor.GRAY + " Right click while holding it, to put it on.");
						player.sendMessage(ChatColor.GREEN + "[3]" + ChatColor.GRAY + " Your name will be hidden.");
						player.sendMessage(ChatColor.GRAY + "Type /assassin [help] for more information.");
						return true;
					case 1:
						if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("status")) {
							String status = data.getStatus(player);
							player.sendMessage(ChatColor.GOLD + "Your status = " + ChatColor.RED + status);
							if (data.isAssassin(player)) {
								String timeleft = data.getStringTimeLeft(player);
								player.sendMessage(ChatColor.GOLD + "Time left in Assassin Mode = " + ChatColor.DARK_RED + timeleft);
							}
							return true;
						}
						if (args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("c")) {
							if (player.hasPermission("assassin.assassin")) {
								if (data.isAssassin(player)) {
									if (!data.getAssassinChatMode(player)) {
										data.enterAssassinChat(player);
										player.sendMessage(ChatColor.GRAY + "Assassin Chat " + ChatColor.GREEN + "ON");
									} else {
										data.leaveAssassinChat(player);
										player.sendMessage(ChatColor.GRAY + "Assassin Chat " + ChatColor.RED + "OFF");
									}
								} else
									player.sendMessage(ChatColor.RED + "You must be an Assassin to use this.");
							}
							return true;
						}
					case 2:
						if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
							if (args.length == 2) {
								if (Integer.parseInt(args[1]) > 1) {
									getHelpPage(Integer.parseInt(args[1]), player);
									return true;
								} else {
									getHelpPage(1, player);
									return true;
								}
							} else {
								getHelpPage(1, player);
								return true;
							}
						}
						if (args[0].equalsIgnoreCase("mask") && player.hasPermission("assassin.spawnmask")) {
							if (args.length == 2) {
								assassin.spawnMask(player, Integer.parseInt(args[1]));
								return true;
							} else {
								assassin.spawnMask(player, 1);
								return true;
							}
						}
						if (args[0].equalsIgnoreCase("refresh") && player.hasPermission("assassin.refresh")) {
							if (args.length == 2) {
								Player target = Bukkit.getServer().getPlayer(args[1]);
								if (target == null) {
									sender.sendMessage(args[0] + " is not online!");
									return false;
								} else {
									TagAPI.refreshPlayer(target);
									data.removeCooldown(target);
									player.sendMessage(ChatColor.RED + "Refreshed cooldowns for " + target);
									return true;
								}
							} else {
								TagAPI.refreshPlayer(player);
								data.removeCooldown(player);
								player.sendMessage(ChatColor.RED + "Refreshed cooldowns for " + player.getName());
								return true;
							}
						}
						if (args[0].equalsIgnoreCase("deactivate") && player.hasPermission("assassin.deactivate")) {
							if (args.length == 2) {
								Player target = Bukkit.getServer().getPlayer(args[1]);
								if (data.isAssassin(target)) {
									assassin.deactivateAssassin(target);
									data.resetActiveTime(target);
									return true;
								} else {
									player.sendMessage(ChatColor.RED + "Not an Assassin.");
									return true;
								}
							} else {
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

	private void getHelpPage(int page, Player player) {
		int maxPages = 2;
		int nextPage = page + 1;
		if (page > maxPages) {
			player.sendMessage(ChatColor.RED + "This page does not exist." + ChatColor.GOLD + " /help [0-" + maxPages + "]");
		} else {
			String dot = ChatColor.DARK_RED + "* ";
			player.sendMessage(ChatColor.GOLD + "-----[ " + ChatColor.DARK_RED + "Assassin Help" + ChatColor.GOLD + " ]----- Page " + page + "/" + maxPages);
			if (page == 1) {
				player.sendMessage(ChatColor.GOLD + "How does it work?");
				player.sendMessage(dot + ChatColor.GRAY + "When an Assassin, you can PVP other players.");
				player.sendMessage(dot + ChatColor.GRAY + "You're name and skin will be hidden, even in chat.");
				player.sendMessage(dot + ChatColor.GRAY + "You can chat with other Assassins in AssassinChat.");
				player.sendMessage(dot + ChatColor.GRAY + "When the timer expires, you will be teleported back to where you put on your mask.");
				player.sendMessage(dot + ChatColor.GRAY + "Nobody will ever know that you were an Assassin.");
			}
			if (page == 2) {
				player.sendMessage(ChatColor.GOLD + "Commands:");
				if (player.hasPermission("assassin.info")) {
					player.sendMessage(dot + ChatColor.GREEN + "/assassin [info]" + ChatColor.GRAY + " Check your status");
				}
				if (player.hasPermission("assassin.spawnmask")) {
					player.sendMessage(dot + ChatColor.GREEN + "/assassin [mask] <amount>" + ChatColor.GRAY + " Spawn Assassin mask");
				}
				if (player.hasPermission("assassin.refresh")) {
					player.sendMessage(dot + ChatColor.GREEN + "/assassin [refresh] <player>" + ChatColor.GRAY + " Reset cooldown time for <player>");
				}
				if (player.hasPermission("assassin.deactivate")) {
					player.sendMessage(dot + ChatColor.GREEN + "/assassin [deactivate] <player>" + ChatColor.GRAY + " Deactivate Assassin mode for <player>");
				}
			}
			if (nextPage <= maxPages) player.sendMessage(ChatColor.GOLD + "Type /assassin help " + nextPage + " for more");
		}
	}
}