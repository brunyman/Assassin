package com.me.tft_02.assassin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length == 2) {
            if (Integer.parseInt(args[1]) > 1) {
                getHelpPage(Integer.parseInt(args[1]), player);
                return true;
            }
            else {
                getHelpPage(1, player);
                return true;
            }
        }
        else {
            getHelpPage(1, player);
            return true;
        }
    }

    private void getHelpPage(int page, Player player) {
        int maxPages = 2;
        int nextPage = page + 1;
        if (page > maxPages) {
            player.sendMessage(ChatColor.RED + "This page does not exist." + ChatColor.GOLD + " /help [0-" + maxPages + "]");
        }
        else {
            String dot = ChatColor.DARK_RED + "* ";
            player.sendMessage(ChatColor.GOLD + "-----[ " + ChatColor.DARK_RED + "Assassin Help" + ChatColor.GOLD + " ]----- Page " + page + "/" + maxPages);
            if (page == 1) {
                player.sendMessage(ChatColor.GOLD + "How does it work?");
                player.sendMessage(dot + ChatColor.GRAY + "When in Assassin mode, you can PVP.");
                player.sendMessage(dot + ChatColor.GRAY + "Your skin and name will be hidden, even in chat.");
                player.sendMessage(dot + ChatColor.GRAY + "You can chat with other Assassins in 'Assassin Chat'.");
                player.sendMessage(dot + ChatColor.GRAY + "When the timer expires, you will be teleported back to where you put on your mask.");
                player.sendMessage(dot + ChatColor.GRAY + "Nobody will ever know that you were an Assassin.");
            }
            if (page == 2) {
                player.sendMessage(ChatColor.GOLD + "AssassinCommand:");
                if (player.hasPermission("assassin.info")) {
                    player.sendMessage(dot + ChatColor.GREEN + "/assassin [info]/[status]" + ChatColor.GRAY + " Check your status");
                }
                if (player.hasPermission("assassin.chat")) {
                    player.sendMessage(dot + ChatColor.GREEN + "/assassin [chat]/[c]" + ChatColor.GRAY + " Toggle Assassin chat mode");
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
            if (nextPage <= maxPages) {
                player.sendMessage(ChatColor.GOLD + "Type /assassin help " + nextPage + " for more");
            }
        }
    }
}
