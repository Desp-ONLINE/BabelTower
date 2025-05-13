package org.desp.babelTower.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.desp.babelTower.database.PlayerDataRepository;
import org.desp.babelTower.database.RewardLogRepository;

public class PlayerJoinAndQuitListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerDataRepository.getInstance().loadPlayerData(player);
        RewardLogRepository.getInstance().loadRewardLogData(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerDataRepository.getInstance().savePlayerData(player);
        RewardLogRepository.getInstance().saveRewardLog(player);
    }
}