package com.mazxn.korpux.formatter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.mazxn.korpux.persistence.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Parser {
    public static Hashtable<String, Entry> parse(String html, String url) {
        Hashtable<String, Entry> items = new Hashtable<>();
        Document document = Jsoup.parse(html.toLowerCase(), url);
        for (Element e : document.getAllElements()) {
            if (e.tagName().equals("script")) {
                continue;
            }
            if (e.tagName().equals("style")) {
                continue;
            }
            if (e.tagName().equals("link")) {
                continue;
            }
            if (e.tagName().equals("meta")) {
                continue;
            }
            List<String> ownText = new ArrayList<>();
            for (String w : e.ownText().split(" ")) {
                if (w.length() > 1) {
                    ownText.add(w);
                }
            }

            ownText = new Formatter().formatWords(ownText);

            for (String word : ownText) {
                if (!items.containsKey(word)) {
                    items.put(word, new Entry(url));
                }
                items.get(word).TotalCount += 1;
            }

            List<String> allText = new ArrayList<>();
            for (String w : e.ownText().split(" ")) {
                if (w.length() > 1) {
                    allText.add(w);
                }
            }
            allText = new Formatter().formatWords(allText);

            for (String word : allText) {
                if (!items.containsKey(word)) {
                    items.put(word, new Entry(url));
                }
                items.get(word).AsTitle += e.tagName().equals("title") ? 1 : 0;
                items.get(word).AsHeader1 += e.tagName().equals("h1") ? 1 : 0;
                items.get(word).AsHeader2 += e.tagName().equals("h2") ? 1 : 0;
                items.get(word).AsHeader3 += e.tagName().equals("h3") ? 1 : 0;
                items.get(word).AsHeader4 += e.tagName().equals("h4") ? 1 : 0;
                items.get(word).AsHeader5 += e.tagName().equals("h5") ? 1 : 0;
                items.get(word).AsHeader6 += e.tagName().equals("h6") ? 1 : 0;
            }
        }

        return items;
    }
}
