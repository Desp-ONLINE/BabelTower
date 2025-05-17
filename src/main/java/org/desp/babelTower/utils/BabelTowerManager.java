package org.desp.babelTower.utils;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.desp.babelTower.BabelTower;
import org.desp.babelTower.database.PlayerDataRepository;
import org.desp.babelTower.database.RewardLogRepository;
import org.desp.babelTower.database.RoomRepository;
import org.desp.babelTower.dto.PlayerDataDto;
import org.desp.babelTower.dto.RewardLogDto;
import org.desp.babelTower.dto.RoomDto;
import org.desp.babelTower.event.BabelClearEvent;

public class BabelTowerManager {

    private static BabelTowerManager instance;
    public static BabelTowerManager getInstance() {
        if (instance == null)
            instance = new BabelTowerManager();
        return instance;
    }

    private static final Location DEFAULT_LOCATION = new Location(
            Bukkit.getWorld("world"),
            -21.521,
            37.0000,
            -737.349
    );

    private final Map<String, BabelTowerSession> sessions = new HashMap<>();

    public void startSession(Player player, RoomDto room) {
        PlayerDataDto playerData = PlayerDataRepository.getInstance().getPlayerData(player);

        String uuid = playerData.getUuid();
        String user_id = playerData.getUser_id();
        if (sessions.containsKey(uuid)) {
            player.sendMessage(ChatColor.RED + "잠시 쉬어가는 것도 중요해요!");
            return;
        }

        int challengeFloor = playerData.getClearFloor() + 1;

        BabelTowerSession session = new BabelTowerSession(user_id, uuid, challengeFloor, true, room.getRoomID());
        sessions.put(uuid, session);
        session.getController().start();

        player.teleport(LocationUtil.parseLocation(room.getPlayerLocation()));
    }

    public void endSession(Player player, boolean success) {
        String uuid = player.getUniqueId().toString();
        BabelTowerSession session = sessions.get(uuid);
        if (session == null) return;

        try {
            session.getController().stop();
            clearRoom(session.getRoomID());
            if (success) {
                int cleared = session.getFloor();
                PlayerDataDto playerData = PlayerDataRepository.getInstance().getPlayerData(player);
                player.sendTitle("§a" + cleared + "층 클리어!", "§7[잠시후 로비로 돌아갑니다.]");
                player.sendMessage("§a 보상은 메일함으로 지급됩니다.");
                // 보상 지급 로직
                RewardUtil.sendReward(cleared, RewardUtil.getReward(cleared), player);
                playerData.setClearFloor(cleared);

                BabelClearEvent clearEvent = new BabelClearEvent(player, playerData, cleared);
                Bukkit.getPluginManager().callEvent(clearEvent);

                RewardLogDto rewardLogDto = RewardLogRepository.getInstance().getRewardLogDataCache()
                        .get(playerData.getUuid());
                rewardLogDto.getRewardFloor().add(cleared);
                Bukkit.getScheduler().runTaskLater(BabelTower.getInstance(), () -> player.teleport(DEFAULT_LOCATION), 40L);
            } else {
                Bukkit.getScheduler().runTaskLater(BabelTower.getInstance(), () -> player.teleport(DEFAULT_LOCATION), 10L);
                player.sendTitle("§c 도전에 실패했습니다.","");
            }
        } catch (Exception e) {
            System.out.println("세션 종료 중 오류: " + e.getMessage());
            e.printStackTrace();
        } finally {
            sessions.remove(uuid);
        }
    }

    public boolean isInSession(Player player) {
        BabelTowerSession session = sessions.get(player.getUniqueId().toString());
        return session != null && session.isActive();
    }

    public BabelTowerSession getSession(Player player) {
        return sessions.get(player.getUniqueId().toString());
    }

    public void clearRoom(int roomID) {
        RoomRepository.getInstance().roomMap.get(roomID).setPlaying(false);
        Bukkit.getPlayer("Dawn__L").sendMessage("[퇴장] 방번호 : " + roomID + " 상태 : §c" +RoomRepository.getInstance().roomMap.get(roomID).isPlaying());
    }
}
