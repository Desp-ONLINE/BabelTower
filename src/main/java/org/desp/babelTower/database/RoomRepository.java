package org.desp.babelTower.database;


import com.mongodb.client.MongoCollection;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bson.Document;
import org.desp.babelTower.dto.RoomDto;

public class RoomRepository {

    private static RoomRepository instance;
    private final MongoCollection<Document> roomDB;
    @Getter
    public Map<Integer, RoomDto> roomMap = new HashMap<>();

    private RoomRepository() {
        DatabaseRegister database = new DatabaseRegister();
        this.roomDB = database.getDatabase().getCollection("RoomData");
    }

    public static RoomRepository getInstance() {
        if (instance == null) instance = new RoomRepository();
        return instance;
    }

    // 서버 시작 시 전체 로딩
    public void loadAllRooms() {
        for (Document doc : roomDB.find()) {
            RoomDto roomDto = RoomDto.builder()
                    .roomID(doc.getInteger("roomID"))
                    .isPlaying(doc.getBoolean("isPlaying", false))
                    .playerLocation(doc.getString("playerLocation"))
                    .build();

            roomMap.put(roomDto.getRoomID(), roomDto);
        }
    }

    public boolean isExistEmptyRoom() {
        for (RoomDto room : roomMap.values()) {
            if (!room.isPlaying()) {
                return true;
            }
        }
        return false;
    }

    public RoomDto getAvailableRoom() {
        for (RoomDto room : roomMap.values()) {
            if (!room.isPlaying()) {
                room.setPlaying(true);
                return room;
            }
        }
        return null;
    }
}