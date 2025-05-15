package org.desp.babelTower.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class RoomDto {
    private int roomID;
    private boolean isPlaying;
    private String playerLocation;
    private String mobLocation;
}

