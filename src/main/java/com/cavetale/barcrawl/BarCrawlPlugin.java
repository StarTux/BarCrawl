package com.cavetale.barcrawl;

import com.cavetale.fam.trophy.Highscore;
import com.cavetale.mytems.Mytems;
import java.util.List;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.TextColor.color;
import static net.kyori.adventure.text.format.TextDecoration.*;

@Getter
public final class BarCrawlPlugin extends JavaPlugin {
    public static final Component TITLE = textOfChildren(Mytems.FIZZY_BREW, text("Bar Crawl", color(0x00ff00), BOLD));
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
