package org.desp.babelTower.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.desp.babelTower.database.FloorDataRepository;
import org.desp.babelTower.database.PlayerDataRepository;
import org.desp.babelTower.dto.PlayerDataDto;
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

        PlayerDataDto playerData = PlayerDataRepository.getInstance().getPlayerData(player);
        if (!FloorDataRepository.getInstance().floorDataList.containsKey(playerData.getClearFloor())) {
            player.sendMessage("§c 더이상 오를 층이 없습니다");
            return false;
        }

        BabelTowerManager.getInstance().startSession(player);
        return true;
    }
}
