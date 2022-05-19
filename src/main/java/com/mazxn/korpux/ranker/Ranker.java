package com.mazxn.korpux.ranker;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.mazxn.korpux.persistence.DocumentManager;
import com.mazxn.korpux.persistence.Entry;
import com.mazxn.korpux.persistence.EntryManager;

public class Ranker {
    public static List<String> rank(Hashtable<String, List<Entry>> entries) {
        Hashtable<String, Integer> scores = new Hashtable<>();
        double count = (double)DocumentManager.getKeys("").size();
        for (String word : entries.keySet()) {
            for (Entry entry : entries.get(word)) {
                if (scores.containsKey(entry.URL)) {
                    scores.put(entry.URL, scores.get(entry.URL) + relevance(word, entry, count));
                } else {
                    scores.put(entry.URL, relevance(word, entry, count));
                }
            }
        }

        List<String> ranked = new ArrayList<>();
        for (String url : scores.keySet()) {
            ranked.add(url);
        }
        ranked.sort((a, b) -> scores.get(b) - scores.get(a));
        return ranked;
    }

    public static int relevance(String word, Entry e, Double count) {
        Double result = (double) (10.0 * e.TotalCount + 40.0 * e.AsTitle + 30.0 * e.AsHeader1 + 26.0 * e.AsHeader2 + 22.0 * e.AsHeader3
                + 18.0 * e.AsHeader4 + 14.0 * e.AsHeader5 + 12.0 * e.AsHeader6) * 1000.0;
        result /= (double)DocumentManager.countWords(e.URL);
        result *= (double)EntryManager.getKeys(word).size();
        result /= (double)count;
        return result.intValue();
    }
}
