package com.cavetale.barcrawl;

import java.util.Set;

public interface Needs {
    Set<String> getAllKeys();

    Need getNeed(String key);
}
