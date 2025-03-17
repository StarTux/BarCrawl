package com.cavetale.barcrawl;

import com.cavetale.fam.trophy.Highscore;
import java.util.List;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class BarCrawlPlugin extends JavaPlugin {
    protected static BarCrawlPlugin instance;
    protected final BarCrawlCommand barcrawlCommand = new BarCrawlCommand(this);
    protected final EventListener eventListener = new EventListener();
    private SaveTag saveTag;
    private List<Component> highscore;

    public BarCrawlPlugin() {
        instance = this;
    }

    @Override
    public void onEnable() {
        barcrawlCommand.enable();
        eventListener.enable();
        getDataFolder().mkdirs();
        Session.getSaveFolder().mkdirs();
        Session.reload();
        reload();
    }

    @Override
    public void onDisable() {
    }

    public static BarCrawlPlugin plugin() {
        return instance;
    }

    public void computeHighscore() {
        highscore = Highscore.sidebar(Highscore.of(saveTag.getScores()));
    }

    public void reload() {
        saveTag = SaveTag.load();
        computeHighscore();
    }
}
