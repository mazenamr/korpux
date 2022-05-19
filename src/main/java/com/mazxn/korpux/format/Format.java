package com.mazxn.korpux.format;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.mazxn.korpux.Constants;

import smile.nlp.stemmer.PorterStemmer;

public class Format {
    public static class WordFormatOptions {
        public boolean lowercase = true;
        public boolean trimWhitespace = true;
        public boolean removeSymbols = true;
        public boolean stemming = true;
        public boolean stopwords = true;
        public int minLength = 1;
        public int maxLength = 127;
        public HashSet<Character> splitOn = new HashSet<>(
                List.of(' ', '\n', '\t', '-', '_', '/', '.', ',', ';', ':', '&', '@'));
    }

    public List<String> formatWords(List<String> words, WordFormatOptions options) {
        List<String> result = new ArrayList<>();
        PorterStemmer stemmer = new PorterStemmer();

        if (options.splitOn != null && options.splitOn.size() > 0) {
            List<String> splitWords = new ArrayList<>();
            for (String word : words) {
                for (Character c : options.splitOn) {
                    String[] split = word.split(c.toString());
                    for (String s : split) {
                        if (s.length() >= options.minLength && s.length() <= options.maxLength) {
                            splitWords.add(s);
                        }
                    }
                }
            }
            words.addAll(splitWords);
        }

        for (String word : words) {
            if (options.lowercase) {
                word = word.toLowerCase();
            }
            if (options.trimWhitespace) {
                word = word.trim();
            }
            if (options.removeSymbols) {
                // word = word.replaceAll("[^a-zA-Z0-9]", "");
            }
            if (options.stemming) {
                word = stemmer.stem(word);
            }
            if (options.stopwords) {
                if (options.stemming && Constants.STOP_WORDS_STEMMED.contains(word)) {
                    continue;
                }
                if (!options.stemming && Constants.STOP_WORDS_NOT_STEMMED.contains(word)) {
                    continue;
                }
            }
            if (word.length() >= options.minLength && word.length() <= options.maxLength) {
                result.add(word);
            }
        }
        return result;
    }

    public List<String> formatWords(List<String> words) {
        return formatWords(words, new WordFormatOptions());
    }
}