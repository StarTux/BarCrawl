package com.cavetale.barcrawl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public final class SessionTag implements Serializable {
    private List<String> needs = new ArrayList<>();
    private List<String> npcs = new ArrayList<>();
    private int needIndex;
    private boolean started;

    public void roll(final UUID uuid) {
        needs.clear();
        npcs.clear();
        needIndex = 0;
        final List<String> allNeeds = new ArrayList<>(BarCrawlPlugin.getInstance().getEdition().getNeeds().getAllKeys());
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

    public String getNeedKey(int index) {
        return needs.get(index);
    }

    public String getCurrentNeedKey() {
        return getNeedKey(needIndex);
    }

    public Need getNeed(int index) {
        return BarCrawlPlugin.getInstance().getEdition().getNeeds().getNeed(getNeedKey(index));
    }

    public Need getCurrentNeed() {
        return getNeed(needIndex);
    }
}
