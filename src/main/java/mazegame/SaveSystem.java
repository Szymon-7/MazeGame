package mazegame;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class SaveSystem {
    private static final String SAVE_FILE = "savegame.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void saveGame(Game game) {
        SaveData data = new SaveData(game);
        try {
            mapper.writeValue(new File(SAVE_FILE), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SaveData loadGame() {
        File file = new File(SAVE_FILE);
        if (!file.exists())
            return null;

        try {
            return mapper.readValue(file, SaveData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
