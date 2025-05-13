package org.desp.babelTower.utils;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.desp.babelTower.BabelTower;
import org.desp.babelTower.database.PlayerDataRepository;
import org.desp.babelTower.database.RewardLogRepository;
import org.desp.babelTower.dto.PlayerDataDto;
import org.desp.babelTower.dto.RewardLogDto;
import org.desp.babelTower.game.BabelTowerController;

public class BabelTowerManager {

    private static BabelTowerManager instance;
    public static BabelTowerManager getInstance() {
        if (instance == null)
            instance = new BabelTowerManager();
        return instance;
    }

    private final Map<String, BabelTowerSession> sessions = new HashMap<>();

    public void startSession(Player player) {
        PlayerDataDto playerData = PlayerDataRepository.getInstance().getPlayerData(player);

        int challengeFloor = playerData.getClearFloor() + 1;

        String uuid = playerData.getUuid();
        String user_id = playerData.getUser_id();

        if (sessions.containsKey(uuid)) return;

        BabelTowerSession session = new BabelTowerSession(user_id, uuid, challengeFloor, true);
        sessions.put(uuid, session);
        session.getController().start();
//        player.teleport(floorLocation);   각 층 텔포
    }

    public void endSession(Player player, boolean success) {
        String uuid = player.getUniqueId().toString();
        BabelTowerSession session = sessions.remove(uuid);
        if (session == null) return;

        session.getController().stop();

        if (success) {
            int cleared = session.getFloor();
            player.sendMessage("§a[✔] " + cleared + "층 클리어! 보상을 획득했습니다.");
            // 보상 지급 로직
            RewardUtil.sendReward(cleared, RewardUtil.getReward(cleared), player);
            PlayerDataDto playerData = PlayerDataRepository.getInstance().getPlayerData(player);
            playerData.setClearFloor(cleared);

            RewardLogDto rewardLogDto = RewardLogRepository.getInstance().getRewardLogDataCache()
                    .get(playerData.getUuid());
            rewardLogDto.getRewardFloor().add(cleared);

        } else {
            player.sendMessage("§7[로비로 돌아갑니다.]");
        }
        Location location = new Location(Bukkit.getWorld("raid"), -340.637, 6.0000, 551.976);

        Bukkit.getScheduler().runTaskLater(BabelTower.getInstance(), () -> player.teleport(location), 20L);
    }

    public boolean isInSession(Player player) {
        BabelTowerSession session = sessions.get(player.getUniqueId().toString());
        return session != null && session.isActive();
    }

    public BabelTowerSession getSession(Player player) {
        return sessions.get(player.getUniqueId().toString());
    }

    private Location getFloorLocation(int floor) {

        return new Location(Bukkit.getWorld("world"), 100, 100 + floor * 5, 100);
    }

}
