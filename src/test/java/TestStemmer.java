import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mazxn.korpux.Constants;

import org.junit.jupiter.api.Test;

import smile.nlp.stemmer.PorterStemmer;

public class TestStemmer {
    @Test
    public void testStemmer() {
        PorterStemmer stemmer = new PorterStemmer();
        int result = 0;
        for (String word : Constants.STOP_WORDS_NOT_STEMMED) {
            if (!word.equals(stemmer.stem(word))) {
                result += 1;
                System.out.println(word + " -> " + stemmer.stem(word));
            }
        }
        assertEquals(46, result);
    }
}
