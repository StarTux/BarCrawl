package com.cavetale.barcrawl;

import com.cavetale.mytems.Mytems;

public interface Need {
    String getKey();
    String getDisplayName();
    Mytems getMytems();
    String getRequest();
}
