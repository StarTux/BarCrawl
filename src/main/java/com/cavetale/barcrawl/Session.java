package com.cavetale.barcrawl;

import com.cavetale.core.util.Json;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Data
public final class Session {
    private static final Map<UUID, Session> SESSIONS = new HashMap<>();
    private final UUID uuid;
    private SessionTag tag;

    public static void reload() {
        SESSIONS.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            load(player.getUniqueId());
        }
    }

    public static void load(UUID uuid) {
        final Session session = new Session(uuid);
        session.load();
        SESSIONS.put(uuid, session);
    }

    public static void unload(UUID uuid) {
        SESSIONS.remove(uuid);
    }

    public static Session get(UUID uuid) {
        return SESSIONS.get(uuid);
    }

    public static File getSaveFolder() {
        return new File(BarCrawlPlugin.plugin().getDataFolder(), "sessions");
    }

    public File getSaveFile() {
        return new File(getSaveFolder(), uuid + ".json");
    }

    public void load() {
        tag = Json.load(getSaveFile(), SessionTag.class, SessionTag::new);
    }

    public void save() {
        Json.save(getSaveFile(), tag, true);
    }
}
