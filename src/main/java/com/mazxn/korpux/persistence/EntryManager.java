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
    private static final RocksDBManager countDBManager = new RocksDBManager(Constants.COUNT_DB_NAME);

    public static void putByKey(final String key, final Entry value) {
        try {
            mutex.lock();
            ObjectOutputStream out = null;
            try {
                Boolean found = getByKey(key) != null;
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                out = new ObjectOutputStream(b);
                out.writeObject(value);
                entryDBManager.put(key.getBytes(), b.toByteArray());
                if (!found) {
                    String url = value.URL;
                    int count = byteToInt(countDBManager.get(url.getBytes()));
                    countDBManager.put(url.getBytes(), intToByte(count + 1));
                }
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

    public static Entry getByKey(final String key) {
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

    public static void deleteByKey(final String key) {
        try {
            mutex.lock();
            Entry e = getByKey(key);
            if (e != null) {
                String url = e.URL;
                int count = byteToInt(countDBManager.get(url.getBytes()));
                if (count > 1) {
                    countDBManager.put(url.getBytes(), intToByte(count - 1));
                } else {
                    countDBManager.delete(url.getBytes());
                }
                entryDBManager.delete(key.getBytes());
            }
        } finally {
            mutex.unlock();
        }
    }

    public static void addByWord(final String word, final Entry entry) {
        try {
            mutex.lock();
            putByKey(word + "-" + entry.URL, entry);
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

    public static int getURLCount(final String url) {
        try {
            mutex.lock();
            return byteToInt(countDBManager.get(url.getBytes()));
        } finally {
            mutex.unlock();
        }
    }

    public static void resetURLCount(final String url) {
        try {
            mutex.lock();
            countDBManager.put(url.getBytes(), intToByte(0));
        } finally {
            mutex.unlock();
        }
    }

    private static byte[] intToByte(int value) {
        return new byte[] {
                (byte) ((value >> 24) & 0xff),
                (byte) ((value >> 16) & 0xff),
                (byte) ((value >> 8) & 0xff),
                (byte) ((value >> 0) & 0xff),
        };
    }

    private static int byteToInt(byte[] value) {
        if (value == null || value.length != 4) {
            return 0x0;
        }
        return (int) ((0xff & value[0]) << 24 |
                (0xff & value[1]) << 16 |
                (0xff & value[2]) << 8 |
                (0xff & value[3]) << 0);
    }
}
