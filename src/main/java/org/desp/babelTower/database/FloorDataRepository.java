package org.desp.babelTower.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

//    public void loadItemData() {
//        FindIterable<Document> documents = floorDataDB.find();
//        for (Document document : documents) {
//            List<RewardDto> rewardDtoList = new ArrayList<>();
//            List<String> rewards = document.getList("rewards", String.class);
//            for (String reward : rewards) {
//                String[] split = reward.split(":");
//                RewardDto rewardDto = RewardDto.builder()
//                        .rewardItemID(split[0])
//                        .rewardQuantity(Integer.parseInt(split[1]))
//                        .build();
//                rewardDtoList.add(rewardDto);
//            }
//
//            FloorDataDto floorDto = FloorDataDto.builder()
//                    .floor(document.getInteger("floor"))
//                    .MythicMobID(document.getString("MythicMobID"))
//                    .rewards(rewardDtoList)
//                    .build();
//
//            System.out.println("floorDto.getMythicMobID() = " + floorDto.getMythicMobID());
//            System.out.println("floorDto.getFloor() = " + floorDto.getFloor());
//
//            floorDataList.put(floorDto.getFloor(), floorDto);
//        }
//    }

    public void loadItemData() {
        System.out.println("[BabelTower] 데이터 로딩 시작...");
        FindIterable<Document> documents = floorDataDB.find();

        if (documents == null) {
            System.out.println("[BabelTower] documents는 null입니다!");
            return;
        }

        int documentCount = 0;
        for (Document document : documents) {
            documentCount++;
            System.out.println("[BabelTower] 로드된 문서: " + document.toJson());

            List<RewardDto> rewardDtoList = new ArrayList<>();
            List<String> rewards = document.getList("rewards", String.class);
            if (rewards == null) {
                System.out.println("[BabelTower] rewards 필드가 없거나 비어있습니다.");
                continue;
            }

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

            System.out.println("floorDto.getMythicMobID() = " + floorDto.getMythicMobID());
            System.out.println("floorDto.getFloor() = " + floorDto.getFloor());

            floorDataList.put(floorDto.getFloor(), floorDto);
        }

        System.out.println("[BabelTower] 데이터 로딩 완료. 로드된 문서 수: " + documentCount);
    }


    public FloorDataDto getFloorData(int floor) {
        for (Entry<Integer, FloorDataDto> integerFloorDataDtoEntry : floorDataList.entrySet()) {
            System.out.println("integerFloorDataDtoEntry.getKey() = " + integerFloorDataDtoEntry.getKey());
            System.out.println("integerFloorDataDtoEntry.getValue().getMythicMobID() = " + integerFloorDataDtoEntry.getValue().getMythicMobID());
        }
        return floorDataList.get(floor);
    }
}
