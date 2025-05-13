package org.desp.babelTower.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.desp.babelTower.dto.FloorDataDto;
import org.desp.babelTower.dto.RewardDto;

public class FloorDataRepository {

    private static FloorDataRepository instance;
    private final MongoCollection<Document> floorDataDB;
    public Map<Integer, FloorDataDto> floorDataList = new HashMap<>();

    public FloorDataRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.floorDataDB = database.getDatabase().getCollection("FloorData");
    }

    public static FloorDataRepository getInstance() {
        if (instance == null) {
            instance = new FloorDataRepository();
        }
        return instance;
    }

    public void loadItemData() {
        FindIterable<Document> documents = floorDataDB.find();
        for (Document document : documents) {
            List<RewardDto> rewardDtoList = new ArrayList<>();
            List<String> rewards = document.getList("rewards", String.class);
            for (String reward : rewards) {
                String[] split = reward.split(":");
                RewardDto rewardDto = RewardDto.builder()
                        .rewardItemID(split[0])
                        .rewardQuantity(Integer.parseInt(split[1]))
                        .build();
                rewardDtoList.add(rewardDto);
            }

            FloorDataDto floorDto = FloorDataDto.builder()
                    .floor(document.getInteger("floor"))
                    .MythicMobID(document.getString("MythicMobID"))
                    .rewards(rewardDtoList)
                    .build();

            floorDataList.put(floorDto.getFloor(), floorDto);
        }
    }

    public FloorDataDto getFloorData(int floor) {
        return floorDataList.get(floor);
    }
}
