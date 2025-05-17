package org.desp.babelTower.command;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.desp.babelTower.database.FloorDataRepository;
import org.desp.babelTower.database.PlayerDataRepository;
import org.desp.babelTower.database.RoomRepository;
import org.desp.babelTower.dto.PlayerDataDto;
import org.desp.babelTower.dto.RoomDto;
import org.desp.babelTower.utils.BabelTowerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;

public class BabelTowerCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings) {

        if (!(commandSender instanceof Player player)) {
            return false;
        }

        if(strings.length == 0){
            CommandUtil.runCommandAsOP(player, "워프 이동 바벨탑_입구");
        }

        if (!player.isOp()) {
            return false;
        }

        if (strings.length > 0) {
            String subCommand = strings[0].toLowerCase();

            switch (subCommand) {
                case "입장": {
                    PlayerDataDto playerData = PlayerDataRepository.getInstance().getPlayerData(player);
                    int nextFloor = playerData.getClearFloor() + 1;

                    if (!FloorDataRepository.getInstance().floorDataList.containsKey(nextFloor)) {
                        player.sendMessage("§c 바벨탑을 정복했습니다!!");
                        return false;
                    }

                    if (!RoomRepository.getInstance().isExistEmptyRoom()) {
                        player.sendMessage("§c 참여자가 너무 많습니다..조금뒤에 다시 도전해주세요");
                        return false;
                    }

                    if (FloorDataRepository.getInstance().floorDataList.containsKey(nextFloor)) {
                        if (BabelTowerManager.getInstance().isInSession(player)) {
                            return false;
                        }
                        RoomDto availableRoom = RoomRepository.getInstance().getAvailableRoom();
                        if (availableRoom == null) {
                            return false;
                        }

                        Player debug = Bukkit.getPlayer("Dawn__L");
                        if (debug != null) {
                            debug.sendMessage("[입장] 이름 : " + player.getName() + " 방 번호 : §9" + availableRoom.getRoomID() );
                        }
                        BabelTowerManager.getInstance().startSession(player, availableRoom);
                        return true;
                    }
                }

                case "내정보": {
                    PlayerDataDto playerData = PlayerDataRepository.getInstance().getPlayerData(player);
                    int clearFloor = playerData.getClearFloor();

                    player.sendMessage("§a[바벨탑 정보]");
                    player.sendMessage("§f현재 클리어한 층: §e" + clearFloor + "층");
                    return true;
                }

                default:
                    player.sendMessage("§c알 수 없는 명령어입니다: " + subCommand);
                    return false;
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String s, @NotNull String[] strings) {

        if (strings.length == 1) {
            return List.of("입장", "내정보");
        }

        return List.of();
    }
}
