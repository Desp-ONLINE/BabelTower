package org.desp.babelTower.utils;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.desp.babelTower.game.BabelTowerController;

@Getter
@Setter
public class BabelTowerSession {

    private final String user_id;
    private final String uuid;
    private final int floor;
    private boolean active;
    private Entity boss;
    private BukkitTask timerTask;
    private int roomID;

    private BabelTowerController controller;

    public BabelTowerSession(String user_id, String uuid, int floor, boolean active, int roomID) {
        this.user_id = user_id;
        this.uuid = uuid;
        this.floor = floor;
        this.active = active;
        this.roomID = roomID;
        this.controller = new BabelTowerController(Bukkit.getPlayer(UUID.fromString(this.uuid)), floor, roomID);
    }
}