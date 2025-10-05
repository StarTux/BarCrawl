package com.cavetale.barcrawl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public final class SessionTag implements Serializable {
    private List<Need> needs = new ArrayList<>();
    private List<String> npcs = new ArrayList<>();
    private int needIndex;
    private boolean started;

    public void roll(final UUID uuid) {
        needs.clear();
        npcs.clear();
        needIndex = 0;
        final List<Need> allNeeds = new ArrayList<>(List.of(Need.values()));
        final List<String> allNpcs = new ArrayList<>(NonPlayerCharacter.IDS);
        Collections.shuffle(allNeeds);
        Collections.shuffle(allNpcs);
        final int score = BarCrawlPlugin.plugin().getSaveTag().getScore(uuid);
        final int max = Math.min(allNpcs.size(), Math.min(allNeeds.size(), 10 + score));
        for (int i = 0; i < max; i += 1) {
            needs.add(allNeeds.get(i));
            npcs.add(allNpcs.get(i));
        }
    }
}
