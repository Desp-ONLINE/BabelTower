package org.desp.babelTower.listener;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.desp.babelTower.database.FloorDataRepository;
import org.desp.babelTower.utils.BabelTowerManager;
import org.desp.babelTower.utils.BabelTowerSession;

public class FloorListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        BabelTowerManager manager = BabelTowerManager.getInstance();

        if (manager.isInSession(player)) {
            player.sendMessage("§c[!] 사망하여 도전이 실패했습니다.");
            manager.endSession(player, false);
        }
    }

    @EventHandler
    public void onBossDeath(MythicMobDeathEvent event) {
        if (!(event.getKiller() instanceof Player player)) return;

        BabelTowerManager manager = BabelTowerManager.getInstance();

        BabelTowerSession session = manager.getSession(player);
        if (session == null || !session.isActive()) return;

        // 현재 층의 몹인지 확인
        String expectedMobId = FloorDataRepository.getInstance()
                .getFloorData(session.getFloor())
                .getMythicMobID();

        String deadMobId = event.getMob().getType().getInternalName();

        if (!expectedMobId.equalsIgnoreCase(deadMobId)) return;

        // 성공 처리
        player.sendMessage("§a[✔] 보스를 처치했습니다!");
        manager.endSession(player, true);
    }


}
