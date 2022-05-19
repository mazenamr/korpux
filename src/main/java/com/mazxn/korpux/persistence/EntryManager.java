package com.mazxn.korpux.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.mazxn.korpux.Constants;

public class EntryManager {
    private static final ReentrantLock mutex = new ReentrantLock();

    private static final RocksDBManager entryDBManager = new RocksDBManager(Constants.ENTRY_DB_NAME);

    public static void put(final String key, final Entry value) {
        try {
            mutex.lock();
            ObjectOutputStream out = null;
            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                out = new ObjectOutputStream(b);
                out.writeObject(value);
                entryDBManager.put(key.getBytes(), b.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            mutex.unlock();
        }
    }

    public static Entry get(final String key) {
        try {
            mutex.lock();
            ObjectInputStream in = null;
            try {
                byte[] value = entryDBManager.get(key.getBytes());
                if (value == null) {
                    return null;
                }
                in = new ObjectInputStream(new ByteArrayInputStream(value));
                return (Entry) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        } finally {
            mutex.unlock();
        }
    }

    public static void delete(final String key) {
        try {
            mutex.lock();
            Entry e = get(key);
            if (e != null) {
                entryDBManager.delete(key.getBytes());
            }
        } finally {
            mutex.unlock();
        }
    }

    public static void putByWord(final String word, final Entry entry) {
        try {
            mutex.lock();
            put(word + "-" + entry.URL, entry);
        } finally {
            mutex.unlock();
        }
    }

    public static List<Entry> getByWord(final String prefix) {
        try {
            mutex.lock();
            ObjectInputStream in = null;
            try {
                List<byte[]> values = entryDBManager.getRange(prefix.getBytes());
                List<Entry> entries = new ArrayList<>();
                for (byte[] value : values) {
                    in = new ObjectInputStream(new ByteArrayInputStream(value));
                    entries.add((Entry) in.readObject());
                }
                return entries;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        } finally {
            mutex.unlock();
        }
    }

    public static List<String> getKeys(final String prefix) {
        try {
            mutex.lock();
            List<byte[]> values = entryDBManager.getKeys(prefix.getBytes());
            List<String> keys = new ArrayList<>();
            for (byte[] value : values) {
                keys.add(new String(value));
            }
            return keys;
        } finally {
            mutex.unlock();
        }
    }
}
