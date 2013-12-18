package com.me.tft_02.assassin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.me.tft_02.assassin.Assassin;
import com.me.tft_02.assassin.locale.LocaleLoader;
import com.me.tft_02.assassin.util.Permissions;

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
        if (!Permissions.assassin(sender)) {
            return false;
        }

        switch (args.length) {
            case 0:
                return printUsage(sender);
            case 1:
                if (args[0].equalsIgnoreCase("status") || args[0].equalsIgnoreCase("status")) {
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
            default:
                return false;
        }
    }

    private boolean printUsage(CommandSender sender) {
        sender.sendMessage(LocaleLoader.getString("General.Plugin_Header", Assassin.p.getDescription().getName(), Assassin.p.getDescription().getAuthors()));
        sender.sendMessage(LocaleLoader.getString("General.About.1"));
        sender.sendMessage(LocaleLoader.getString("General.About.2"));
        sender.sendMessage(LocaleLoader.getString("General.Running_Version", Assassin.p.getDescription().getVersion()));
        sender.sendMessage(LocaleLoader.getString("General.Use_Help"));
        return true;
    }
}
