package org.desp.babelTower;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.desp.babelTower.command.EnterBabelTowerCommand;
import org.desp.babelTower.command.StartBabelTowerCommand;
import org.desp.babelTower.database.FloorDataRepository;
import org.desp.babelTower.database.PlayerDataRepository;
import org.desp.babelTower.listener.PlayerJoinAndQuitListener;

public final class BabelTower extends JavaPlugin {

    @Getter
    private static BabelTower instance;

    @Override
    public void onEnable() {
        instance = this;
        FloorDataRepository.getInstance().loadItemData();

        System.out.println("BabelTower plugin enabled : " + FloorDataRepository.getInstance().floorDataList.size());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinAndQuitListener(), this);
        getCommand("바벨탑").setExecutor(new EnterBabelTowerCommand());
        getCommand("바벨탑입장").setExecutor(new StartBabelTowerCommand());


        Bukkit.getOnlinePlayers().forEach(player ->
            PlayerDataRepository.getInstance().loadPlayerData(player)
        );
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> PlayerDataRepository.getInstance().savePlayerData(player));
    }
}
