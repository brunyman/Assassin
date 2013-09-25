package com.me.tft_02.assassin.commands;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.util.CommandUtils;
import com.me.tft_02.assassin.util.Misc;
import com.me.tft_02.assassin.util.player.PlayerData;

public class LeaderboardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("assassin.commands.leaderboard")) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        player.sendMessage(ChatColor.DARK_GRAY + "==========[ " + ChatColor.YELLOW + "Bounty Leaderboard" + ChatColor.DARK_GRAY + " ]===========");
        List<Map.Entry<String, Integer>> entries = Misc.entriesSortedByValues(PlayerData.bountyCollected);

        for (Map.Entry<String, Integer> entry : entries) {
            player.sendMessage(entry + "" + entry.getKey());
        }
        return true;
    }
}
