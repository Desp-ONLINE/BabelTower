package org.desp.babelTower.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.desp.babelTower.dto.PlayerDataDto;

public class PlayerDataRepository {

    private static PlayerDataRepository instance;
    private final MongoCollection<Document> playerList;
    private static final Map<String, PlayerDataDto> playerListCache = new HashMap<>();

    public PlayerDataRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.playerList = database.getDatabase().getCollection("PlayerData");
    }

    public static PlayerDataRepository getInstance() {
        if (instance == null) {
            instance = new PlayerDataRepository();
        }
        return instance;
    }

    // 플레이어의 데이터 로드 (캐시에도 저장)
    public void loadPlayerData(Player player) {
        String user_id = player.getName();
        String uuid = player.getUniqueId().toString();

        Document doc = new Document("uuid", uuid);
        if (playerList.find(Filters.eq("uuid", uuid)).first() == null) {
            // 새로운 유저일 경우 DB에 삽입
            Document newUserDocument = new Document()
                    .append("user_id", user_id)
                    .append("uuid", uuid)
                    .append("clearFloor", 0);

            playerList.insertOne(newUserDocument);
        }

        int floor = playerList.find(doc).first().getInteger("clearFloor");

        PlayerDataDto playerDto = PlayerDataDto.builder()
                .user_id(user_id)
                .uuid(uuid)
                .clearFloor(floor)
                .build();
        // 캐시에 저장
        playerListCache.put(uuid, playerDto);
    }

    // 플레이어 데이터 저장 (캐시에서 DB로)
    public void savePlayerData(Player player) {
        String user_id = player.getName();
        String uuid = player.getUniqueId().toString();
        PlayerDataDto playerDataDto = playerListCache.get(uuid);

        Document document = new Document()
                .append("user_id", user_id)
                .append("uuid", uuid)
                .append("clearFloor", playerDataDto.getClearFloor());

        playerList.replaceOne(
                Filters.eq("uuid", uuid),
                document,
                new ReplaceOptions().upsert(true)
        );
    }

    public PlayerDataDto getPlayerData(Player player) {
        return playerListCache.get(player.getUniqueId().toString());
    }

}
