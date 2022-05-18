import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.mazxn.korpux.persistence.Entry;
import com.mazxn.korpux.persistence.EntryManager;

import org.junit.jupiter.api.Test;

public class TestEntryManager {

    @Test
    public void testEntryManager() {
        Entry google_1 = new Entry("https://www.google.com", 1, 1, 1, 1, 1, 1, 1, 1);
        Entry google_2 = new Entry("https://abc.xyz", 1, 1, 1, 1, 1, 1, 1, 1);
        Entry alphabet = new Entry("https://abc.xyz", 1, 1, 1, 1, 1, 1, 1, 1);
        Entry facebook = new Entry("https://www.facebook.com", 1, 1, 1, 1, 1, 1, 1, 1);
        Entry twitter_1 = new Entry("https://www.twitter.com", 0, 0, 0, 0, 0, 0, 0, 0);
        Entry twitter_2 = new Entry("https://www.twitter.com", 1, 1, 1, 1, 1, 1, 1, 1);

        EntryManager.add("google-" + google_1.URL, google_1);
        EntryManager.add("google-" + google_2.URL, google_2);
        EntryManager.add("alphabet-" + alphabet.URL, alphabet);
        EntryManager.add("facebook-" + facebook.URL, facebook);
        EntryManager.add("twitter-" + twitter_1.URL, twitter_1);
        EntryManager.add("twitter-" + twitter_2.URL, twitter_2);

        assertEquals(google_1, EntryManager.get("google-https://www.google.com"));
        assertEquals(google_2, EntryManager.get("google-https://abc.xyz"));
        assertEquals(alphabet, EntryManager.get("alphabet-https://abc.xyz"));
        assertEquals(facebook, EntryManager.get("facebook-https://www.facebook.com"));
        assertEquals(twitter_2, EntryManager.get("twitter-https://www.twitter.com"));

        List<String> keys = EntryManager.getKeys("");

        assertEquals(5, keys.size());
        assertTrue(keys.contains("google-https://www.google.com"));
        assertTrue(keys.contains("google-https://abc.xyz"));
        assertTrue(keys.contains("alphabet-https://abc.xyz"));
        assertTrue(keys.contains("facebook-https://www.facebook.com"));
        assertTrue(keys.contains("twitter-https://www.twitter.com"));

        List<Entry> googleResult = EntryManager.getRange("google");
        assertEquals(2, googleResult.size());
        assertTrue(googleResult.contains(google_1));
        assertTrue(googleResult.contains(google_2));

        List<Entry> alphabetResult = EntryManager.getRange("alphabet");
        assertEquals(1, alphabetResult.size());
        assertTrue(alphabetResult.contains(alphabet));

        List<Entry> facebookResult = EntryManager.getRange("facebook");
        assertEquals(1, facebookResult.size());
        assertTrue(facebookResult.contains(facebook));

        List<Entry> twitterResult = EntryManager.getRange("twitter");
        assertEquals(1, twitterResult.size());
        assertTrue(twitterResult.contains(twitter_2));

        assertEquals(1, EntryManager.getCount("https://www.google.com"));
        assertEquals(2, EntryManager.getCount("https://abc.xyz"));
        assertEquals(1, EntryManager.getCount("https://www.facebook.com"));
        assertEquals(1, EntryManager.getCount("https://www.twitter.com"));

        EntryManager.remove("google-" + google_1.URL);
        EntryManager.remove("google-" + google_2.URL);
        EntryManager.remove("alphabet-" + alphabet.URL);
        EntryManager.remove("facebook-" + facebook.URL);
        EntryManager.remove("twitter-" + twitter_2.URL);

        googleResult = EntryManager.getRange("google");
        alphabetResult = EntryManager.getRange("alphabet");
        facebookResult = EntryManager.getRange("facebook");
        twitterResult = EntryManager.getRange("twitter");

        assertEquals(0, googleResult.size());
        assertEquals(0, alphabetResult.size());
        assertEquals(0, facebookResult.size());
        assertEquals(0, twitterResult.size());

        assertEquals(0, EntryManager.getCount("https://www.google.com"));
        assertEquals(0, EntryManager.getCount("https://abc.xyz"));
        assertEquals(0, EntryManager.getCount("https://www.facebook.com"));
        assertEquals(0, EntryManager.getCount("https://www.twitter.com"));
    }
}
