package com.mazxn.korpux.persistence;

import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

public class AsyncWriter implements Runnable {
    private static final LinkedBlockingQueue<Hashtable<String, Entry>> queue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        try {
            while (!queue.isEmpty()) {
                Hashtable<String, Entry> item = queue.take();
                for (String key : item.keySet()) {
                    EntryManager.addByWord(key, item.get(key));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Thread write(Hashtable<String, Entry> items) {
        queue.add(items);
        AsyncWriter writer = new AsyncWriter();
        Thread t = new Thread(writer);
        t.start();
        return t;
    }
}
