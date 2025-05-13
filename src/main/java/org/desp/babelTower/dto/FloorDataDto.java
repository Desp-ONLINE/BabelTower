package org.desp.babelTower.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class FloorDataDto {
    private int floor;
    private String MythicMobID;
    private List<RewardDto> rewards;
}
