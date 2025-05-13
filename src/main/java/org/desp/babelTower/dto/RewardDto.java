package org.desp.babelTower.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class RewardDto {
    private String rewardItemID;
    private int rewardQuantity;
}
