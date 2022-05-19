import java.util.LinkedHashMap;
import java.util.List;

import com.mazxn.korpux.persistence.DocumentManager;
import com.mazxn.korpux.persistence.Entry;
import com.mazxn.korpux.persistence.EntryManager;

import org.junit.Test;

public class TestEntryDB {
    @Test
    public void testEntryDB() {
        List<String> keys = EntryManager.getKeys("");
        LinkedHashMap<String, Entry> words = new LinkedHashMap<>();
        for (String key : keys) {
            String word = key.split("-")[0];
            if (!words.containsKey(word)) {
                words.put(word, new Entry("0"));
            }
            Entry e = EntryManager.get(key);
            Integer count = Integer.parseInt(words.get(word).URL) + 1;
            words.get(word).URL = count.toString();
            words.get(word).TotalCount += e.TotalCount;
            words.get(word).AsTitle += e.AsTitle;
            words.get(word).AsHeader1 += e.AsHeader1;
            words.get(word).AsHeader2 += e.AsHeader2;
            words.get(word).AsHeader3 += e.AsHeader3;
            words.get(word).AsHeader4 += e.AsHeader4;
            words.get(word).AsHeader5 += e.AsHeader5;
            words.get(word).AsHeader6 += e.AsHeader6;
        }

        int i = 0;
        for (String word : words.keySet()) {
            i++;
            System.out.print(i + ": ");
            System.out.print(word + " - ");
            System.out.print(words.get(word).URL + " ");
            System.out.print(words.get(word).TotalCount + " ");
            System.out.print(words.get(word).AsTitle + " ");
            System.out.print(words.get(word).AsHeader1 + " ");
            System.out.print(words.get(word).AsHeader2 + " ");
            System.out.print(words.get(word).AsHeader3 + " ");
            System.out.print(words.get(word).AsHeader4 + " ");
            System.out.print(words.get(word).AsHeader5 + " ");
            System.out.print(words.get(word).AsHeader6 + "\n");
        }

        System.out.println("URL COUNT: " + DocumentManager.getKeys("").size());
    }
}
