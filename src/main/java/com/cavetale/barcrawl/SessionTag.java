package com.cavetale.barcrawl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Data;

@Data
public final class SessionTag implements Serializable {
    private List<Need> needs = new ArrayList<>();
    private List<String> npcs = new ArrayList<>();
    private int needIndex;
    private boolean started;

    public void roll() {
        needs.clear();
        npcs.clear();
        needIndex = 0;
        final List<Need> allNeeds = new ArrayList<>(List.of(Need.values()));
        final List<String> allNpcs = new ArrayList<>(NonPlayerCharacter.IDS);
        Collections.shuffle(allNeeds);
        Collections.shuffle(allNpcs);
        for (int i = 0; i < 10; i += 1) {
            needs.add(allNeeds.get(i));
            npcs.add(allNpcs.get(i));
        }
    }
}
