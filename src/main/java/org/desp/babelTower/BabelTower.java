package org.desp.babelTower;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.desp.babelTower.command.EnterBabelTowerCommand;
import org.desp.babelTower.command.StartBabelTowerCommand;
import org.desp.babelTower.database.FloorDataRepository;
import org.desp.babelTower.database.PlayerDataRepository;
import org.desp.babelTower.database.RewardLogRepository;
import org.desp.babelTower.game.BabelTowerController.ControllerListener;
import org.desp.babelTower.listener.PlayerJoinAndQuitListener;

public final class BabelTower extends JavaPlugin {

    @Getter
    private static BabelTower instance;

    @Override
    public void onEnable() {
        instance = this;
        FloorDataRepository.getInstance().loadItemData();

        registerListeners(
                new PlayerJoinAndQuitListener(),
                new ControllerListener()
        );

        getCommand("바벨탑").setExecutor(new EnterBabelTowerCommand());
        getCommand("바벨탑입장").setExecutor(new StartBabelTowerCommand());

        Bukkit.getOnlinePlayers().forEach(player -> {
                    PlayerDataRepository.getInstance().loadPlayerData(player);
                    RewardLogRepository.getInstance().loadRewardLogData(player);
                }
        );
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pm = getServer().getPluginManager();
        for (Listener listener : listeners) {
            pm.registerEvents(listener, this);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerDataRepository.getInstance().savePlayerData(player);
            RewardLogRepository.getInstance().saveRewardLog(player);
        });
    }
}
