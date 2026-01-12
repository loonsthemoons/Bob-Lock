package dev.loons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BobLockConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(),
            "bob-lock.json");

    public boolean enabled = true;
    public int depthPercent = 100; // 100% = -0.08f
    public int smoothness = 30; // 30 = 0.03f
    public int delayMs = 90;

    private static BobLockConfig INSTANCE = new BobLockConfig();

    public static BobLockConfig getInstance() {
        return INSTANCE;
    }

    public void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                INSTANCE = GSON.fromJson(reader, BobLockConfig.class);
                if (INSTANCE == null)
                    INSTANCE = new BobLockConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Intuitive Mapping Helpers
    public float getInternalOffset() {
        // 100% -> -0.08f. 0% -> 0.0f
        return (depthPercent / 100.0f) * -0.08f;
    }

    public float getInternalLerp() {
        // 30 -> 0.03f. 100 -> 0.1f. 1 -> 0.001f
        return smoothness / 1000.0f;
    }
}
