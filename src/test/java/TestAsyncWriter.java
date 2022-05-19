import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Hashtable;

import com.mazxn.korpux.persistence.AsyncEntryWriter;
import com.mazxn.korpux.persistence.Entry;
import com.mazxn.korpux.persistence.EntryManager;

import org.junit.jupiter.api.Test;

public class TestAsyncWriter {
    @Test
    public void testAsyncWriter() {
        Hashtable<String, Entry> entries = new Hashtable<>();

        Entry google_1 = new Entry("https://www.google.com", 1, 1, 1, 1, 1, 1, 1, 1);
        Entry google_2 = new Entry("https://abc.xyz", 1, 1, 1, 1, 1, 1, 1, 1);
        Entry alphabet = new Entry("https://abc.xyz", 1, 1, 1, 1, 1, 1, 1, 1);
        Entry facebook = new Entry("https://www.facebook.com", 1, 1, 1, 1, 1, 1, 1, 1);
        Entry twitter_1 = new Entry("https://www.twitter.com", 0, 0, 0, 0, 0, 0, 0, 0);
        Entry twitter_2 = new Entry("https://www.twitter.com", 1, 1, 1, 1, 1, 1, 1, 1);

        entries.put("google", google_1);
        entries.put("alphabet", alphabet);
        entries.put("twitter", twitter_1);

        Thread t1 = AsyncEntryWriter.write(entries);
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(google_1, EntryManager.getByWord("google").get(0));
        assertEquals(alphabet, EntryManager.getByWord("alphabet").get(0));
        assertEquals(twitter_1, EntryManager.getByWord("twitter").get(0));

        entries.put("google", google_2);
        entries.put("facebook", facebook);
        entries.put("twitter", twitter_2);

        Thread t2 = AsyncEntryWriter.write(entries);
        try {
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(google_1, EntryManager.getByWord("google").get(1));
        assertEquals(google_2, EntryManager.getByWord("google").get(0));
        assertEquals(alphabet, EntryManager.getByWord("alphabet").get(0));
        assertEquals(facebook, EntryManager.getByWord("facebook").get(0));
        assertEquals(twitter_2, EntryManager.getByWord("twitter").get(0));

        EntryManager.delete("google-" + google_1.URL);
        EntryManager.delete("google-" + google_2.URL);
        EntryManager.delete("alphabet-" + alphabet.URL);
        EntryManager.delete("facebook-" + facebook.URL);
        EntryManager.delete("twitter-" + twitter_2.URL);

        assertEquals(0, EntryManager.getByWord("google").size());
        assertEquals(0, EntryManager.getByWord("alphabet").size());
        assertEquals(0, EntryManager.getByWord("facebook").size());
        assertEquals(0, EntryManager.getByWord("twitter").size());
    }
}
