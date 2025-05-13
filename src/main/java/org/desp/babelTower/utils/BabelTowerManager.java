package org.desp.babelTower.utils;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.desp.babelTower.BabelTower;
import org.desp.babelTower.database.FloorDataRepository;
import org.desp.babelTower.database.PlayerDataRepository;
import org.desp.babelTower.dto.FloorDataDto;
import org.desp.babelTower.dto.PlayerDataDto;

public class BabelTowerManager {

    private static BabelTowerManager instance;

    private final Map<String, BabelTowerSession> sessions = new HashMap<>();

    public static BabelTowerManager getInstance() {
        if (instance == null)
            instance = new BabelTowerManager();
        return instance;
    }

    public void startSession(Player player) {
        PlayerDataDto playerData = PlayerDataRepository.getInstance().getPlayerData(player);

        int challengeFloor = playerData.getClearFloor() + 1;

        String uuid = playerData.getUuid();
        String user_id = playerData.getUser_id();

        if (sessions.containsKey(uuid)) return;

        BabelTowerSession session = new BabelTowerSession(user_id, uuid, challengeFloor, true);
        sessions.put(uuid, session);
//        player.teleport(floorLocation);   각 층 텔포

        player.sendMessage("§e[!] " + challengeFloor + "층 도전 시작!");

        // 3초 후 보스 소환
        Bukkit.getScheduler().runTaskLater(BabelTower.getInstance(), () -> {
            if (session.isActive()) {
                spawnBoss(player, challengeFloor);
                startTimeoutTimer(player, session);
            }
        }, 60L);

    }

    private void spawnBoss(Player player, int floor) {
        FloorDataDto floorData = FloorDataRepository.getInstance().getFloorData(floor);
        System.out.println("floorData.getFloor() = " + floorData.getFloor());
        System.out.println("floorData.getMythicMobID() = " + floorData.getMythicMobID());
        System.out.println("floorData.getRewards().size() = " + floorData.getRewards().size());

        String mobId = floorData.getMythicMobID();
        Location spawnLocation = player.getLocation().add(0, 0, 3);
        // 몹 스폰하는 로직
        player.sendMessage("§c[!] " + mobId + " 보스 등장! 30초 안에 처치하세요!");
    }

    private void startTimeoutTimer(Player player, BabelTowerSession session) {
        // 1초마다 ActionBar 출력하는 타이머
        BukkitTask task = new BukkitRunnable() {
            int timeLeft = 30;

            @Override
            public void run() {
                if (!session.isActive()) {
                    cancel();
                    return;
                }

                if (timeLeft <= 0) {
                    player.sendMessage("§c[!] 시간이 초과되었습니다. 실패!");
                    endSession(player, false);
                    cancel();
                    return;
                }

                player.sendActionBar("§e남은 시간: §c" + timeLeft + "초");
                timeLeft--;
            }
        }.runTaskTimer(BabelTower.getInstance(), 0L, 20L);

        session.setTimerTask(task);

        // 백업 타이머: 혹시라도 위 타이머가 실패할 경우 대비
        Bukkit.getScheduler().runTaskLater(BabelTower.getInstance(), () -> {
            if (session.isActive()) {
                player.sendMessage("§c[!] 시간이 초과되었습니다. 실패!");
                endSession(player, false);  // 세션 종료
            }
        }, 600L);
    }


    public void endSession(Player player, boolean success) {
        String uuid = player.getUniqueId().toString();
        BabelTowerSession session = sessions.remove(uuid);
        if (session == null) return;

        session.end();
        Location location = new Location(Bukkit.getWorld("world"), 46.527, 105.000, -737.370);
        player.teleport(location);

//        if (success) {
//            int cleared = session.getFloor();
//            clearedFloors.put(uuid, cleared);
//            player.sendMessage("§a[✔] " + cleared + "층 클리어! 보상을 획득했습니다.");
//            giveReward(player, cleared);
//        } else {
//            player.sendMessage("§7[로비로 돌아갑니다.]");
//        }
    }

    public boolean isInSession(Player player) {
        BabelTowerSession session = sessions.get(player.getUniqueId().toString());
        return session != null && session.isActive();
    }

    public BabelTowerSession getSession(Player player) {
        return sessions.get(player.getUniqueId().toString());
    }

    private Location getFloorLocation(int floor) {
        // 예시: 층수마다 저장된 좌표 사용
        // 추후 config 연동 추천
        return new Location(Bukkit.getWorld("world"), 100, 100 + floor * 5, 100);
    }
}
