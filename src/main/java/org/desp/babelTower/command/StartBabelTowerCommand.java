package org.desp.babelTower.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.desp.babelTower.utils.BabelTowerManager;
import org.jetbrains.annotations.NotNull;

public class StartBabelTowerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings) {

        if (!(commandSender instanceof Player player)) {
            return false;
        }

        if (!player.isOp()) {
            return false;
        }

        BabelTowerManager.getInstance().startSession(player);
        return false;
    }
}
