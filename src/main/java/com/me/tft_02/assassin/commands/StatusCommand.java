package com.me.tft_02.assassin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.me.tft_02.assassin.Bounty;
import com.me.tft_02.assassin.datatypes.player.AssassinPlayer;
import com.me.tft_02.assassin.util.CommandUtils;
import com.me.tft_02.assassin.util.Misc;
import com.me.tft_02.assassin.util.Permissions;
import com.me.tft_02.assassin.util.player.PlayerData;
import com.me.tft_02.assassin.util.player.UserManager;

public class StatusCommand implements CommandExecutor {

    private PlayerData data = new PlayerData();
    private Bounty bounty = new Bounty();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;

        if (!Permissions.status(player)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }
        AssassinPlayer assassinPlayer = UserManager.getPlayer(player);
        String status = assassinPlayer.getStatus();

        player.sendMessage(ChatColor.DARK_GRAY + "==========[ " + ChatColor.YELLOW + "Assassin Info" + ChatColor.DARK_GRAY + " ]===========");
        player.sendMessage(ChatColor.GOLD + "Your status = " + ChatColor.AQUA + status);
        if (assassinPlayer.isAssassin() || assassinPlayer.isHostile()) {
            player.sendMessage(ChatColor.GOLD + "Time left in " + status + " Mode = " + ChatColor.DARK_RED + Misc.getStringTimeLeft(player));
        }
        player.sendMessage(ChatColor.DARK_GRAY + "------------------------------------");
        player.sendMessage(ChatColor.GOLD + "Current bounty = " + ChatColor.DARK_RED + data.getKillCount(player));
        player.sendMessage(ChatColor.GOLD + "Bounty collected = " + ChatColor.GREEN + bounty.getBountyCollectedString(player));
        return true;
    }
}
