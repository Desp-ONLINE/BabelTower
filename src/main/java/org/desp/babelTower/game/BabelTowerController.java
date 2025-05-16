package org.desp.babelTower.game;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.desp.babelTower.BabelTower;
import org.desp.babelTower.database.FloorDataRepository;
import org.desp.babelTower.database.RoomRepository;
import org.desp.babelTower.dto.FloorDataDto;
import org.desp.babelTower.dto.RoomDto;
import org.desp.babelTower.utils.BabelTowerManager;
import org.desp.babelTower.utils.BabelTowerSession;
import org.desp.babelTower.utils.LocationUtil;

@Getter
public class BabelTowerController {

    private static final Plugin PLUGIN = BabelTower.getInstance();
    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();
    private static final int TIME_LEFT = 30;

    private final Player player;
    private final int floor;
    private Entity boss;
    private final int roomID;

    private int myTime = TIME_LEFT;
    private final List<Integer> tasks = new ArrayList<>();

    public BabelTowerController(Player player, int floor, int roomID) {
        this.player = player;
        this.floor = floor;
        this.roomID = roomID;
    }

    public void start() {
        player.sendMessage("§e[!] " + floor + "층 도전 시작!");

        // 3초 카운트다운
        new BukkitRunnable() {
            int count = 5;

            @Override
            public void run() {
                if (count > 0) {
                    player.sendTitle("§e몬스터 출현까지 " + count + "초...", "");
                    count--;
                } else {
                    this.cancel();
                    try {
                        spawnBoss();
                    } catch (InvalidMobTypeException e) {
                        throw new RuntimeException(e);
                    }
                    startTimeoutTimer(); // 이후 타이머 시작
                }
            }
        }.runTaskTimer(BabelTower.getInstance(), 0L, 20L); // 1초 간격으로 반복
    }


    public void stop() {
        tasks.forEach(SCHEDULER::cancelTask);
        boss.remove();
    }

    private void runTaskLater(Runnable runnable, long delay) {
        int id = SCHEDULER.runTaskLater(PLUGIN, runnable, delay).getTaskId();
        tasks.add(id);
    }

    private void runTaskTimer(Runnable runnable, long delay, long priod) {
        int id = SCHEDULER.runTaskTimer(PLUGIN, runnable, delay, priod).getTaskId();
        tasks.add(id);
    }

    private void spawnBoss() throws InvalidMobTypeException {
        FloorDataDto floorData = FloorDataRepository.getInstance().getFloorData(floor);

        String mobId = floorData.getMythicMobID();

        RoomDto roomDto = RoomRepository.getInstance().getRoomMap().get(roomID);
        String mobLocation = roomDto.getMobLocation();

        // 몹 스폰하는 로직
        boss = MythicBukkit.inst().getAPIHelper().spawnMythicMob(floorData.getMythicMobID(), LocationUtil.parseLocation(mobLocation));

        player.sendTitle("§c" + mobId + " 보스 등장!", " 30초 안에 처치하세요!", 10, 30, 10);
    }

    private void startTimeoutTimer() {
        // 1초마다 ActionBar 출력하는 타이머
        runTaskTimer(() -> {
            if (myTime <= 0) {
                player.sendTitle("§c시간이 초과되었습니다. 실패!", "", 10, 30, 10);
                BabelTowerManager.getInstance().endSession(player, false);
                return;
            }
            player.sendActionBar("§e남은 시간: §c" + myTime + "초");
            myTime--;
        }, 0, 20);
    }

    public static class ControllerListener implements Listener {

        private BabelTowerManager manager() {
            return BabelTowerManager.getInstance();
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent event) {
            Player player = event.getPlayer();

            BabelTowerManager manager = manager();
            if (manager.isInSession(player)) {
                manager.endSession(player, false);
            }
        }

        @EventHandler
        public void onPlayerDeath(PlayerDeathEvent event) {
            Player player = event.getEntity();

            BabelTowerManager manager = manager();
            if (manager.isInSession(player)) {
                manager.endSession(player, false);
            }
        }

        @EventHandler
        public void onBossDeath(MythicMobDeathEvent event) {
            if (!(event.getKiller() instanceof Player player)) {
                return;
            }
            BabelTowerManager manager = manager();
            BabelTowerSession session = manager.getSession(player);
            if (session == null || !session.isActive()) {
                return;
            }

            // 현재 층의 몹인지 확인
            String expectedMobId = FloorDataRepository.getInstance()
                    .getFloorData(session.getFloor())
                    .getMythicMobID();

            String deadMobId = event.getMob().getType().getInternalName();

            if (!expectedMobId.equalsIgnoreCase(deadMobId)) {
                return;
            }

            // 성공 처리
            player.sendTitle("§a 보스를 처치했습니다!", "", 10, 30, 10);

            Bukkit.getScheduler().runTaskLater(BabelTower.getInstance(), () -> manager.endSession(player, true), 30L);
        }
    }
}