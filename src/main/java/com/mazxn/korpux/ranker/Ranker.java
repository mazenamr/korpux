package com.mazxn.korpux.ranker;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.mazxn.korpux.persistence.Entry;

public class Ranker {
    public static List<String> rank(Hashtable<String, List<Entry>> entries) {
        List<String> result = new ArrayList<>();
        for (String key : entries.keySet()) {
            result.add(key);
        }
        return result;
    }
}
