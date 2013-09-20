package com.me.tft_02.assassin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AssassinCommand implements CommandExecutor {

    private CommandExecutor helpCommand = new HelpCommand();
    private CommandExecutor statusCommand = new StatusCommand();
    private CommandExecutor chatCommand = new ChatCommand();
    private CommandExecutor deactivateCommand = new DeactivateCommand();
    private CommandExecutor refreshCommand = new RefreshCommand();
    private CommandExecutor spawnMaskCommand = new SpawnMaskCommand();
    private CommandExecutor reloadCommand = new ReloadCommand();
    private CommandExecutor leaderboardCommand = new LeaderboardCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;

        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (command.getName().equalsIgnoreCase("assassin")) {
            if (player == null) {
                sender.sendMessage("Assassin adds a new way of PVP.");
            }
            else {
                if (player.hasPermission("assassin.assassin")) {
                    switch (args.length) {
                        case 0:
                            return showPluginInfo(player);
                        case 1:
                            if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("status")) {
                                return statusCommand.onCommand(sender, command, label, args);
                            }
                            if (args[0].equalsIgnoreCase("leaderboard") || args[0].equalsIgnoreCase("top")) {
                                return leaderboardCommand.onCommand(sender, command, label, args);
                            }

                            if (args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("c")) {
                                return chatCommand.onCommand(sender, command, label, args);
                            }

                            if (args[0].equalsIgnoreCase("reload")) {
                                return reloadCommand.onCommand(sender, command, label, args);
                            }
                        case 2:
                            if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                                return helpCommand.onCommand(sender, command, label, args);
                            }
                            if (args[0].equalsIgnoreCase("mask")) {
                                return spawnMaskCommand.onCommand(sender, command, label, args);
                            }
                            if (args[0].equalsIgnoreCase("refresh")) {
                                return refreshCommand.onCommand(sender, command, label, args);
                            }
                            if (args[0].equalsIgnoreCase("deactivate")) {
                                return deactivateCommand.onCommand(sender, command, label, args);
                            }
                    }
                }
            }
        }
        return false;
    }

    private boolean showPluginInfo(Player player) {
        player.sendMessage(ChatColor.GOLD + "-----[ " + ChatColor.DARK_RED + "Assassin" + ChatColor.GOLD + " ]-----");
        player.sendMessage(ChatColor.GOLD + "Become an " + ChatColor.DARK_RED + "[ASSASSIN]" + ChatColor.GOLD + " and kill other players anonymously:");
        player.sendMessage(ChatColor.GREEN + "[1]" + ChatColor.GRAY + " Craft an Assassin Mask.");
        player.sendMessage(ChatColor.GREEN + "[2]" + ChatColor.GRAY + " Right click while holding it, to put it on.");
        player.sendMessage(ChatColor.GREEN + "[3]" + ChatColor.GRAY + " Your skin & name will be hidden.");
        player.sendMessage(ChatColor.GREEN + "[4]" + ChatColor.GRAY + " Happy killing! :D");
        player.sendMessage(ChatColor.GRAY + "Type /assassin [help] for more information.");
        return true;
    }
}
