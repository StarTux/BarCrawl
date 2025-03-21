package com.cavetale.barcrawl;

import com.cavetale.core.util.Json;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
public final class SaveTag implements Serializable {
    private boolean enabled;
    private Map<UUID, Integer> scores = new HashMap<>();

    public void addScore(UUID uuid, int value) {
        final int score = scores.getOrDefault(uuid, 0);
        scores.put(uuid, score + value);
    }

    public static File getSaveFile() {
        return new File(BarCrawlPlugin.plugin().getDataFolder(), "save.yml");
    }

    public static SaveTag load() {
        return Json.load(getSaveFile(), SaveTag.class, SaveTag::new);
    }

    public void save() {
        Json.save(getSaveFile(), this, true);
    }

    public int getScore(UUID uuid) {
        return scores.getOrDefault(uuid, 0);
    }

    public int getTotalScore() {
        int result = 0;
        for (Integer value : scores.values()) {
            result += value;
        }
        return result;
    }
}
