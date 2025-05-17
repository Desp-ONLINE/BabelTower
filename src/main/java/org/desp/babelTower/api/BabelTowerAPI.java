package org.desp.babelTower.api;

import org.bukkit.entity.Player;
import org.desp.babelTower.database.PlayerDataRepository;
import org.desp.babelTower.dto.PlayerDataDto;

public class BabelTowerAPI {
    public static PlayerDataDto getPlayerData(Player player) {
        return PlayerDataRepository.getInstance().getPlayerData(player);
    }
}
