package org.desp.babelTower.command;

import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.desp.babelTower.database.RoomRepository;
import org.desp.babelTower.dto.RoomDto;
import org.jetbrains.annotations.NotNull;

public class BabelETCCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings) {

        if (!(commandSender instanceof Player player)) {
            return false;
        }

        if (!player.isOp()) {
            return false;
        }

        Map<Integer, RoomDto> roomMap = RoomRepository.getInstance().roomMap;

        System.out.println("=========================");
        for (Entry<Integer, RoomDto> entity : roomMap.entrySet()) {
            Integer key = entity.getKey();
            boolean playing = entity.getValue().isPlaying();
            player.sendMessage("방 번호 : " + key + " 상태 : " + playing);
        }
        System.out.println("=========================");

        return false;
    }
}
