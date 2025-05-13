package org.desp.babelTower.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitTask;

@Getter @Setter
public class BabelTowerSession {
    private final String user_id;
    private final String uuid;
    private final int floor;
    private boolean active;
    private BukkitTask timerTask;

    public BabelTowerSession(String user_id, String uuid, int floor, boolean active) {
        this.user_id = user_id;
        this.uuid = uuid;
        this.floor = floor;
        this.active = active;
    }

    public void cancelTimer() {
        if (timerTask != null && !timerTask.isCancelled()) {
            timerTask.cancel();
        }
    }

    public void end() {
        this.active = false;
        cancelTimer(); // 세션 종료 시 타이머도 정리
    }
}

