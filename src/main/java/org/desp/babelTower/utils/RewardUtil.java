package org.desp.babelTower.utils;

import com.binggre.mmomail.MMOMail;
import com.binggre.mmomail.objects.Mail;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.manager.TypeManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.desp.babelTower.database.FloorDataRepository;
import org.desp.babelTower.dto.FloorDataDto;
import org.desp.babelTower.dto.RewardDto;

public class RewardUtil {

    public static void sendReward(int floor, List<ItemStack> reward, Player player) {
        MMOMail mmoMail = MMOMail.getInstance();
        Mail rewardMail = mmoMail.getMailAPI().createMail(
                "시스템",
                "바벨탑, §a"+floor+"§f층 클리어 보상입니다.",
                0,
                reward
        );

        mmoMail.getMailAPI().sendMail(player.getName(), rewardMail);
    }

    public static List<ItemStack> getReward(int floor) {

        FloorDataDto floorData = FloorDataRepository.getInstance().getFloorData(floor);
        List<RewardDto> rewards = floorData.getRewards();

        List<ItemStack> rewardItems = new ArrayList<>();

        for (RewardDto reward : rewards) {
            String rewardItemID = reward.getRewardItemID();
            Integer rewardQuantity = reward.getRewardQuantity();

            ItemStack rewardItem = getValidTypeItem(rewardItemID);
            rewardItem.setAmount(rewardQuantity);
            rewardItems.add(rewardItem);
        }
        return rewardItems;
    }

    public static ItemStack getValidTypeItem(String itemID) {
        ItemStack rewardItem = null;
        TypeManager types = MMOItems.plugin.getTypes();
        for (Type type : types.getAll()) {
            if(MMOItems.plugin.getItem(type, itemID)==null){
                continue;
            }
            rewardItem = MMOItems.plugin.getItem(type, itemID);
        }

        return rewardItem;
    }
}
