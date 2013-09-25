package com.me.tft_02.assassin.util;

import org.bukkit.permissions.Permissible;

public class Permissions {

    public static boolean status(Permissible permissible) {
        return permissible.hasPermission("assassin.commands.status");
    }

    public static boolean chat(Permissible permissible) {
        return permissible.hasPermission("assassin.commands.chat");
    }

    public static boolean reload(Permissible permissible) {
        return permissible.hasPermission("assassin.commands.reload");
    }

    public static boolean mask(Permissible permissible) {
        return permissible.hasPermission("assassin.commands.mask");
    }

    public static boolean refresh(Permissible permissible) {
        return permissible.hasPermission("assassin.commands.refresh");
    }

    public static boolean deactivate(Permissible permissible) {
        return permissible.hasPermission("assassin.commands.deactivate");
    }
}
