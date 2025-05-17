package org.desp.babelTower.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.desp.babelTower.dto.PlayerDataDto;
import org.jetbrains.annotations.NotNull;

@Getter
public class BabelClearEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final PlayerDataDto playerDto;
    private final int floor;

    private boolean cancelled;

    public BabelClearEvent(Player player, PlayerDataDto playerDto, int floor) {
        this.playerDto = playerDto;
        this.player = player;
        this.floor = floor;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
