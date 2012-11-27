package me.TfT02.Assassin;

import me.TfT02.Assassin.runnables.AssassinRangeTimer;
import me.TfT02.Assassin.util.PlayerData;
import me.TfT02.Assassin.util.itemNamer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.CommandExecutor;
import org.kitteh.tag.TagAPI;

public class Commands implements CommandExecutor {
	Assassin plugin;

	public Commands(final Assassin instance) {
		plugin = instance;
	}

	private final AssassinMode assassin = new AssassinMode(plugin);
	private final PlayerData data = new PlayerData(plugin);
	private final AssassinRangeTimer range = new AssassinRangeTimer(plugin);

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
						player.sendMessage(ChatColor.RED + "Correct usage /assassin [activate/deactivate/info/refresh]");
						return true;
					case 1:
						if (args[0].equalsIgnoreCase("activate")) {
							int inHandID = player.getItemInHand().getTypeId();
							if (inHandID == 35) {
								if (data.isAssassin(player)) {
									player.sendMessage(ChatColor.RED + "You already are an Assassin.");
								} else {
									ItemStack itemHand = player.getInventory().getItemInHand();
									String item = itemNamer.getName(itemHand);
									String mask = ChatColor.DARK_RED + "Assassin Mask";
									if (item == null || !item.equalsIgnoreCase(mask)){
										player.sendMessage(ChatColor.RED + "Not a mask.");
									}
									else {
										assassin.activateAssassin(player);
									}
								}
							} else {
								player.sendMessage(ChatColor.RED + "You need to have black wool in your hand to use this.");
							}
							return true;
						}
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

							player.sendMessage(ChatColor.YELLOW + "Cooldown done = " + ChatColor.RED + data.isReady(player));
							
							range.checkIfAssassinNear(player);
							return true;
						}
						if (args[0].equalsIgnoreCase("refresh")) {
							TagAPI.refreshPlayer(player);
							return true;
						}
						if (args[0].equalsIgnoreCase("mask")) {
							assassin.spawnMask(player);
							return true;
						}
						if (args[0].equalsIgnoreCase("setstamp")) {
							data.addTimestamp(player);
							player.sendMessage(ChatColor.YELLOW + "Timestamp " + ChatColor.RED + "set");
							return true;
						}
						if (args[0].equalsIgnoreCase("readstamp")) {
							long timestamp = data.getTimestamp(player);
							player.sendMessage(ChatColor.YELLOW + "Timestamp = " + ChatColor.RED + timestamp);
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